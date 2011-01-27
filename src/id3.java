import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class id3 {
	
	public static void main(String[] args) throws FileNotFoundException{
		List<Example> trainingExamples = loadExamples("train.txt");
		
		for(SplitRule rule : SplitRule.values()){
			double maxAcc = -1;
			double lastAcc = 0;
			int maxHeight = 0;
			
			while(maxAcc < lastAcc){
				maxAcc = lastAcc;
				maxHeight++;
				
				for(int i = 0; i < 10; i++){
					DecisionTree t = new DecisionTree(rule, maxHeight);
					List<Example> validationExamples = partitionExamples(trainingExamples, i);
					
					t.train(trainingExamples);
					lastAcc = test(t, validationExamples);
				}
				
				System.out.println("Accuracy = " + lastAcc + " at height " + maxHeight);
			}
			maxHeight--;
			
			System.out.println("For splitting rule " + rule + " max accuracy = " + maxAcc + " achieved at height " + maxHeight);
		}
	}
	
	public static double test(DecisionTree t, List<Example> testExamples){
		int correct = 0;
		for(Example e : testExamples){
			Label l = t.predict(e);
			if(l.equals(e.getLabel()))
				correct++;
		}
		
		return correct * 100.0 / (double)testExamples.size();
	}
	
	public static List<Example> loadExamples(String fileName) throws FileNotFoundException{
		List<Example> rtn = new ArrayList<Example>();
		
		Scanner s = new Scanner(new File(fileName));
		while(s.hasNextLine()){
			String[] tmp = s.nextLine().split(",");
			
			String[] data = new String[tmp.length - 1];
			for(int i = 0; i < data.length; i++)
				data[i] = tmp[i];
			
			Label l = Label.valueOf(tmp[tmp.length - 1]);
			
			rtn.add(new Example(data, l));
		}
		
		return rtn;
	}
}
