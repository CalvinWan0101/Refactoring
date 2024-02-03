package tw.teddysoft.ezdoc.report.readme;

import java.util.Set;

public record EzDocCodeFence(String codeFence, Set<Placeholder> placeHolders) {

    public String codeBlock() {
        String s = this.codeFence.replaceFirst("```(?i)ezdoc(\\n|\\r|\\r\\n)*", "");
        int pos = s.lastIndexOf("```");
        return s.substring(0, pos);
    }
}
