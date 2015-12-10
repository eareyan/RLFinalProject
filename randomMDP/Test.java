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

    	final double gammaEval=0.99;//from the paper - this should not be changed
    	    	
    	double[] gammas = new double[3];
    	gammas[0] = 0.3;
    	gammas[1] = 0.6;
    	gammas[2] = 0.99;
    	
    	int[] trajectories = new int[6];
    	trajectories[0] = 5;
    	trajectories[1] = 10;
    	trajectories[2] = 20;
    	trajectories[3] = 50;
    	trajectories[4] = 100;
    	trajectories[5] = 200;
    	
    	int numExperiments = 100;
    	double tempLoss = 0.0;
    	double currLoss = 0.0;
    	int numberOfPolicyIterations = 1000;
    	
    	
    	double[] cvValues = crossValidation.cv();
    	
    	PrintWriter writer = new PrintWriter("planningloss-"+numExperiments+".csv", "UTF-8");
		

    	    	
    	for(int g=0;g<gammas.length+1;g++){
    		for(int t=0;t<trajectories.length;t++){
    			double currentGamma = pickGamma(g,t,cvValues,gammas);
    			tempLoss = 0.0;
    			for(int i=0;i<numExperiments;i++){
    				RandomMDP InstanceRandomMDP = new RandomMDP();
    				
    				
    				/* Optimal Policy random MDP*/
            		PolicyIteration PI=InstanceRandomMDP.getPolicyIterationOutput(gammaEval,numberOfPolicyIterations);//this is the best policy on the original mdp
                	
                	EstimatedMDP RandomMDPestimated = new EstimatedMDP(InstanceRandomMDP.Trajectories(trajectories[t]),InstanceRandomMDP.dg,InstanceRandomMDP.domain,InstanceRandomMDP.hashFactory);
                	PolicyIteration PI2=RandomMDPestimated.getPolicyIterationOutput(currentGamma,numberOfPolicyIterations);//find the best policy on estimated mdp 
                	Policy bestPolicyOnEstimatedMDP = PI2.getComputedPolicy();//extract policy from PI2
                	
                	PolicyIteration PI3 = InstanceRandomMDP.getPolicyFromInitialPolicy(gammaEval, 1, bestPolicyOnEstimatedMDP);
                	
                	//currLoss = loss(PI,PI3,RandomMDP.domain);
                	//System.out.println(loss(PI,PI3,RandomMDP.domain,RandomMDP.domain));
                	//System.out.println(loss(PI,PI3,RandomMDP.domain,RandomMDPestimated.domain));
                	//System.out.println(loss(PI,PI3,RandomMDPestimated.domain,RandomMDPestimated.domain));
                	//System.exit(0);
                	currLoss = loss(PI,PI3,InstanceRandomMDP.domain);
                	tempLoss += currLoss;    				
                	//System.out.println(i+":"+currLoss);
    			}
    			System.out.print(trajectories[t] + "," + (tempLoss / numExperiments) + "\t" );
    			writer.println(currentGamma + "," + trajectories[t] + "," + (tempLoss / numExperiments));
    		}
    		System.out.println();
		}
    	writer.close();
    }
    private static double loss(PolicyIteration first, PolicyIteration second, Domain domain) {
    	double temp=0;
    	for(int i=0;i<10;i++){
    		temp += first.value(GraphDefinedDomain.getState(domain, i)) - second.value(GraphDefinedDomain.getState(domain, i));
    	}
		// TODO Auto-generated method stub
		return temp/10;
	}
    private static double pickGamma(int g,int t,double[] cv,double[] fixedgammas){
    	if(g<3){
    		return fixedgammas[g];
    	}else{
    		return cv[t];
    	}
    	
    }
	public static void printValues(PolicyIteration PI,Domain domain){
    	for(int i=0;i<10;i++){
    		System.out.println(PI.value(GraphDefinedDomain.getState(domain, i)));
    	}
    }
}