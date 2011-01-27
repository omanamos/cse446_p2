import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class DecisionTree {
	private Node root;
	private SplitRule rule;
	private int maxDepth;
	private int depth;
	
	public DecisionTree(SplitRule rule, int maxDepth){
		this.rule = rule;
		this.root = null;
		this.maxDepth = maxDepth;
		this.depth = 0;
	}
	
	public Label predict(Example e){
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
		
		this.root = id3(divideExamples(examples), attributes, 0);
	}
	
	private Node id3(Map<Label, List<Example>> examples, List<Attr> attributes, int curDepth){
		if(examples.keySet().size() == 1 || attributes.size() == 0 || curDepth == this.maxDepth){
			return classify(examples);
		}else{
			int attrIndex = getBestAttr(examples, attributes);
			Attr a = attributes.get(attrIndex);
			
			Map<String, Map<Label, List<Example>>> splitExamples = divideExamples(examples, a);
			List<String> emptyValues = Attr.getMissingValues(a, splitExamples.keySet());
			
			DecisionNode curNode = new DecisionNode(a);
			LeafNode curLeaf = classify(examples);
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
	
	private int getBestAttr(Map<Label, List<Example>> examples, List<Attr> attributes){
		if(this.rule.equals(SplitRule.RANDOM))
			return (int)(Math.random() * attributes.size());
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
	
	public String toString(){
		Set<Node> frontier = new HashSet<Node>();
		frontier.add(this.root);
		String rtn = "";
		int curDepth = -1;
		
		while(!frontier.isEmpty()){
			Set<Node> newFront = new HashSet<Node>();
			rtn += repeat("\t", this.depth - curDepth);
			for(Node n : frontier){
				rtn += n.toString() + repeat(" ", this.depth - curDepth + 1);
				
				if(n instanceof DecisionNode){
					Iterator<Node> children = ((DecisionNode)n).getChildren();
					while(children.hasNext()){
						newFront.add(children.next());
					}
				}
			}
			curDepth++;
			rtn += "\n";
			frontier = newFront;
		}
		return rtn;
	}
	
	private String repeat(String s, int cnt){
		String rtn = "";
		for(int i = 0; i < cnt; i++)
			rtn += s;
		return rtn;
	}
}
