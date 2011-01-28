import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DecisionTree implements Classifier {
	private Node root;
	private SplitRule rule;
	private boolean useFullTree;
	private int maxDepth;
	private int depth;
	
	/**
	 * Initializes a full decision tree
	 * @param rule SplitRule to use to choose the next best attribute to split on
	 * @param useFullTree specifies whether or not it should stop splitting based off of the chi-square test
	 */
	public DecisionTree(SplitRule rule, boolean useFullTree){
		this(rule, useFullTree, -1);
	}
	
	/**
	 * @param rule SplitRule to use to choose the next best attribute to split on
	 * @param useFullTree specifies whether or not it should stop splitting based off of the chi-square test
	 * @param maxDepth max depth this tree should go to
	 */
	public DecisionTree(SplitRule rule, boolean useFullTree, int maxDepth){
		this.rule = rule;
		this.root = null;
		this.useFullTree = useFullTree;
		this.maxDepth = maxDepth;
		this.depth = 0;
	}
	
	/**
	 * @param e example to classify/label
	 * @return classification/label of the given example guess based off of this DecisionTree
	 */
	public Label predict(Example e){
		if(this.root == null){
			return Label.values()[(int)(Label.values().length * Math.random())];
		}
		Node curRoot = this.root;
		while(!(curRoot instanceof LeafNode)){
			DecisionNode n = (DecisionNode)curRoot;
			curRoot = n.getChild(e.getValue(n.getAttribute()));
		}
		return ((LeafNode)curRoot).getClassification();
	}
	
	public void train(List<Example> examples){
		List<Attr> attributes = new ArrayList<Attr>();
		for(Attr a : Attr.values())
			attributes.add(a);
		
		this.root = id3(Utils.divideExamples(examples), attributes, 0);
	}
	
	/**
	 * @param examples list of examples to train on split based on their label (Examples with the label l could be retrieved with 'examples.get(l)')
	 * @param attributes list of attributes to split the training examples on
	 * @param curDepth current depth in the decision tree
	 * @return A decision tree based on the given examples. Either a LeafNode(where a classification can be made)
	 * or a DecisionNode(where the predict function can traverse left or right based on the attribute in that DecisionNode).
	 */
	private Node id3(Map<Label, List<Example>> examples, List<Attr> attributes, int curDepth){
		if(examples.keySet().size() == 1 || attributes.size() == 0 || this.maxDepth != -1 && curDepth == this.maxDepth){
			return Utils.classify(examples);
		}else{
			int attrIndex = getBestAttr(examples, attributes);
			Attr a = attributes.get(attrIndex);
			
			Map<String, Map<Label, List<Example>>> splitExamples = Utils.divideExamples(examples, a);
			
			if(!this.useFullTree){
				double chiSquare = Utils.chiSquare(examples, splitExamples);
				if(id3.DEBUG) System.out.println("Depth: " + curDepth + " chi: " + chiSquare);
				if(!Attr.isSignificant(a, chiSquare)){
					return Utils.classify(examples);
				}
			}
			
			List<String> emptyValues = Attr.getMissingValues(a, splitExamples.keySet());
			
			DecisionNode curNode = new DecisionNode(a);
			LeafNode curLeaf = Utils.classify(examples);
			for(String val : emptyValues){
				curNode.addBranch(val, curLeaf);
			}
			
			attributes.remove(attrIndex);
			for(String key : splitExamples.keySet())
				curNode.addBranch(key, id3(splitExamples.get(key), attributes, curDepth + 1));
			attributes.add(attrIndex, a);
			
			this.depth = Math.max(this.depth, curDepth);
			return curNode;
		}
	}
	
	/**
	 * @param examples current list of examples
	 * @param attributes current list of attributes
	 * @return index of the most selected attribute to split on. Chosen based off of the global SplittingRule, rule.
	 */
	private int getBestAttr(Map<Label, List<Example>> examples, List<Attr> attributes){
		if(this.rule.equals(SplitRule.RANDOM))
			return (int)(Math.random() * attributes.size());
		int totalSize = Utils.getTotalSize(examples);
		double currentImpurity = Utils.calculateImpurity(examples, totalSize, this.rule);
		double maxInfoGain = -1.0;
		int maxInd = -1;
		
		for(int i = 0; i < attributes.size(); i++){
			Attr a = attributes.get(i);
			Map<String, Map<Label, List<Example>>> attributeSplit = Utils.divideExamples(examples, a);
			double childrenImpurity = 0.0;
			
			for(String val : attributeSplit.keySet()){
				int childSize = Utils.getTotalSize(attributeSplit.get(val));
				childrenImpurity += (childSize / (double)totalSize) * Utils.calculateImpurity(attributeSplit.get(val), childSize, this.rule);
			}
			
			double curInfoGain = currentImpurity - childrenImpurity;
			if(maxInfoGain < curInfoGain){
				maxInfoGain = curInfoGain;
				maxInd = i;
			}
		}
		
		return maxInd;
	}
	
	public String toString(){
		return this.root == null ? "" : toString(this.root, "\t");
	}
	
	public String toString(Node curRoot, String indent){
		if(curRoot instanceof LeafNode){
			return indent + curRoot.toString() + "\n";
		}else{
			String rtn = indent + curRoot.toString() + "\n";
			Iterator<Node> i = ((DecisionNode)curRoot).getChildren();
			while(i.hasNext()){
				Node n = i.next();
				rtn += toString(n, indent + "\t");
			}
			return rtn;
		}
	}
}
