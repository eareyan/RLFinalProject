package randomMDP;


/*
 * RL Final Project, Fall 2015
 * The goal is to recreate figures 6 and 7 from the paper: 
 * The Dependence of Effective Planning Horizon on Model Accuracy, by
 * Nan Jiang, Alex Kulesza, Satinder Singh, and Richard Lewis.
 * 
 * This class in particular generates the RANDOM-MDP distribution,
 * as explained in the paper. It also has helper methods to generate
 * datasets. 
 */

import java.util.Random;

import burlap.oomdp.core.states.State;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TerminalFunction;
import burlap.behavior.policy.RandomPolicy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.auxiliary.common.NullTermination;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;

public class MDP implements Cloneable {
	
	DomainGenerator dg;
	Domain domain;
	State initState;
	RewardFunction rf;
	TerminalFunction tf;
	SimpleHashableStateFactory hashFactory;
	Random randomGenerator;
	private static boolean verbose = true;
	
	public MDP(){
		this.dg = new GraphDefinedDomain(10);
		this.randomGenerator = new Random();
		/*
		 * Set transitions. From the paper (Jiang, et al.)
		 * For each state-action pair (s, a), the distribution over the next state, P (s, a, ·), is
		 * determined by choosing 5 non-zero entries uniformly from all 10 states, filling these 5 entries 
		 * with values uniformly drawn from [0, 1], and finally normalizing P (s, a, ·).
		 */
		double[] distribution;
		for(int s=0;s<10;s++){
			for(int a=0;a<2;a++){
				distribution = this.createDistribution();
				/*System.out.println("Setting transition for state-action pair (s,a) = ("+s+","+a+")");
				this.printDistribution(distribution);*/
				for(int sprime=0;sprime<10;sprime++){
					((GraphDefinedDomain) this.dg).setTransition(s, a, sprime, distribution[sprime]);
				}

			}
		}
		this.domain = this.dg.generateDomain();
		this.initState = GraphDefinedDomain.getState(this.domain, 0); 
		//this.rf = new SpecificRF(this.randomGenerator);
		this.rf = new RandomMDPRewardV1(this.randomGenerator);
		this.tf = new NullTermination();
		this.hashFactory = new SimpleHashableStateFactory();	
		
	}
	
	protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
	
	public void estimateModel(String experience){
		//String[][] myStringArray = new String [x][y];
		int numStates=10;
		int numActions=2;
		float [][][] estimatedTransition = new float [numStates][numActions][numStates];
		float [][][] estimatedReward = new float [numStates][numActions][numStates];
		String [] listOfExperience = experience.split("\n");
		for(int i=0;i<listOfExperience.length;i++){
			String [] eachExperience=listOfExperience[i].split(",");
			int s=Integer.parseInt(eachExperience[0]);
			int a=Integer.parseInt(eachExperience[1].substring(eachExperience[1].length() - 1));
			float r=Float.parseFloat(eachExperience[2]);
			int sprime=Integer.parseInt(eachExperience[3]);
			estimatedTransition[s][a][sprime]=estimatedTransition[s][a][sprime]+1;
			estimatedReward[s][a][sprime]=estimatedReward[s][a][sprime]+r;
		}
		
		float [][][] counts = estimatedTransition;
		
		for (int i=0;i<10;i++){
			for (int j=0;j<2;j++){
				for(int k=0;k<10;k++){
					if (counts[i][j][k]!=0){
					estimatedReward[i][j][k]=estimatedReward[i][j][k] / counts[i][j][k];
					}
					else{
						estimatedReward[i][j][k]=(float) 0.5;// this could be something more clever!
					}
				}
			}
		}
		/*
		System.out.println("Reward:");
		for (int i=0;i<10;i++){
			for (int j=0;j<10;j++){
				System.out.print(estimatedReward[i][0][j]+ " ");
			}
			System.out.println("");
		}
		System.out.println("");
		
		for (int i=0;i<10;i++){
			for (int j=0;j<10;j++){
				System.out.print(estimatedReward[i][1][j]+ " ");
			}
			System.out.println("");
		}
		System.out.println("");
		System.out.println("Transition:");
		*/
		for (int i=0;i<10;i++){
			for (int j=0;j<2;j++){
				float temp=0;
				for (int k=0;k<10;k++){
				temp=temp+estimatedTransition[i][j][k];
				}
				for (int k=0;k<10;k++){
					if (temp>0){
						estimatedTransition[i][j][k]=estimatedTransition[i][j][k]/temp;
					}
					else{
						estimatedTransition[i][j][k]=(float) 0.1;
					}
					}
				
			}
		}
		//sets the transition and reward
		for(int s=0;s<10;s++){
			for(int a=0;a<2;a++){
				for(int sprime=0;sprime<10;sprime++){
					((GraphDefinedDomain) this.dg).setTransition(s, a, sprime, estimatedTransition[s][a][sprime]);
				}

			}
		}
		this.rf = new EstimatedRF(estimatedReward);
		/*
		for (int i=0;i<10;i++){
			for (int j=0;j<10;j++){
				System.out.print(estimatedTransition[i][0][j]+ " ");
			}
			System.out.println("");
		}
		System.out.println("");
		for (int i=0;i<10;i++){
			for (int j=0;j<10;j++){
				System.out.print(estimatedTransition[i][1][j]+ " ");
			}
			System.out.println("");
		}
		*/
	}
	/*
	 * Create distribution for next state transitions.
	 */
	protected double[] createDistribution(){
		
		double[] distribution = new double[10];
		int[] entriesSoFar = new int[10];
		/* Initialize the distribution and entriesSoFar to all zeros*/
		for(int i=0;i<10;i++){
			distribution[i] = 0.0;
			entriesSoFar[i] = 0;
		}
		int randomInt,countDifferentEntries = 0;
		double randomDouble, total = 0.0;
		/* While we don't have 5 different entries, keep looking for entries */
		while(countDifferentEntries < 5){
			/*
			 * Choose 5 entries from the 10 entries and fill these 5 entries 
			 * with values uniformly drawn from [0, 1]
			 */
			randomInt = this.randomGenerator.nextInt(10);
			if(entriesSoFar[randomInt] == 0){ //The entry in randomInt has not yet been chosen
				entriesSoFar[randomInt] = 1;  //Mark the entry as chosen
				randomDouble = this.randomGenerator.nextDouble();
				distribution[randomInt] += randomDouble;
				total += randomDouble;
				countDifferentEntries++;
			}
		}
		/*
		 * Normalize the distribution
		 */
		for(int i=0;i<10;i++){
			distribution[i] = distribution[i] / total;
		}
		return distribution;
	}
	//Enrique's reward
	public static class SpecificRF implements RewardFunction{
		Random randomGenerator;
		double meanReward;
		double[][][] rewards;
		public SpecificRF(Random randomGenerator){
			this.randomGenerator = randomGenerator;
			this.meanReward = this.randomGenerator.nextDouble();
			if(MDP.verbose){
				System.out.println("Actual mean reward = "+this.meanReward);
			}
		}
		public double reward(State s, GroundedAction a, State sprime){
			/*
			 * From the paper (Jiang, et al.)
			 * The mean rewards were likewise sampled uniformly and independently from [0, 1], 
			 * and the actual reward signals have additive Gaussian noise with standard deviation 0.1.
			 * Also, emails with the authors:
			 * For each s,a pair, we set the "true" mean R(s,a) to be an iid sample from [0 1]. So two different s,a pairs in general have totally unrelated rewards. But the rewards that we actually observe in our synthetic data for a given s,a are not precisely R(s,a) -- they have added Gaussian noise. So for instance if R(s,a) = 0.5, we will see rewards in the data like 0.49, 0.52, etc.
			 * 
			 * Yes, Alex's answers are correct, and I think for the second question Enrique's original understanding is also correct. The mean reward (or you called it "baseline") is sampled only once when we generate the MDP specification; the Gaussian noise is added whenever we sample trajectories from the MDP.
			 */
			return this.meanReward;
		}
	}
	//Kavosh's reward
	public static class EstimatedRF implements RewardFunction{
		double[][][] rewards;
		public EstimatedRF(float [][][] rew){
			rewards= new double [10][2][10];
			for(int s=0;s<10;s++){
				for(int sprime=0;sprime<10;sprime++){
					//System.out.println(s+","+sprime);
					rewards[s][0][sprime]=(double) rew[s][0][sprime];
					rewards[s][1][sprime]=(double) rew[s][1][sprime];
				}
			}
		}
		public double reward(State s, GroundedAction a, State sprime){
			return rewards[GraphDefinedDomain.getNodeId(s)][(a.actionName().equals("action0"))?0:1][GraphDefinedDomain.getNodeId(sprime)];
		}
	}
	/*
	 * This function performs PolicyIteration in the Random-MDP defined by this object.
	 */
	public PolicyIteration getPolicyIterationOutput(double gamma, int maxInt){
		PolicyIteration PI = new PolicyIteration(this.domain, this.rf, this.tf, gamma , this.hashFactory, 0.001, 1000, maxInt);// why is it 1?
		PI.toggleDebugPrinting(false); // do not print BURLAP stuff
		PI.planFromState(this.initState);
		return PI;
	}
	/*
	 * This method simulates the MDP and returns an string
	 * with the result of the simulation for n trajectories.
	 */
	public String singleTrajectory(){
		int n=10;
		n++; //We want exactly n trajectories...
		RandomPolicy R = new RandomPolicy(this.domain); //This object simulates the MDP by uniformly picking actions
		//We will simulate from a random initial state
		EpisodeAnalysis data = R.evaluateBehavior(GraphDefinedDomain.getState(this.domain, this.randomGenerator.nextInt(10)) , this.rf, n);
		String trajectory = "";
		for(int i=0;i<n;i++){
			trajectory += GraphDefinedDomain.getNodeId(data.getState(i)) + ",";
			if(i<n-1){
				trajectory += data.getAction(i) + ",";
				/*Email from Nan Jiang:  The mean reward (or you called it "baseline") is sampled only once when we generate the MDP specification; the Gaussian noise is added whenever we sample trajectories from the MDP.*/
				trajectory += (data.rewardSequence.get(i)+ (0.1*this.randomGenerator.nextGaussian())) + ",";
			}
			if(i<n-2) trajectory += GraphDefinedDomain.getNodeId(data.getState(i+1)) + "\n";
		}
		return trajectory.substring(0, trajectory.length()-1);
	}
	public String Trajectories(int numberOfTrajectories){
		String temp="";
		for(int i=0;i<numberOfTrajectories;i++){
			if (i==0){
				temp=temp+singleTrajectory();
			}
			else{
				temp=temp+"\n"+singleTrajectory();
			}
		}
		return temp;
	}
	/*
	 * Method that generates a dataset.
	 * A dataset is defined as a number n of trajectories, each of length 10,
	 * again, from the paper: For each generated MDP M , and for each value of n ∈ {5, 10, 20, 50}, 
	 * we independently generated 1,000 data sets, each consisting of n trajectories of length 10 starting at uniformly
	 * random initial states and choosing uniformly random actions.
	 */
	public void generateDataSet(int n){
		for(int i=0;i<n;i++){
			System.out.println("Generate data set "+i); //In the future we could store these data sets in files...
			System.out.println(singleTrajectory());
		}
	}
	/*
	 * Some helper methods
	 */
	protected void printDistribution(double[] distribution){
		for(int i=0;i<10;i++){
			System.out.println(i+"="+distribution[i]);
		}
	}
}
