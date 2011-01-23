
public class LeafNode implements Node{
	private Label classification;
	
	public LeafNode(Label classification){
		this.classification = classification;
	}
	
	public Label getClassification(){
		return this.classification;
	}
	
	public String toString(){
		return this.classification.toString();
	}
}
