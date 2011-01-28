import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
	private static final Map<Integer, Double> CHI_THRESHOLDS = buildThresh();
	private static final Map<Attr, Map<String, Integer>> attrLookup = buildLookup();
	
	/**
	 * @param a Attr to search for
	 * @return all possible values for the given Attr a
	 */
	public static Set<String> getValues(Attr a){
		return attrLookup.get(a).keySet();
	}
	
	/**
	 * @param a Attr to look for values
	 * @param value value of Attr to check
	 * @return true if given value of given Attr is valid, false otherwise
	 */
	public static boolean isValid(Attr a, String value){
		return attrLookup.get(a).containsKey(value);
	}
	
	/**
	 * @param a Attribute that was split on to give the given chiSquare
	 * @param chiSquare calculated by Utility.chiSquare()
	 * @return true if the chiSquare value signifies a statistically significant split
	 * (whether or not the DecisionTree should split or stop here)
	 */
	public static boolean isSignificant(Attr a, double chiSquare){
		int degreeOfFreedom = attrLookup.get(a).size();
		return chiSquare >= CHI_THRESHOLDS.get(degreeOfFreedom);
	}
	
	/**
	 * @param a Attr to collect values of
	 * @param c Collection of values to remove from possible set of values
	 * @return All possible values of given Attr a that are not contained in Collection c
	 */
	public static List<String> getMissingValues(Attr a, Collection<String> c){
		Set<String> values = attrLookup.get(a).keySet();
		List<String> rtn = new ArrayList<String>();
		
		for(String s : values){
			if(!c.contains(s))
				rtn.add(s);
		}
		return rtn;
	}
	
	private static Map<Integer, Double> buildThresh(){
		Map<Integer, Double> rtn = new HashMap<Integer, Double>();
		rtn.put(3, 2.37);
		rtn.put(4, 3.36);
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
