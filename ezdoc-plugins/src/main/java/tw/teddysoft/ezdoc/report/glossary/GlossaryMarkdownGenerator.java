package tw.teddysoft.ezdoc.report.glossary;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import tw.teddysoft.ezdoc.annotation.Definition;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GlossaryMarkdownGenerator {

    public void execute(Set<String> targetPath, String path) {
        Map<String, String> glossaryMap = buildGlossaryMap(targetPath);
        String glossaryMarkdown = buildGlossaryMarkdown(glossaryMap);
        writeReport(glossaryMarkdown, path);
    }

    private String buildGlossaryMarkdown(Map<String, String> glossaryMap) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(new Heading("Glossary", 1)).append("\n\n");
        stringBuilder.append(buildMarkdownTableInAlphabeticOrder(glossaryMap));
        return stringBuilder.toString();
    }

    private Table buildMarkdownTableInAlphabeticOrder(Map<String, String> glossaryMap) {
        List<String> sortedTerms = glossaryMap.entrySet().stream().map((entry)-> entry.getKey()).sorted().toList();
        Table.Builder tableBuilder = new Table.Builder().addRow("Term", "Definition");

        for (var key: sortedTerms) {
            tableBuilder.addRow(key, convertToMarkdownLineBreaking(glossaryMap.get(key)));
        }
        Table glossaryMarkdownTable = tableBuilder.build();
        return glossaryMarkdownTable;
    }

    private Map<String, String> buildGlossaryMap(Set<String> testFilePaths) {
        Map<String, String> glossaryMap = new HashMap<>();
        for(var path: testFilePaths) {
            visitJavaSourceCode(path, glossaryMap);
        }
        return glossaryMap;
    }

    private static void visitJavaSourceCode(String path, Map<String, String> glossaryMap) {
        try {
            String sourceCode = new String(Files.readAllBytes(Paths.get(path)));
            StaticJavaParser.setConfiguration(new ParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17));
            CompilationUnit cu = StaticJavaParser.parse(sourceCode);
            DefinitionVisitor definitionVisitor = new DefinitionVisitor(glossaryMap);
            cu.accept(definitionVisitor, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeReport(String glossary, String path) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.append(glossary);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String convertToMarkdownLineBreaking(String definition) {
        return definition.replace("\r\n", "<br/><br/>")
                .replace("\r", "<br/><br/>")
                .replace("\n", "<br/><br/>");
    }

    public static class DefinitionVisitor extends VoidVisitorAdapter<Void> {

        private final Map<String, String> glossaryMap;

        public DefinitionVisitor(Map<String, String> glossaryMap) {
            this.glossaryMap = glossaryMap;
        }

        @Override
        public final void visit(NormalAnnotationExpr annotationExpr, Void arg) {
            super.visit(annotationExpr, arg);

            if (!annotationExpr.getName().getIdentifier().equals(Definition.class.getSimpleName())) return;
            if (annotationExpr.getParentNode().isEmpty()) return;

            Node parentNode = annotationExpr.getParentNode().get();
            if (!(parentNode instanceof NodeWithSimpleName<?>)) return;
            String targetClassSimpleName = removePostfix(getAnnotationField(annotationExpr, "targetClass"));
            String key = getKey(glossaryMap, targetClassSimpleName, 0);
            glossaryMap.put(key, getAnnotationField(annotationExpr, "brief"));
        }

        private String removePostfix(String key) {
            int postfixIndex = key.lastIndexOf(".class");
            return key.substring(0, postfixIndex);
        }

        private String getKey(Map<String, String> glossaryMap, String key, int index) {
            String newKey = key;
            if (index != 0) {
                newKey = key + " (" + index + ")";
            }
            if (!glossaryMap.containsKey(newKey)) return newKey;
            return getKey(glossaryMap, key, index + 1);
        }

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
}
