package tw.teddysoft.ezdoc.report.readme;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.String.format;

public class ReadmeMarkdownGenerator {

    private final Set<Class<?>> ezDocAnnotationVisitors;

    private final Set<Class<? extends Annotation>> erasedAnnotations;

    public ReadmeMarkdownGenerator(Set<Class<?>> ezDocAnnotationVisitors, Class<? extends Annotation> ... erasedAnnotations) {
        this.ezDocAnnotationVisitors = ezDocAnnotationVisitors;
        this.erasedAnnotations = new HashSet<>(Arrays.asList(erasedAnnotations));
    }

    public void execute(String templatePath, String outputPath, Set<String> testFilePaths) {
        String markdownTemplate = readMarkdownTemplate(templatePath);
        List<EzDocCodeFence> ezDocCodeFences = EzDocCodeFenceParser.parse(markdownTemplate);
        PlaceholderLookupTable placeholderLookupTable = buildPlaceholderLookupTable(testFilePaths);
        markdownTemplate = resolvePlaceholdersInMarkdownTemplate(markdownTemplate, ezDocCodeFences, placeholderLookupTable);
        writeReadMe(markdownTemplate, outputPath);
    }

    private PlaceholderLookupTable buildPlaceholderLookupTable(Set<String> testFilePaths) {
        PlaceholderLookupTable placeholderLookupTable = new PlaceholderLookupTable();
        List<VoidVisitorAdapter> annotationVisitors = createVisitorInstances(placeholderLookupTable);
        for (var visitor: annotationVisitors) {
            for (String path : testFilePaths) {
                visitJavaSourceCode(visitor, path);
            }
        }

        return placeholderLookupTable;
    }

    private static void visitJavaSourceCode(VoidVisitorAdapter visitor, String path) {
        try {
            String sourceCode = new String(Files.readAllBytes(Paths.get(path)));
            StaticJavaParser.setConfiguration(new ParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17));
            CompilationUnit cu = StaticJavaParser.parse(sourceCode);
            cu.accept(visitor, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private List<VoidVisitorAdapter> createVisitorInstances(PlaceholderLookupTable placeholderLookupTable) {
        List<VoidVisitorAdapter> annotationVisitors = new ArrayList<>();
        for (Class clazz: ezDocAnnotationVisitors) {
            try {
                Constructor<?> constructor = Class.forName(clazz.getName()).getConstructor(PlaceholderLookupTable.class, Set.class);
                VoidVisitorAdapter<Void> visitor = (VoidVisitorAdapter<Void>)constructor.newInstance(placeholderLookupTable, erasedAnnotations);
                annotationVisitors.add(visitor);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return annotationVisitors;
    }

    private static String resolvePlaceholdersInMarkdownTemplate(String markdownTemplate,
                                                                List<EzDocCodeFence> ezDocCodeFences,
                                                                PlaceholderLookupTable placeholderLookupTable) {
        String resolvedMarkdown = markdownTemplate;
        for (var ezDocCodeFence : ezDocCodeFences) {
            String resolvedCodeBlock = getResolvedCodeBlock(placeholderLookupTable, ezDocCodeFence, ezDocCodeFence.codeBlock());
            resolvedMarkdown = replaceCodeFenceWithResolvedCodeBlock(resolvedMarkdown, ezDocCodeFence, resolvedCodeBlock);
        }
        return resolvedMarkdown;
    }

    private static String replaceCodeFenceWithResolvedCodeBlock(String markdownTemplate, EzDocCodeFence ezDocCodeFence, String resolvedCodeBlock) {
        return markdownTemplate.replace(ezDocCodeFence.codeFence(), resolvedCodeBlock);
    }

    private static String getResolvedCodeBlock(PlaceholderLookupTable placeholderLookupTable, EzDocCodeFence ezDocCodeFence, String codeBlock) {
        String resolvedCodeBlock = codeBlock;
        for (var placeholder : ezDocCodeFence.placeHolders()) {
            if(!placeholderLookupTable.containsKey(placeholder.withoutSpace())) {
                throw new RuntimeException(format("Placeholder '%s' is not exist.", placeholder.value()));
            }
            resolvedCodeBlock = resolvedCodeBlock.replace(placeholder.value(), placeholderLookupTable.get(placeholder.withoutSpace()));
        }
        return resolvedCodeBlock;
    }

    private String readMarkdownTemplate(String filePath) {
        Path path = Paths.get(filePath);
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeReadMe(String readMe, String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.append(readMe);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

