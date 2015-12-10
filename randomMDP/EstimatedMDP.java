package randomMDP;

import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.auxiliary.common.NullTermination;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;

public class EstimatedMDP {
	DomainGenerator dg;
	Domain domain;
	State initState;
	RewardFunction rf;
	TerminalFunction tf;
	SimpleHashableStateFactory hashFactory;
	
	public EstimatedMDP(String experience,DomainGenerator dg,Domain domain,SimpleHashableStateFactory hashFactory){
		//this.dg = new GraphDefinedDomain(10);
		this.dg = dg;
		
		//this.domain = this.dg.generateDomain();
		this.domain = domain;
		double[][][] estimatedRewards = this.estimateModel(experience);
		this.initState = GraphDefinedDomain.getState(this.domain, 0); 
		this.rf = new EstimatedRF(estimatedRewards);
		this.tf = new NullTermination();
		this.hashFactory = hashFactory;	
	}	
	
	protected double[][][] estimateModel(String experience){
		int numStates=10;
		int numActions=2;
		double [][][] estimatedTransition = new double [numStates][numActions][numStates];
		double [][][] estimatedReward = new double [numStates][numActions][numStates];
		String [] listOfExperience = experience.split("\n");
		/*
		 * Parse a string of experience consisting of s,a,r,s lines
		 */
		//System.out.println(experience);
		for(int i=0;i<listOfExperience.length;i++){
			String [] eachExperience=listOfExperience[i].split(",");
			int s=Integer.parseInt(eachExperience[0]);
			int a=Integer.parseInt(eachExperience[1].substring(eachExperience[1].length() - 1));
			double r=Double.parseDouble(eachExperience[2]);
			int sprime=Integer.parseInt(eachExperience[3]);
			estimatedTransition[s][a][sprime]=estimatedTransition[s][a][sprime] + 1;
			estimatedReward[s][a][sprime]=estimatedReward[s][a][sprime] + r;
		}
		
		double [][][] counts = estimatedTransition;
		/*
		 * Compute estimated rewards
		 */
		for (int i=0;i<10;i++){
			for (int j=0;j<2;j++){
				for(int k=0;k<10;k++){
					if (counts[i][j][k]!=0){
						estimatedReward[i][j][k] = estimatedReward[i][j][k] / counts[i][j][k];
					}
				}
			}
		}
		for (int i=0;i<10;i++){
			for (int j=0;j<2;j++){
				for(int k=0;k<10;k++){
					if (counts[i][j][k]==0 && !checkIfObservedSA(counts,i,j)){
						estimatedReward[i][j][k] = 0.5;
					}
				}
			}
		}
		
		/*
		 * Compute estimated Transitions
		 */
		for (int i=0;i<10;i++){
			for (int j=0;j<2;j++){
				double temp = 0;
				for (int k=0;k<10;k++){
					temp = temp + estimatedTransition[i][j][k];
				}
				for (int k=0;k<10;k++){
					if (temp > 0){
						estimatedTransition[i][j][k]=estimatedTransition[i][j][k]/temp;
					}
					else{
						estimatedTransition[i][j][k] = 0.1;
					}
				}
				
			}
		}
		/*
		 * sets the transition and reward
		 */
		for(int s=0;s<10;s++){
			for(int a=0;a<2;a++){
				for(int sprime=0;sprime<10;sprime++){
					((GraphDefinedDomain) this.dg).setTransition(s, a, sprime, estimatedTransition[s][a][sprime]);
				}

			}
		}
		/*
		 * Print some debugging info
		 */
		//this.printEstimatedRewards(estimatedReward);
		//this.printEstimatedTransitions(estimatedTransition);
		return estimatedReward;
	}	
	protected boolean checkIfObservedSA(double[][][] counts,int i,int j){
		for(int k=0;k<10;k++){
			if(counts[i][j][k] != 0.0){
				return true;
			}
		}
		return false;
	}
	//Kavosh's reward
	public static class EstimatedRF implements RewardFunction{
		double[][][] rewards;
		public EstimatedRF(double [][][] rew){
			this.rewards= new double [10][2][10];
			for(int s=0;s<10;s++){
				for(int sprime=0;sprime<10;sprime++){
					//System.out.println(s+","+sprime);
					rewards[s][0][sprime]= rew[s][0][sprime];
					rewards[s][1][sprime]= rew[s][1][sprime];
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
		PolicyIteration PI = new PolicyIteration(this.domain, this.rf, this.tf, gamma , this.hashFactory, 0.05, 1000, maxInt);// why is it 1?
		PI.toggleDebugPrinting(false); // do not print BURLAP stuff
		PI.planFromState(this.initState);
		return PI;
	}
	protected void printEstimatedRewards(double[][][] estimatedReward){
		System.out.println("Estimated Reward Action 0:");
		for (int i=0;i<10;i++){
			for (int j=0;j<10;j++){
				System.out.print(estimatedReward[i][0][j]+ " ");
			}
			System.out.println("");
		}
		System.out.println("Estimated Reward Action 1:");		
		for (int i=0;i<10;i++){
			for (int j=0;j<10;j++){
				System.out.print(estimatedReward[i][1][j]+ " ");
			}
			System.out.println("");
		}
	}
	protected void printEstimatedTransitions(double[][][] estimatedTransition){
		System.out.println("Estimated Transition Action 0:");		
		for (int i=0;i<10;i++){
			for (int j=0;j<10;j++){
				System.out.print(estimatedTransition[i][0][j]+ " ");
			}
			System.out.println("");
		}
		System.out.println("Estimated Transition Action 1:");		
		for (int i=0;i<10;i++){
			for (int j=0;j<10;j++){
				System.out.print(estimatedTransition[i][1][j]+ " ");
			}
			System.out.println("");
		}
	}	
}
