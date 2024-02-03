package tw.teddysoft.ezdoc.report.glossary.testdata;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tw.teddysoft.ezdoc.annotation.Definition;
import tw.teddysoft.ezspec.EzFeature;
import tw.teddysoft.ezspec.keyword.Feature;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@EzFeature
@Definition(brief = "The Feature class." ,
        narrative = """
            The Feature class represents the feature keyword.
            """, targetClass = tw.teddysoft.ezdoc.report.glossary.Feature.class)
public class FeatureDefinition {
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
