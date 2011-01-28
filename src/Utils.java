import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Utils {
	
	
	/**
	 * @param examples set of examples to classify
	 * @return a LeafNode with the most commonly occuring label in the given list of examples
	 */
	public static LeafNode classify(Map<Label, List<Example>> examples){
		int max = -1;
		Label rtn = null;
		for(Label key : examples.keySet()){
			int cnt = examples.get(key).size();
			if(cnt >= max){
				max = cnt;
				rtn = key;
			}
		}
		return new LeafNode(rtn);
	}
	
	/**
	 * @param examples list of examples
	 * @param size total count of all examples
	 * @param rule rule to use to split
	 * @return impurity based on the given rule of the given examples
	 */
	public static double calculateImpurity(Map<Label, List<Example>> examples, int size, SplitRule rule){
		switch(rule){
		case ENTROPY: return calculateEntropyImpurity(examples, size);
		case ACCURACY: return calculateMisclassificationImpurity(examples, size);
		default:
			throw new IllegalArgumentException("Illegal SplitRule: " + rule);
		}
	}
	
	private static double calculateEntropyImpurity(Map<Label, List<Example>> examples, int size){
		double rtn = 0.0;
		Label[] labels = Label.values();
		
		for(int i = 0; i < labels.length; i++){
			if(examples.containsKey(labels[i]) && examples.get(labels[i]).size() != 0){
				double frac = (double)examples.get(labels[i]).size() / (double)size;
				rtn += -(frac * log(frac, 2));
			}
		}
		return rtn;
	}
	
	private static double calculateMisclassificationImpurity(Map<Label, List<Example>> examples, int size){
		int maxSize = -1;
		for(Label l : examples.keySet()){
			int curSize = examples.get(l).size();
			if(curSize > maxSize)
				maxSize = curSize;
		}
		return 1.0 - (maxSize / (double)size);
	}
	
	/**
	 * @param oldEx examples before the split
	 * @param newEx examples split on some attribute
	 * @return the chi-square estimate of this split
	 */
	public static double chiSquare(Map<Label, List<Example>> oldEx, Map<String, Map<Label, List<Example>>> newEx){
		Map<String, Map<Label, Integer>> primeCounts = new HashMap<String, Map<Label, Integer>>();
		for(String val : newEx.keySet())
			primeCounts.put(val, mapToCounts(newEx.get(val)));
		Map<Label, Integer> totalCounts = mapToCounts(oldEx);
		int primeDenominator = reduce(totalCounts);
		
		double rtn = 0.0;
		for(String s : newEx.keySet()){
			Map<Label, Integer> valueCounts = primeCounts.get(s);
			int primeNumerator = reduce(valueCounts);
			for(Label l : Label.values()){
				int x = valueCounts.get(l);
				double xPrime = getPrime(totalCounts.get(l), primeNumerator, primeDenominator);
				double chiSquare = chiSquare(x, xPrime);
				rtn += chiSquare;
			}
		}
		
		return rtn;
	}
	
	private static double chiSquare(int x, double xPrime){
		return xPrime == 0.0 ? xPrime : Math.pow(x - xPrime, 2) / xPrime;
	}
	
	/**
	 * @param base Examples to divide, which are already split on their label.
	 * @param attr attribute to split on
	 * @return New Maps with the same structure as the given base. Each one 
	 * has all examples that have the given attr set to a given value.
	 */
	public static Map<String, Map<Label, List<Example>>> divideExamples(Map<Label, List<Example>> examples, Attr a){
		Map<String, Map<Label, List<Example>>> rtn = new HashMap<String, Map<Label, List<Example>>>();
		for(Label label : examples.keySet()){
			for(Example e : examples.get(label)){
				String val = e.getValue(a);
				if(!rtn.containsKey(val)){
					rtn.put(val, new HashMap<Label, List<Example>>());
					rtn.get(val).put(label, new ArrayList<Example>());
				}else if(!rtn.get(val).containsKey(label))
					rtn.get(val).put(label, new ArrayList<Example>());
				rtn.get(val).get(label).add(e);
			}
		}
		return rtn;
	}
	
	/**
	 * @param examples list to divide based on label
	 * @return a map with Label examples in value list as key.
	 */
	public static Map<Label, List<Example>> divideExamples(List<Example> examples){
		Map<Label, List<Example>> rtn = new HashMap<Label, List<Example>>();
		
		for(Example e : examples){
			Label l = e.getLabel();
			if(!rtn.containsKey(l)){
				rtn.put(l, new ArrayList<Example>());
			}
			rtn.get(l).add(e);
		}
		
		return rtn;
	}
	
	private static double getPrime(int p, int numerator, int denominator){
		return (double)p * (numerator / (double)denominator);
	}
	
	/**
	 * @param examples set of examples to count
	 * @return total number of examples
	 */
	public static int getTotalSize(Map<Label, List<Example>> examples){
		int rtn = 0;
		for(Label l : examples.keySet())
			rtn += examples.get(l).size();
		return rtn;
	}
	
	/**
	 * @param q value to take logarithm of
	 * @param base base of logarithm
	 * @return log(base = base) of (q)
	 */
	public static double log(double q, int base){
		return Math.log10(q) / Math.log10(base);
	}
	
	private static Map<Label, Integer> mapToCounts(Map<Label, List<Example>> oldEx){
		Map<Label, Integer> rtn = new HashMap<Label, Integer>();
		for(Label l : Label.values())
			rtn.put(l, oldEx.containsKey(l) ? oldEx.get(l).size() : 0);
		return rtn;
	}
	
	/**
	 * @param m Map to reduce
	 * @return sum of all integers in value set
	 */
	public static int reduce(Map<Label, Integer> m){
		int rtn = 0;
		for(Label l : m.keySet())
			rtn += m.get(l);
		return rtn;
	}
}
