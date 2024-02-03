package tw.teddysoft.ezdoc.report.readme;

public record Placeholder(String value) {
    public String withoutSpace() {
        return value.replaceAll("\\s", "");
    }
}
