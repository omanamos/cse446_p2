import java.util.List;

public interface Classifier {
	
	public void train(List<Example> examples);
	
	public Label predict(Example example);
}
