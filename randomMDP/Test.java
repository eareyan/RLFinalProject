package randomMDP;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.oomdp.core.Domain;

public class Test {

    public static void main(String[] args) throws CloneNotSupportedException, FileNotFoundException, UnsupportedEncodingException {

    	MDP RandomMDP = new MDP();
    	double gammaEval=0.99;//from the paper - this should not be changed
    	
    	double gammaEstimate=0.3;//[0.3,0.6,0.99] 	 **** loop over 
    	//int numTrajectories=2000;//[1 10 20 30.. 200]**** these two
    											//**** also for each (gamma,trajectory) loss should be computed from 1000 samples and the average is computed. 
    											// so 3 "for"s is needed here.
    	    	
    	double[] gammas = new double[3];
    	gammas[0] = 0.3;
    	gammas[1] = 0.6;
    	gammas[2] = 0.99;
    	
    	int[] trajectories = new int[9];
    	trajectories[0] = 2;
    	trajectories[1] = 4;
    	trajectories[2] = 6;
    	trajectories[3] = 8;
    	trajectories[4] = 10;
    	trajectories[5] = 20;
    	trajectories[6] = 50;
    	trajectories[7] = 100;
    	trajectories[8] = 120;
    	
    	int numExperiments = 1000;
    	int currNumberTrajectory = 1;
    	double tempLoss = 0.0;
    	double currLoss = 0.0;
    	PrintWriter writer = new PrintWriter("planningloss-"+numExperiments+".csv", "UTF-8");

    	for(int g=0;g<gammas.length;g++){
			gammaEstimate = gammas[g];
    		for(int t=0;t<trajectories.length;t++){
    			tempLoss = 0.0;
            	System.out.println("Gamma = " + gammaEstimate + " and numberOfTrajectories=" + trajectories[t]);
    			for(int i=0;i<numExperiments;i++){
            		currNumberTrajectory = trajectories[t];
            		PolicyIteration PI=RandomMDP.getPolicyIterationOutput(gammaEval,1000);//this is the best policy on the original mdp
                	//printValues(PI,RandomMDP.domain);
                	
                	MDP RandomMDPestimated =(MDP) RandomMDP.clone();//copy the previous mdp to randomMDPEstimated -- we will change model below
                	String Exp=RandomMDPestimated.Trajectories(currNumberTrajectory);//sample a bunch of trajectories
                	RandomMDPestimated.estimateModel(Exp);//change the MDP by estimating the reward and transition model from sampled experience
                	PolicyIteration PI2=RandomMDPestimated.getPolicyIterationOutput(gammaEstimate,1000);//find the best policy on estimated mdp 
                	//printValues(PI2,RandomMDPestimated.domain);
                	Policy bestPolicyOnEstimatedMDP = PI2.getComputedPolicy();//extract policy from PI2
                	
                	PolicyIteration PI3 = new PolicyIteration(RandomMDP.domain, RandomMDP.rf, RandomMDP.tf, gammaEval , RandomMDP.hashFactory, 0.001, 1000, 1);// create
            		PI3.setPolicyToEvaluate(bestPolicyOnEstimatedMDP);//feed PI3 by policy found from estimated mdp
                	PI3.planFromState(RandomMDP.initState);//do the actual policy evaluation
                	PI3.toggleDebugPrinting(false); // do not print BURLAP stuff
                	
                	currLoss = loss(PI,PI3,RandomMDP.domain);
                	tempLoss += currLoss;    				
                	System.out.println(i+":"+currLoss);
    			}
    			System.out.println("Average Loss: " + (tempLoss / numExperiments));
    			writer.println(gammaEstimate + "," + currNumberTrajectory + "," + (tempLoss / numExperiments));
    		}
		}
    	writer.close();
    	
    }
    private static double loss(PolicyIteration first, PolicyIteration second, Domain domain) {
    	double max=-1;
    	for(int i=0;i<10;i++){
    		double difference= abs(first.value(GraphDefinedDomain.getState(domain, i)) - second.value(GraphDefinedDomain.getState(domain, i) ) );
    		if (max<difference){
    			max = difference;
    		}
    	}
		// TODO Auto-generated method stub
		return max;
	}
    public static double abs(double a) {
        return (a <= 0.0F) ? 0.0F - a : a;
    }
	public static void printValues(PolicyIteration PI,Domain domain){
    	for(int i=0;i<10;i++){
    		System.out.println(PI.value(GraphDefinedDomain.getState(domain, i)));
    	}
    }
}