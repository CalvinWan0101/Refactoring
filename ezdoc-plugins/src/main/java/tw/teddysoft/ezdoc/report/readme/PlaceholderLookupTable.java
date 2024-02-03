package tw.teddysoft.ezdoc.report.readme;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlaceholderLookupTable {

    private final Map<Placeholder, String> lookupTable;

    public PlaceholderLookupTable(){
        lookupTable = new HashMap<>();
    }

    public void put(String placeholder, String value){
        lookupTable.put(new Placeholder(placeholder), value);
    }

    public void put(Placeholder placeholder, String value){
        lookupTable.put(placeholder, value);
    }

    public String get(Placeholder placeholder){
        return lookupTable.get(placeholder);
    }

    public String get(String placeholder){
        return lookupTable.get(new Placeholder(placeholder));
    }

    public boolean containsKey(String placeholder){
        return lookupTable.containsKey(new Placeholder(placeholder));
    }

    public Set<Map.Entry<Placeholder, String>> entrySet() {
        return lookupTable.entrySet();
    }

}
