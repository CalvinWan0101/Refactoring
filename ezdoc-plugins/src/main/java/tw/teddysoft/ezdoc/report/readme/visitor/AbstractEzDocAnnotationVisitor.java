package tw.teddysoft.ezdoc.report.readme.visitor;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import tw.teddysoft.ezdoc.report.readme.PlaceholderLookupTable;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractEzDocAnnotationVisitor extends VoidVisitorAdapter<Void> {
    protected final PlaceholderLookupTable placeholderLookupTable;
    protected final Set<Class<? extends Annotation>> annotationsToBeErased;

    public AbstractEzDocAnnotationVisitor(PlaceholderLookupTable placeholderLookupTable,
                                          Set<Class<? extends Annotation>> annotationsToBeErased) {
        Objects.requireNonNull(placeholderLookupTable);
        Objects.requireNonNull(annotationsToBeErased);
        this.placeholderLookupTable = placeholderLookupTable;
        this.annotationsToBeErased = annotationsToBeErased;
    }

    @Override
    public final void visit(NormalAnnotationExpr annotationExpr, Void arg) {
        super.visit(annotationExpr, arg);

        if (!annotationExpr.getName().getIdentifier().equals(getAnnotationClass().getSimpleName())) return;
        if (annotationExpr.getParentNode().isEmpty()) return;

        Node parentNode = annotationExpr.getParentNode().get();
        if (!(parentNode instanceof NodeWithSimpleName<?>)) return;

        placeholderLookupTable.put(buildPlaceholder(annotationExpr),
                buildReplacingValue(annotationExpr));
    }

    protected abstract Class<? extends Annotation> getAnnotationClass();

    protected abstract String buildPlaceholder(NormalAnnotationExpr annotationExpr);

    protected abstract String buildReplacingValue(NormalAnnotationExpr annotationExpr);

    protected final String getAnnotationField(AnnotationExpr annotation, String annotationFieldName) {
        for (MemberValuePair param : annotation.toNormalAnnotationExpr().get().getPairs()) {
            if (param.getNameAsString().equals(annotationFieldName)) {
                switch (param.getValue()) {
                    case StringLiteralExpr stringLiteralExpr -> {
                        return stringLiteralExpr.asString();
                    }
                    case TextBlockLiteralExpr textBlockLiteralExpr -> {
                        return textBlockLiteralExpr.asString();
                    }
                    case ClassExpr classExpr -> {
                        return classExpr.toString();
                    }
                    default -> {
                        return "";
                    }
                }
            }
        }
        return "";
    }
}
