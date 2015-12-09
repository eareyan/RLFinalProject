package randomMDP;


import java.util.Random;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;

public class RandomMDPReward implements RewardFunction{
	Random randomGenerator;
	double meanReward;
	double[][][] rewards;
	public RandomMDPReward(Random randomGenerator){
		this.randomGenerator = randomGenerator;
		this.rewards = new double[10][10][2];
		//Initialize the rewards matrix. 
		for(int i=0;i<10;i++){
			for(int j=0;j<10;j++){
				for(int k=0;k<2;k++){
					this.rewards[i][j][k] = -1.0;
				}
			}
		}
	}
	public double reward(State s, GroundedAction a, State sprime){
		/*
		 * From the paper (Jiang, et al.)
		 * The mean rewards were likewise sampled uniformly and independently from [0, 1], 
		 * and the actual reward signals have additive Gaussian noise with standard deviation 0.1.
		 * 
		 */
		int sNode = GraphDefinedDomain.getNodeId(s) , sprimeNode = GraphDefinedDomain.getNodeId(sprime), action = (a.actionName().equals("action0"))?0:1;
		//Make sure we use the same reward for the same state,action,next state tuple.
		if(rewards[sNode][sprimeNode][(a.actionName().equals("action0"))?0:1] == -1.0){
			rewards[sNode][sprimeNode][action] = this.randomGenerator.nextDouble();
		}
		return rewards[sNode][sprimeNode][action];				
	}
}