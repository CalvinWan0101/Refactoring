package tw.teddysoft.ezdoc.report.readme.visitor;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import tw.teddysoft.ezdoc.annotation.Usage;
import tw.teddysoft.ezdoc.report.readme.PlaceholderLookupTable;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.regex.Pattern;

public class UsageVisitor extends AbstractEzDocAnnotationVisitor {

    public UsageVisitor(PlaceholderLookupTable placeholderLookupTable,
                        Set<Class<? extends Annotation>> annotationsToBeErased) {
        super(placeholderLookupTable, annotationsToBeErased);
    }

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return Usage.class;
    }

    @Override
    protected String buildReplacingValue(NormalAnnotationExpr annotationExpr) {
        StringBuilder sb = new StringBuilder();
        String processedUsageCodeSnippet = eraseAnnotationsInNode(annotationExpr.getParentNode().get());

        int indent = calculateIndent(annotationExpr.getParentNode().get().getTokenRange().get());
        processedUsageCodeSnippet = addIndent(processedUsageCodeSnippet, indent);

        sb.append(getAnnotationField(annotationExpr, "value"))
                .append("\n```java\n")
                .append(processedUsageCodeSnippet)
                .append("\n```\n");

        return sb.toString();
    }

    @Override
    protected String buildPlaceholder(NormalAnnotationExpr annotationExpr) {
        StringBuilder sb = new StringBuilder();
        sb.append("@")
                .append(Usage.class.getSimpleName())
                .append("(")
                .append(getAnnotationField(annotationExpr, "targetClass"))
                .append("#")
                .append(((NodeWithSimpleName<?>) annotationExpr.getParentNode().get()).getNameAsString())
                .append(")");
        return sb.toString();
    }

    private String eraseAnnotationsInNode(Node node) {

        String codeAnnotatedByUsage = node.getTokenRange().get().toString();
        for (var erasedAnnotation : annotationsToBeErased) {
            switch (node) {
                case ClassOrInterfaceDeclaration clsDeclaration when
                        clsDeclaration.isAnnotationPresent(erasedAnnotation.getSimpleName()) -> {

                    String codeSnippetToBeErased = clsDeclaration.getAnnotationByClass(erasedAnnotation).get().getTokenRange().get().toString();
                    codeAnnotatedByUsage = erase(codeAnnotatedByUsage, codeSnippetToBeErased);

                    for (Node childNode : clsDeclaration.getChildNodes()) {
                        String replacedNodeString = eraseAnnotationsInNode(childNode);
                        if (!replacedNodeString.isEmpty()) {
                            codeAnnotatedByUsage = codeAnnotatedByUsage.replace(childNode.getTokenRange().map(TokenRange::toString).orElse(""), replacedNodeString);
                        }
                    }
                }
                case MethodDeclaration methodDeclaration when
                        methodDeclaration.isAnnotationPresent(erasedAnnotation.getSimpleName()) -> {

                    String replacingText = methodDeclaration.getAnnotationByClass(erasedAnnotation).get().getTokenRange().get().toString();
                    codeAnnotatedByUsage = erase(codeAnnotatedByUsage, replacingText);
                }
                default -> {
                }
            }
        }

        return codeAnnotatedByUsage;
    }


    private String erase(String originalString, String stringToBeErased) {
        String escaped = Pattern.quote(stringToBeErased);
        return originalString.replaceFirst(escaped + "\\s*", "");
    }

    private static String addIndent(String codeSnippet, int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append(" ");
        }
        sb.append(codeSnippet);
        return sb.toString();
    }

    private static int calculateIndent(TokenRange tokenRange) {
        return tokenRange.getBegin().getRange().get().begin.column - 1;
    }
}