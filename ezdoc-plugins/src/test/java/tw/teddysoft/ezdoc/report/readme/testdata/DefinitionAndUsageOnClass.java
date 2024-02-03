package tw.teddysoft.ezdoc.report.readme.testdata;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tw.teddysoft.ezdoc.annotation.Definition;
import tw.teddysoft.ezdoc.annotation.Usage;
import tw.teddysoft.ezspec.EzFeature;
import tw.teddysoft.ezspec.keyword.Feature;
import tw.teddysoft.ezspec.keyword.Rule;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@EzFeature
@Definition(brief = "A `Rule` represents the business logic of a feature. A feature can be broken down into multiple rules to aid in scenario illustration. Each rule, in turn, encompasses a collection of related scenarios that pertain to the feature's business logic. The use of the `Rule` keyword is optional. If a feature does not contain a rule, all of its scenarios will be categorized under a predefined rule named \"default\" in ezSpec. A rule has the capability to define a Background that can be shared among scenarios associated with the same rule.",
        narrative = """
- To create a rule in a feature, invoke `Feature::NewRule(String ruleName)`.
- There are three ways to categorize a scenario to a rule:
  1. invoke `Scenario::withRule(String ruleName)`,
  2. use `@EzScenario(rule = ruleName)`, and
  3. annotate with `@EzRule(ruleName)`.

If no rule is specified, the scenario belongs to the default rule.
        """,
        targetClass = Rule.class,
        usages = {UsageOnClass.class})
@Usage(value = "Rule can be used with a feature.",
        targetClass = Rule.class,
        definitionClass = DefinitionOnClass.class)
public class DefinitionAndUsageOnClass {
    static Feature feature;

    @BeforeAll
    public static void beforeAll() {

        feature = Feature.New("Leave Team Use Case", """
                    A team member can leave a team at anytime.
                    However, there must be at least one team admin in the team.
                    That is, the last team admin cannot leave the team.
                """);

        feature.NewRule("5% tax fee");
        feature.NewRule("Tax free");
    }

    @Test
    public void a_feature_has_a_default_rule() {
        assertNotNull(feature.getDefaultRule());
    }
}
