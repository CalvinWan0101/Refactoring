package tw.teddysoft.ezdoc.report.glossary;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlossaryMarkdownGeneratorTest {

    @Test
    public void generate_glossary_for_unique_definition() {
        Set<String> set = new HashSet<>();
        set.add("src/test/java/tw/teddysoft/ezdoc/report/glossary/testdata/RuleDefinition.java");
        set.add("src/test/java/tw/teddysoft/ezdoc/report/glossary/testdata/FeatureDefinition.java");

        String outputPath = "./target/glossary.md";
        GlossaryMarkdownGenerator glossaryMarkdownGenerator = new GlossaryMarkdownGenerator();
        glossaryMarkdownGenerator.execute(set, outputPath);


        String actualResult = readFile(outputPath);
        String expectedResult = """
                Glossary
                ========
                                
                | Term                                       | Definition                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
                | ------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
                | Rule                                       | A `Rule` represents the business logic of a feature. A feature can be broken down into multiple rules to aid in scenario illustration. Each rule, in turn, encompasses a collection of related scenarios that pertain to the feature's business logic. The use of the `Rule` keyword is optional. If a feature does not contain a rule, all of its scenarios will be categorized under a predefined rule named "default" in ezSpec. A rule has the capability to define a Background that can be shared among scenarios associated with the same rule. |
                | tw.teddysoft.ezdoc.report.glossary.Feature | The Feature class.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |""";
        assertEquals(normalizeNewlines(expectedResult), normalizeNewlines(actualResult));
    }

    @Test
    public void generate_glossary_for_duplicate_definition() {
        Set<String> set = new LinkedHashSet<>();
        set.add("src/test/java/tw/teddysoft/ezdoc/report/glossary/testdata/FeatureDefinition2.java");
        set.add("src/test/java/tw/teddysoft/ezdoc/report/glossary/testdata/FeatureDefinition3.java");

        String outputPath = "./target/glossary.md";
        GlossaryMarkdownGenerator glossaryMarkdownGenerator = new GlossaryMarkdownGenerator();
        glossaryMarkdownGenerator.execute(set, outputPath);

        String actualResult = readFile(outputPath);
        String expectedResult = """
                Glossary
                ========
                                
                | Term        | Definition                           |
                | ----------- | ------------------------------------ |
                | Feature     | The second Feature class definition. |
                | Feature (1) | The third Feature class definition.  |""";
        assertEquals(normalizeNewlines(expectedResult), normalizeNewlines(actualResult));
    }
    private String readFile(String filePath) {
        Path path = Paths.get(filePath);
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String normalizeNewlines(String input) {
        // 將換行符號統一轉換為 Unix 格式（\n）
        return input.replaceAll("\r\n", "\n").replaceAll("\r", "\n");
    }
}
