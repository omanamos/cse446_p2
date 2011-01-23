import java.util.List;
import java.util.Map;


public class Utils {
	public static double calculateImpurity(Map<Label, List<Example>> examples, int size, SplitRule rule){
		switch(rule){
		case ENTROPY: return calculateEntropyImpurity(examples, size);
		case MISCLASS: return calculateMisclassificationImpurity(examples, size);
		default:
			throw new IllegalArgumentException("Illegal SplitRule: " + rule);
		}
	}
	
	public static double calculateEntropyImpurity(Map<Label, List<Example>> examples, int size){
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
	
	public static double calculateMisclassificationImpurity(Map<Label, List<Example>> examples, int size){
		int maxSize = -1;
		for(Label l : examples.keySet()){
			int curSize = examples.get(l).size();
			if(curSize > maxSize)
				maxSize = curSize;
		}
		return 1.0 - (maxSize / (double)size);
	}
	
	/**
	 * @param q value to take logarithm of
	 * @param base base of logarithm
	 * @return log(base = base) of (q)
	 */
	public static double log(double q, int base){
		return Math.log10(q) / Math.log10(base);
	}
	
	public static int getTotalSize(Map<Label, List<Example>> examples){
		int rtn = 0;
		for(Label l : examples.keySet())
			rtn += examples.get(l).size();
		return rtn;
	}
}
