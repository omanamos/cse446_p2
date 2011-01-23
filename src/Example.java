import java.util.HashMap;
import java.util.Map;


public class Example {
	
	private String[] data;
	private Label label;
	private Map<Attr, String> mappings;
	
	public Example(String[] data){
		this(data, null);
	}
	
	public Example(String[] data, Label label){
		this.data = data;
		this.label = label;
		
		this.mappings = new HashMap<Attr, String>();
		Attr[] attributes = Attr.values();
		for(int i = 0; i < this.data.length; i++){
			this.mappings.put(attributes[i], this.data[i]);
		}
	}
	
	public String[] getData(){
		return this.data;
	}
	
	public String getValue(Attr a){
		if(!this.mappings.containsKey(a))
			throw new IllegalArgumentException("This example doesn't have Attribute: " + a);
		return this.mappings.get(a);
	}
	
	public Label getLabel(){
		if(!this.hasLabel())
			throw new IllegalStateException("This example doesn't have a label.");
		return this.label;
	}
	
	public boolean hasLabel(){
		return this.label != null;
	}
	
	public boolean equals(Object other){
		if(!(other instanceof Example)){
			return false;
		}else{
			Example o = (Example)other;
			
			for(int i = 0; i < this.data.length; i++){
				if(!this.data[i].equals(o.data[i]))
					return false;
			}
			
			return this.label.equals(o.label);
		}
	}
}
