Ryan Oman
CSE 446 - Machine Learning
Project 2
---------------------------------
3.1
---------------------------------
Each Node is signified by the attribute it was split on. 
A left branch signifies a 1 on that attribute. 
A right branch signifies a 0 on that attribute.
a.)
        A
      1/ \0
      B   0
    1/ \0
    0   1

b.)
        A
       / \
      1   B
         / \
        C   0
       / \
      1   0

c.)
          A
        _/ \_
       /     \
      B       B
     / \     / \
    0   1   1   0
    
d.)
             A
          __/ \__
         /       \
        B         C
       / \       / \
      1   C     D   0
         / \   / \
        D   0 1   0
       / \
      1   0
---------------------------------     
3.2
---------------------------------
a.)  Entropy(S) = (-0.5 * log(0.5)) + (-0.5 * log(0.5)) = 1
b.)  InfoGain(a2) = Entropy(S) - [(4/6) * (-0.5 * log(0.5) - 0.5 * log(0.5)) + 
                   (2/6) * (-0.5 * log(0.5) + 0.5 * log(0.5))] = 1 - 1 = 0


************************************************************************************************
Implementation Notes
************************************************************************************************
In order to implement Decision Tree Learning, I created the following classes:

------------------------
interface Node
------------------------
This interface is implemented by the LeafNode and DecisionNode classes. It is used 
so that both DecisionNodes and LeafNodes can be stored in the same way, as a Node.

------------------------
class DecisionNode
------------------------
This class stores the Attr that the example set was split on for the 
node, in addition to all of the children generated by splitting on said Attr.

------------------------
class LeafNode
------------------------
This class stores a Label which is the is the label/classification to be 
guessed if the predict function reaches such a given node. It has no children 
pointers because it is the end of the prediction process.

------------------------
class DecisionTree
------------------------
This class is used to perform the training and prediction for the sets of Examples.
The train method uses the recursive helper id3. The examples are passed to this 
helper as a HashMap of ArrayLists. The hash only contains Labels as keys.
The ArrayList that is returned by 'examples.get(Label x)' only contains Examples that 
have a label of 'Label x'.

------------------------
class Utils
------------------------
This class stores all of the static methods for the DecisionTree class. It does all of
the heavy lifting for the DecisionTree class. It contains the method to get the most
important attribute to split on, and calculate the gain/entropy for splitting on each
attribute. It also contains the method for dividing examples based on an attribute, and
doing the final plurality-value for determining the value a LeafNode uses. It also contains
the chi-test code to determine stopping criteria. 

------------------------
Extra Credit: class RandomForest
------------------------
This class adds another layer of abstraction on top of the DecisionTree class,
and adds a majority vote layer on top of multiple randomly split decision trees.

------------------------
enum Attr and Label and SplitRule
------------------------
These define all possible label values, splitting rules, and attributes to split on, 
along with some utility functions that go along with them.


******************************************
Accuracy
******************************************
The following report the accuracy of each algorithm. The averages and
maximums are over 100 runs of the algorithm.

Rule: RANDOM FOREST pruned.
Accuracy: 
	Max Test = 79.0
	Average Test = 74.605
	Average Training = 90.08200000000002
Rule: RANDOM FOREST full.
Accuracy: 
	Max Test = 85.5
	Average Test = 83.76
	Average Training = 100.0
Rule: ENTROPY pruned.
Accuracy: 
	Max Test = 90.0
	Average Test = 90.0
	Average Training = 97.70000000000002
Rule: ENTROPY full.
Accuracy: 
	Max Test = 89.0
	Average Test = 89.0
	Average Training = 100.0
Rule: ACCURACY pruned.
Accuracy: 
	Max Test = 89.5
	Average Test = 89.5
	Average Training = 97.39999999999982
Rule: ACCURACY full.
Accuracy: 
	Max Test = 88.5
	Average Test = 88.5
	Average Training = 100.0
	
*****************************************
Results
*****************************************
The method that yielded the highest accuracy for me was using Entropy Information 
Gain in addition to using the chi-square test to determine stopping criteria. In 
addition to testing which methods performed better, I also tuned the thresholds 
for what is considered a statistically significant result for the chi-square test
in the Attr.isSignificant method. I finally set on 3.36 for attributes with 4 
possible values and 2.37 for attributes with 3 possible values.

I believe the Entropy Information Gain classifier is better than my hand built
one. This is not only because it yielded slightly higher accuracy than the hand
built one, but also because after a certain level of splitting, it becomes easier
for an algorithm to recognize what attributes should be split on next instead of 
a human being. Both classifiers use very similar features to start out with: safety
is first, followed by persons. However, after the first few attributes, the 
DecisionTree becomes better than a human at generalizing concepts from the data.