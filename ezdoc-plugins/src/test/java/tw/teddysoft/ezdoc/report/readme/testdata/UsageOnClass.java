package tw.teddysoft.ezdoc.report.readme.testdata;

import org.junit.jupiter.api.BeforeAll;
import tw.teddysoft.ezdoc.annotation.Usage;
import tw.teddysoft.ezspec.EzFeature;
import tw.teddysoft.ezspec.extension.junit5.EzScenario;
import tw.teddysoft.ezspec.keyword.Feature;

@EzFeature
@Usage(value = "Rule can be used with a feature.",
        targetClass = Rule.class,
        definitionClass = DefinitionOnClass.class)
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
