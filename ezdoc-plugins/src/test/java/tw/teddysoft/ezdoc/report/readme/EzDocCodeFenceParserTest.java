package tw.teddysoft.ezdoc.report.readme;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EzDocCodeFenceParserTest {

    @Test
    public void testParseEzDocCodeFence () {
        String ezDocSnippet = """
                ```ezDoc
                    @Definition (Rule.class)
                    @Usage(Rule.class#DefinitionAndUsageOnClass )
                ```
                
                ```java
                    
                ```
                ```ezdoc
                ```
                """;

        List<EzDocCodeFence> codeFences = EzDocCodeFenceParser.parse(ezDocSnippet);
        assertEquals(2, codeFences.size());
    }


    @Test
    public void testParseTagsInCodeFence () {
        String ezDocSnippet = """
                ```ezDoc
                @Definition (Rule.class)
                @Usage(Rule.class#DefinitionAndUsageOnClass )
                @Definition(Rule.class )
                @Usage( Rule.class#DefinitionAndUsageOnClass )
                @New ( x
                @New2()
                @Usage(Rule.class#scenario_example_with_user_created_rule_1)
                @NewPlaceholder(Rule.class)
                ```
                """;

        Set<Placeholder> placeholders = EzDocCodeFenceParser.parsePlaceholdersInCodeFence(ezDocSnippet);

        assertEquals(6, placeholders.size());
    }

}
