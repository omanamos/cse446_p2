import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RandomForest implements Classifier {
	
	private List<DecisionTree> trees;
	
	/**
	 * @param numTrees number of trees to predict off of, should be an odd number
	 * @param useFullTree specifies whether or not it should stop splitting based off of the chi-square test
	 * @throws IllegalArgumentException if numTrees is not odd, it must be odd, so there aren't any ties.
	 */
	public RandomForest(int numTrees, boolean useFullTree) throws IllegalArgumentException{
		this(numTrees, useFullTree, -1);
	}
	
	/**
	 * @param numTrees number of trees to predict off of, should be an odd number
	 * @param depthLimit depth limit each tree should have
	 * @throws IllegalArgumentException if numTrees is not odd, it must be odd, so there aren't any ties.
	 */
	public RandomForest(int numTrees, boolean useFullTree, int maxDepth) throws IllegalArgumentException{
		if(numTrees % 2 == 0)
			throw new IllegalArgumentException("numTrees must be odd so there aren't any ties.");
		this.trees = new ArrayList<DecisionTree>();
		for(int i = 0; i < numTrees; i++)
			this.trees.add(new DecisionTree(SplitRule.RANDOM, useFullTree, maxDepth));
	}
	
	/**
	 * @param examples list of examples to build the forest off of.
	 */
	public void train(List<Example> examples) {
		for(DecisionTree t : this.trees)
			t.train(examples);
	}
	
	/**
	 * @param example example to classify/label
	 * @return classification/label of the given example guess based off of this RandomForest
	 */
	public Label predict(Example example) {
		Map<Label, Integer> counts = new HashMap<Label, Integer>();
		
		for(DecisionTree t : this.trees){
			Label l = t.predict(example);
			if(!counts.containsKey(l))
				counts.put(l, 0);
			counts.put(l, counts.get(l) + 1);
		}
		
		Label rtn = null;
		int max = -1;
		for(Label l : counts.keySet()){
			if(counts.get(l) > max){
				max = counts.get(l);
				rtn = l;
			}
		}
		return rtn;
	}
}
