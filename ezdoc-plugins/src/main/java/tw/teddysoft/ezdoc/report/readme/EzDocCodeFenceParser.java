package tw.teddysoft.ezdoc.report.readme;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EzDocCodeFenceParser {

    public static List<EzDocCodeFence> parse(String template) {

        Pattern pattern = Pattern.compile("```(?i)ezdoc\\s*([\\s\\S]*?)\\s*```");
        Matcher codeFanceMatcher = pattern.matcher(template);
        List<EzDocCodeFence> codeFences = new ArrayList<>();

        while (codeFanceMatcher.find()) {
            Set<Placeholder> placeholders = parsePlaceholdersInCodeFence(codeFanceMatcher.group());
            codeFences.add(new EzDocCodeFence(codeFanceMatcher.group(), placeholders));
        }

        return codeFences;
    }

    public static Set<Placeholder> parsePlaceholdersInCodeFence(String codeBlock) {

        // regular expression to find a placeholder, e.g.,  @annotation (string)
        Pattern pattern = Pattern.compile("@\\s*(\\w+)\\s*\\(\\s*([A-Za-z0-9#_.]+)\\s*\\)");
        Matcher placeholderMatcher = pattern.matcher(codeBlock);
        Set<Placeholder> placeholders = new HashSet<>();

        while (placeholderMatcher.find()) {
            placeholders.add(new Placeholder(placeholderMatcher.group()));
        }

        return placeholders;
    }
}
