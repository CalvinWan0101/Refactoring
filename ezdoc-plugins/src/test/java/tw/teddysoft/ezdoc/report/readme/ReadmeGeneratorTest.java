package tw.teddysoft.ezdoc.report.readme;

import org.junit.jupiter.api.Test;
import tw.teddysoft.ezdoc.annotation.Definition;
import tw.teddysoft.ezdoc.annotation.Usage;
import tw.teddysoft.ezdoc.report.readme.visitor.DefinitionVisitor;
import tw.teddysoft.ezdoc.report.readme.visitor.UsageVisitor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReadmeGeneratorTest {

    @Test
    public void replace_definition_on_class() {
        Set<Class<?>> visitors = new HashSet<>();
        visitors.add(DefinitionVisitor.class);

        Set<String> ezDocMetadataFiles = new HashSet<>();
        ezDocMetadataFiles.add("src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/DefinitionOnClass.java");

        String outputPath = "./target/README.md";

        ReadmeMarkdownGenerator readMeMarkdownGenerator = new ReadmeMarkdownGenerator(visitors, Usage.class, Definition.class);
        readMeMarkdownGenerator.execute(
                "src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/DefinitionOnClass.md",
                outputPath,
                ezDocMetadataFiles);

        String actualResult = readFile(outputPath);
        String expectedResult = """
                A `Rule` represents the business logic of a feature. A feature can be broken down into multiple rules to aid in scenario illustration. Each rule, in turn, encompasses a collection of related scenarios that pertain to the feature's business logic. The use of the `Rule` keyword is optional. If a feature does not contain a rule, all of its scenarios will be categorized under a predefined rule named "default" in ezSpec. A rule has the capability to define a Background that can be shared among scenarios associated with the same rule.

                - To create a rule in a feature, invoke `Feature::NewRule(String ruleName)`.
                - There are three ways to categorize a scenario to a rule:
                  1. invoke `Scenario::withRule(String ruleName)`,
                  2. use `@EzScenario(rule = ruleName)`, and
                  3. annotate with `@EzRule(ruleName)`.

                If no rule is specified, the scenario belongs to the default rule.
                
                """;
        assertEquals(normalizeNewlines(expectedResult), normalizeNewlines(actualResult));
    }

    @Test
    public void replace_usage_on_class() {
        Set<Class<?>> visitors = new HashSet<>();
        visitors.add(UsageVisitor.class);

        Set<String> ezDocMetadataFiles = new HashSet<>();
        ezDocMetadataFiles.add("src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/UsageOnClass.java");

        String outputPath = "./target/README.md";

        ReadmeMarkdownGenerator readMeMarkdownGenerator = new ReadmeMarkdownGenerator(visitors, Usage.class, Definition.class);
        readMeMarkdownGenerator.execute(
                "src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/UsageOnClass.md",
                outputPath,
                ezDocMetadataFiles);

        String actualResult = readFile(outputPath);
        String expectedResult = """
                Rule can be used with a feature.
                ```java
                @EzFeature
                public class UsageOnClass {
                    static Feature feature;
                    static final String TOTAL_PRICE_RULE = "Total price includes tax";
                                
                    @BeforeAll
                    public static void beforeAll() {
                        feature = Feature.New("Scenario example");
                        feature.NewRule(TOTAL_PRICE_RULE);
                    }
                                
                    @EzScenario
                    public void scenario_example_with_user_created_rule_1() {
                        feature.newScenario().withRule(TOTAL_PRICE_RULE)
                                .Execute();
                    }
                }
                ```
                                
                """;
        assertEquals(normalizeNewlines(expectedResult), normalizeNewlines(actualResult));
    }

    @Test
    public void replace_usage_on_method() {
        Set<Class<?>> visitors = new HashSet<>();
        visitors.add(UsageVisitor.class);

        Set<String> ezDocMetadataFiles = new HashSet<>();
        ezDocMetadataFiles.add("src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/UsageOnMethod.java");

        String outputPath = "./target/README.md";

        ReadmeMarkdownGenerator readMeMarkdownGenerator = new ReadmeMarkdownGenerator(visitors, Usage.class, Definition.class);
        readMeMarkdownGenerator.execute(
                "src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/UsageOnMethod.md",
                outputPath,
                ezDocMetadataFiles);

        String actualResult = readFile(outputPath);
        String expectedResult = """
                Rule can be used with a scenario.
                ```java
                    @EzScenario
                    public void scenario_example_with_user_created_rule_1() {
                        feature.newScenario().withRule(TOTAL_PRICE_RULE)
                                .Execute();
                    }
                ```
                
                """;
        assertEquals(normalizeNewlines(expectedResult), normalizeNewlines(actualResult));

    }

    @Test
    public void replace_definition_and_usage_on_class() {
        Set<Class<?>> visitors = new HashSet<>();
        visitors.add(UsageVisitor.class);
        visitors.add(DefinitionVisitor.class);

        Set<String> ezDocMetadataFiles = new HashSet<>();
        ezDocMetadataFiles.add("src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/DefinitionAndUsageOnClass.java");

        String outputPath = "./target/README.md";

        ReadmeMarkdownGenerator readMeMarkdownGenerator = new ReadmeMarkdownGenerator(visitors, Usage.class, Definition.class);
        readMeMarkdownGenerator.execute(
                "src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/DefinitionAndUsageOnClass.md",
                outputPath,
                ezDocMetadataFiles);

        String actualResult = readFile(outputPath);
        String expectedResult = """
                A `Rule` represents the business logic of a feature. A feature can be broken down into multiple rules to aid in scenario illustration. Each rule, in turn, encompasses a collection of related scenarios that pertain to the feature's business logic. The use of the `Rule` keyword is optional. If a feature does not contain a rule, all of its scenarios will be categorized under a predefined rule named "default" in ezSpec. A rule has the capability to define a Background that can be shared among scenarios associated with the same rule.

                - To create a rule in a feature, invoke `Feature::NewRule(String ruleName)`.
                - There are three ways to categorize a scenario to a rule:
                  1. invoke `Scenario::withRule(String ruleName)`,
                  2. use `@EzScenario(rule = ruleName)`, and
                  3. annotate with `@EzRule(ruleName)`.

                If no rule is specified, the scenario belongs to the default rule.
                                
                                
                Rule can be used with a feature.
                ```java
                @EzFeature
                public class DefinitionAndUsageOnClass {
                    static Feature feature;
                                
                    @BeforeAll
                    public static void beforeAll() {
                                
                        feature = Feature.New("Leave Team Use Case", ""\"
                                    A team member can leave a team at anytime.
                                    However, there must be at least one team admin in the team.
                                    That is, the last team admin cannot leave the team.
                                ""\");
                                
                        feature.NewRule("5% tax fee");
                        feature.NewRule("Tax free");
                    }
                                
                    @Test
                    public void a_feature_has_a_default_rule() {
                        assertNotNull(feature.getDefaultRule());
                    }
                }
                ```
                                
                """;
        assertEquals(normalizeNewlines(expectedResult), normalizeNewlines(actualResult));

    }

    @Test
    public void replace_definition_on_inner_class() {
        Set<Class<?>> visitors = new HashSet<>();
        visitors.add(DefinitionVisitor.class);

        Set<String> ezDocMetadataFiles = new HashSet<>();
        ezDocMetadataFiles.add("src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/DefinitionOnInnerClass.java");

        String outputPath = "./target/README.md";

        ReadmeMarkdownGenerator readMeMarkdownGenerator = new ReadmeMarkdownGenerator(visitors, Usage.class, Definition.class);
        readMeMarkdownGenerator.execute(
                "src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/DefinitionOnInnerClass.md",
                outputPath,
                ezDocMetadataFiles);

        String actualResult = readFile(outputPath);
        String expectedResult = """
                A `Rule` represents the business logic of a feature. A feature can be broken down into multiple rules to aid in scenario illustration. Each rule, in turn, encompasses a collection of related scenarios that pertain to the feature's business logic. The use of the `Rule` keyword is optional. If a feature does not contain a rule, all of its scenarios will be categorized under a predefined rule named "default" in ezSpec. A rule has the capability to define a Background that can be shared among scenarios associated with the same rule.

                - To create a rule in a feature, invoke `Feature::NewRule(String ruleName)`.
                - There are three ways to categorize a scenario to a rule:
                  1. invoke `Scenario::withRule(String ruleName)`,
                  2. use `@EzScenario(rule = ruleName)`, and
                  3. annotate with `@EzRule(ruleName)`.

                If no rule is specified, the scenario belongs to the default rule.
                
                """;
        assertEquals(normalizeNewlines(expectedResult), normalizeNewlines(actualResult));
    }

    @Test
    public void replace_usage_on_inner_class() {
        Set<Class<?>> visitors = new HashSet<>();
        visitors.add(UsageVisitor.class);

        Set<String> ezDocMetadataFiles = new HashSet<>();
        ezDocMetadataFiles.add("src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/UsageOnInnerClass.java");

        String outputPath = "./target/README.md";

        ReadmeMarkdownGenerator readMeMarkdownGenerator = new ReadmeMarkdownGenerator(visitors, Usage.class, Definition.class);
        readMeMarkdownGenerator.execute(
                "src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/UsageOnInnerClass.md",
                outputPath,
                ezDocMetadataFiles);

        String actualResult = readFile(outputPath);
        String expectedResult = """
                Rule can be used with a feature.
                ```java
                    public class InnerClass {
                        static Feature feature;
                        static final String TOTAL_PRICE_RULE = "Total price includes tax";
                                
                        @BeforeAll
                        public static void beforeAll() {
                            feature = Feature.New("Scenario example");
                            feature.NewRule(TOTAL_PRICE_RULE);
                        }
                       
                        @EzScenario
                        public void scenario_example_with_user_created_rule_1() {
                            feature.newScenario().withRule(TOTAL_PRICE_RULE)
                                    .Execute();
                        }
                                
                    }
                ```
                                
                """;
        assertEquals(normalizeNewlines(expectedResult), normalizeNewlines(actualResult));
    }

    @Test
    public void replace_usage_on_method_of_inner_class() {
        Set<Class<?>> visitors = new HashSet<>();
        visitors.add(UsageVisitor.class);

        Set<String> ezDocMetadataFiles = new HashSet<>();
        ezDocMetadataFiles.add("src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/UsageOnMethodOfInnerClass.java");

        String outputPath = "./target/README.md";

        ReadmeMarkdownGenerator readMeMarkdownGenerator = new ReadmeMarkdownGenerator(visitors, Usage.class, Definition.class);
        readMeMarkdownGenerator.execute(
                "src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/UsageOnMethodOfInnerClass.md",
                outputPath,
                ezDocMetadataFiles);

        String actualResult = readFile(outputPath);
        String expectedResult = """
                Rule can be used with a scenario.
                ```java
                        @EzScenario
                        public void scenario_example_with_user_created_rule_1() {
                            feature.newScenario().withRule(TOTAL_PRICE_RULE)
                                    .Execute();
                        }
                ```
                
                """;
        assertEquals(normalizeNewlines(expectedResult), normalizeNewlines(actualResult));
    }

    @Test
    public void replace_usage_at_all_levels() {
        Set<Class<?>> visitors = new HashSet<>();
        visitors.add(UsageVisitor.class);

        Set<String> ezDocMetadataFiles = new HashSet<>();
        ezDocMetadataFiles.add("src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/UsageAtAllLevels.java");

        String outputPath = "./target/README.md";

        ReadmeMarkdownGenerator readMeMarkdownGenerator = new ReadmeMarkdownGenerator(visitors, Usage.class, Definition.class);
        readMeMarkdownGenerator.execute(
                "src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/UsageAtAllLevels.md",
                outputPath,
                ezDocMetadataFiles);

        String actualResult = readFile(outputPath);
        String expectedResult = """
                Rule can be used with a feature.
                ```java
                @EzFeature
                public class UsageAtAllLevels {
                    static Feature feature;
                    static final String TOTAL_PRICE_RULE = "Total price includes tax";
                                
                    @BeforeAll
                    public static void beforeAll() {
                        feature = Feature.New("Scenario example");
                        feature.NewRule(TOTAL_PRICE_RULE);
                    }
                    
                    public class InnerClass {
                                
                        @EzScenario
                        public void scenario_example_with_user_created_rule_1() {
                            feature.newScenario().withRule(TOTAL_PRICE_RULE)
                                    .Execute();
                        }
                                
                        public class InnerInnerClass {
                            public void method_in_inner_inner_class() {
                                feature.newScenario().withRule(TOTAL_PRICE_RULE)
                                        .Execute();
                            }
                
                        }
                                
                    }
                }
                ```
                                
                """;
        assertEquals(normalizeNewlines(expectedResult), normalizeNewlines(actualResult));
    }

    @Test
    public void replace_usage_with_correct_indent() {
        Set<Class<?>> visitors = new HashSet<>();
        visitors.add(UsageVisitor.class);

        Set<String> ezDocMetadataFiles = new HashSet<>();
        ezDocMetadataFiles.add("src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/UsageIndent.java");

        String outputPath = "./target/README.md";

        ReadmeMarkdownGenerator readMeMarkdownGenerator = new ReadmeMarkdownGenerator(visitors, Usage.class, Definition.class);
        readMeMarkdownGenerator.execute(
                "src/test/java/tw/teddysoft/ezdoc/report/readme/testdata/UsageIndent.md",
                outputPath,
                ezDocMetadataFiles);

        String actualResult = readFile(outputPath);
        String expectedResult = """
                
                ```java
                    @EzScenario
                    public void scenario_example_with_user_created_rule_1() {
                        feature.newScenario().withRule(TOTAL_PRICE_RULE)
                                .Execute();
                    }
                ```
                
                
                
                ```java
                    @EzScenario
                    public void scenario_example_with_user_created_rule_2() {
                        feature.newScenario().withRule(TOTAL_PRICE_RULE)
                                .Execute();
                    }
                ```
                
                """;
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
