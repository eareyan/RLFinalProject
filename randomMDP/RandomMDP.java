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
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.RandomPolicy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.auxiliary.common.NullTermination;
import burlap.oomdp.singleagent.RewardFunction;

public class RandomMDP {
	
	DomainGenerator dg;
	Domain domain;
	State initState;
	RewardFunction rf;
	TerminalFunction tf;
	SimpleHashableStateFactory hashFactory;
	Random randomGenerator;
	private static boolean verbose = true;
	
	public RandomMDP(){
		this.dg = new GraphDefinedDomain(10);
		this.randomGenerator = new Random(System.currentTimeMillis());
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
		this.rf = new RandomMDPReward();
		this.tf = new NullTermination();
		this.hashFactory = new SimpleHashableStateFactory();
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
	/*
	 * This function performs PolicyIteration in the Random-MDP defined by this object.
	 */
	public PolicyIteration getPolicyIterationOutput(double gamma, int maxInt){
		PolicyIteration PI = new PolicyIteration(this.domain, this.rf, this.tf, gamma , this.hashFactory, 0.05, 1000, maxInt);// why is it 1?
		PI.toggleDebugPrinting(false); // do not print BURLAP stuff
		PI.planFromState(this.initState);
		return PI;
	}
	/*
	 * This function performs PolicyIteration in the Random-MDP defined by this object.
	 */
	public PolicyIteration getPolicyFromInitialPolicy(double gamma, int maxInt,Policy initialPolicy){
		PolicyIteration PI = new PolicyIteration(this.domain, this.rf, this.tf, gamma , this.hashFactory, 0.05, 1000, maxInt);// why is it 1?
		PI.toggleDebugPrinting(false); // do not print BURLAP stuff
		PI.setPolicyToEvaluate(initialPolicy);
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
		int s,a,sprime;
		double[][][] rewardNoise;
		
		rewardNoise = new double[10][2][10];
		for(int i=0;i<10;i++){
			for(int j=0;j<2;j++){
				for(int k=0;k<10;k++){
					rewardNoise[i][j][k] = Double.MIN_VALUE;
				}
			}
		}		
		for(int i=0;i<n;i++){
			s = GraphDefinedDomain.getNodeId(data.getState(i));
			trajectory += s + ",";
			if(i<n-1){
				a = (data.getAction(i).actionName().equals("action0")?0:1);
				sprime = GraphDefinedDomain.getNodeId(data.getState(i+1));
				/* First check if this noise has already been computed */
				//if(rewardNoise[s][a][sprime] == Double.MIN_VALUE){
					rewardNoise[s][a][sprime] = 0.1*this.randomGenerator.nextGaussian();
				//}
				trajectory += data.getAction(i) + ",";
				/*Email from Nan Jiang:  The mean reward (or you called it "baseline") is sampled only once when we generate the MDP specification; the Gaussian noise is added whenever we sample trajectories from the MDP.*/
				trajectory += (data.rewardSequence.get(i)+ rewardNoise[s][a][sprime]) + ",";
				//trajectory += data.rewardSequence.get(i) +",";
				if(i<n-2) trajectory += sprime + "\n";
			}
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
