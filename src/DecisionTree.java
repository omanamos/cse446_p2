import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class DecisionTree {
	private Node root;
	private SplitRule rule;
	
	public DecisionTree(SplitRule rule){
		this.rule = rule;
		this.root = null;
	}
	
	public Label predict(Example example){
		Node curRoot = this.root;
		while(!(curRoot instanceof LeafNode)){
			DecisionNode n = (DecisionNode)curRoot;
			curRoot = n.getChild(example.getValue(n.getAttribute()));
		}
		return ((LeafNode)curRoot).getClassification();
	}
	
	public void train(List<Example> examples){
		List<Attr> attributes = new ArrayList<Attr>();
		for(Attr a : Attr.values())
			attributes.add(a);
		
		this.root = id3(divideExamples(examples), attributes);
	}
	
	private Node id3(Map<Label, List<Example>> examples, List<Attr> attributes){
		if(examples.keySet().size() == 1 || attributes.size() == 0){
			return classify(examples);
		}else{
			int attrIndex = getBestAttr(examples, attributes);
			Attr a = attributes.get(attrIndex);
			
			Map<String, Map<Label, List<Example>>> splitExamples = divideExamples(examples, a);
			Set<String> emptyValues = Attr.getMissingValues(a, splitExamples.keySet());
			
			DecisionNode curNode = new DecisionNode(a);
			LeafNode curLeaf = classify(examples);
			for(String val : emptyValues){
				curNode.addBranch(val, curLeaf);
			}
			
			attributes.remove(attrIndex);
			for(String key : splitExamples.keySet())
				curNode.addBranch(key, id3(splitExamples.get(key), attributes));
			attributes.add(attrIndex, a);
			
			return curNode;
		}
	}
	
	private int getBestAttr(Map<Label, List<Example>> examples, List<Attr> attributes){
		int totalSize = Utils.getTotalSize(examples);
		double currentImpurity = Utils.calculateImpurity(examples, totalSize, this.rule);
		double maxInfoGain = -1.0;
		int maxInd = -1;
		
		for(int i = 0; i < attributes.size(); i++){
			Attr a = attributes.get(i);
			Map<String, Map<Label, List<Example>>> attributeSplit = divideExamples(examples, a);
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
	
	private static LeafNode classify(Map<Label, List<Example>> examples){
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
	
	private static Map<String, Map<Label, List<Example>>> divideExamples(Map<Label, List<Example>> examples, Attr a){
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
	
	private static Map<Label, List<Example>> divideExamples(List<Example> examples){
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
}
