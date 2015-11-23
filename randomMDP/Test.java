package randomMDP;


public class Test {

    public static void main(String[] args) {
    	System.out.println("RLProject");
    	/*
    	 * Generate a sample from randomMDP distribution
    	 */
    	MDP RandomMDP = new MDP();
    	/*
    	 * You can run policy iteration and get the value of the
    	 * optimal policy for different values of gamma: 0.3, 0.6 and 0.99 for example
    	 */
    	RandomMDP.getValuePolicy(0.3);
    	RandomMDP.getValuePolicy(0.6);
    	RandomMDP.getValuePolicy(0.99);
    	
    	/*
    	 * Simulate a trajectory in this random MDP of a given length, for example 10
    	 */
    	System.out.println(RandomMDP.SimulateMDP(10));
    	
    	/*
    	 * We can also generate data sets
    	 */
    	RandomMDP.generateDataSet(5);
    }
}
