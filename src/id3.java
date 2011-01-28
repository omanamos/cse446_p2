import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class id3 {
	/**
	 * prints out debug messages if true
	 */
	public static final boolean DEBUG = false;
	/**
	 * number of trees in random forest
	 */
	private static final int FOREST_SIZE = 101;
	
	public static void main(String[] args) throws FileNotFoundException{
		
		//Parse parameters
		if(args.length < 3 || args.length > 4){
			System.out.println("Invalid command.");
			return;
		}
		
		String trainingFile = args[0];
		String testFile = args[1];
		
		SplitRule rule;
		if(args[2].toLowerCase().equals("all")){
			testAll(trainingFile, testFile);
			return;
		}if(args[2].toLowerCase().equals("info_gain"))
			rule = SplitRule.ENTROPY;
		else if(args[2].toLowerCase().equals("accuracy"))
			rule = SplitRule.ACCURACY;
		else if(args[2].toLowerCase().equals("random"))
			rule = SplitRule.RANDOM;
		else{
			System.out.println("Invalid splitting rule.");
			return;
		}
		
		boolean useFullTree = false;
		if(args.length == 4){
			if(args[3].toLowerCase().equals("full"))
				useFullTree = true;
			else{
				System.out.println("Invalid depth parameter.");
				return;
			}
		}
			
		//Train and test tree
		List<Example> trainingExamples = loadExamples(trainingFile);
		Classifier t;
		switch(rule){
			case ENTROPY: case ACCURACY:
				t = new DecisionTree(rule, useFullTree);
				break;
			case RANDOM:
				t = new RandomForest(FOREST_SIZE, useFullTree);
				break;
			default:
				System.out.println("Invalid Rule.");
				return;
		}
		t.train(trainingExamples);
		double testAcc = test(t, loadExamples(testFile));
		double trainAcc = test(t, trainingExamples);
		
		System.out.println("Accuracy: ");
		System.out.println("\tTest = " + testAcc);
		System.out.println("\tTraining = " + trainAcc);
		System.out.println(t);
		
	}
	
	private static void testAll(String trainingFile, String testFile) throws FileNotFoundException{
		List<Example> trainingExamples = loadExamples(trainingFile);
		List<Example> testExamples = loadExamples(testFile);
		
		for(SplitRule rule : SplitRule.values()){
			double pruneTestAcc = 0.0;
			double pruneMaxAcc = 0.0;
			double pruneTrainAcc = 0.0;
			double fullTestAcc = 0.0;
			double fullMaxAcc = 0.0;
			double fullTrainAcc = 0.0;
			for(int i = 0; i < 100; i++){
				Classifier prune;
				Classifier full;
				switch(rule){
					case ENTROPY: case ACCURACY:
						prune = new DecisionTree(rule, false);
						full = new DecisionTree(rule, true);
						break;
					default:
						prune = new RandomForest(FOREST_SIZE, false);
						full = new RandomForest(FOREST_SIZE, true);
						break;
				}
				prune.train(trainingExamples);
				full.train(trainingExamples);
				
				double tmp = test(prune, testExamples);
				pruneMaxAcc = Math.max(tmp, pruneMaxAcc);
				pruneTestAcc += tmp;
				pruneTrainAcc += test(prune, trainingExamples);
				
				tmp = test(full, testExamples);
				fullMaxAcc = Math.max(tmp, fullMaxAcc);
				fullTestAcc += tmp;
				fullTrainAcc += test(full, trainingExamples);
			}
			
			System.out.println("Rule: " + rule + " pruned.");
			System.out.println("Accuracy: ");
			System.out.println("\tMax Test = " + pruneMaxAcc);
			System.out.println("\tAverage Test = " + (pruneTestAcc / 100.0));
			System.out.println("\tAverage Training = " + (pruneTrainAcc / 100.0));
			
			System.out.println("Rule: " + rule + " full.");
			System.out.println("Accuracy: ");
			System.out.println("\tMax Test = " + fullMaxAcc);
			System.out.println("\tAverage Test = " + (fullTestAcc / 100.0));
			System.out.println("\tAverage Training = " + (fullTrainAcc / 100.0));
		}
	}
	
	/**
	 * @param t DecisionTree to use to predict
	 * @param testExamples Examples to test against
	 * @return the accuracy of prediction the given DecisionTree has on the given test set
	 */
	private static double test(Classifier t, List<Example> testExamples){
		int correct = 0;
		for(Example e : testExamples){
			Label l = t.predict(e);
			if(l.equals(e.getLabel()))
				correct++;
		}
		
		return correct * 100.0 / (double)testExamples.size();
	}
	
	/**
	 * Assumes the source file is one example per line, with attribute values delimited by commas and the label as the last value on the line.
	 * @param fileName name of file to load
	 * @return List of parsed examples from the given file
	 * @throws FileNotFoundException
	 */
	private static List<Example> loadExamples(String fileName) throws FileNotFoundException{
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
