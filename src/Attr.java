import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public enum Attr {
	buying, 
	maint, 
	doors, 
	persons,
	lug_boot, 
	safety;
	
	private static final String[][] VALUES = {{"vhigh", "high", "med", "low"},
											 {"vhigh", "high", "med", "low"},
											 {"2", "3", "4", "5more"},
											 {"2", "4", "more"},
											 {"small", "med", "big"},
											 {"low", "med", "high"}};
	public static final Map<Attr, Map<String, Integer>> attrLookup = buildLookup();
	
	public static Set<String> getValues(Attr a){
		return attrLookup.get(a).keySet();
	}
	
	public static boolean isValid(Attr a, String value){
		return attrLookup.get(a).containsKey(value);
	}
	
	public static Set<String> getMissingValues(Attr a, Collection<String> c){
		Set<String> rtn = attrLookup.get(a).keySet();
		rtn.removeAll(c);
		return rtn;
	}
	
	private static Map<Attr, Map<String, Integer>> buildLookup(){
		Map<Attr, Map<String, Integer>> rtn = new HashMap<Attr, Map<String, Integer>>();
		
		Attr[] attrs = Attr.values();
		for(int i = 0; i < attrs.length; i++){
			Attr e = attrs[i];
			
			Map<String, Integer> map = new HashMap<String, Integer>();
			for(String s : VALUES[i])
				map.put(s, i);
			
			rtn.put(e, map);
		}
		return rtn;
	}
	
}
