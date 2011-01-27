import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class DecisionNode implements Node{
	private Attr attribute;
	private Map<String, Node> children;
	
	public DecisionNode(Attr attribute){
		this.attribute = attribute;
		this.children = new HashMap<String, Node>();
	}
	
	public void addBranch(String value, Node child){
		if(!Attr.isValid(attribute, value)){
			throw new IllegalArgumentException("Invalid value: " + value + " for attribute: " + this.attribute.toString());
		}
		this.children.put(value, child);
	}
	
	public Attr getAttribute(){
		return this.attribute;
	}
	
	public Node getChild(String value){
		return this.children.get(value);
	}
	
	public Iterator<Node> getChildren(){
		return this.children.values().iterator();
	}
	
	public String toString(){
		return this.attribute.toString();
	}
}
