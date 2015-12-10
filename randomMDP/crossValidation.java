package randomMDP;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Domain;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;

public class crossValidation {
	
	public static double[] cv() {
    	final double gammaEval=0.99;//from the paper - this should not be changed
    	
    	int[] trajectories ={5,10,20,50,100,200};
    	double[] possibleGammas={0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,0.99};
    	double[][] utilityGammaTrajectoryPair=new double [possibleGammas.length][trajectories.length];
    	double[] bestGammas = new double[trajectories.length];
    	
    	int numberOfExperiments = 5;
    	for (int tSize=0;tSize<trajectories.length;tSize++){
    		for(int g=0;g<possibleGammas.length;g++){
    			for(int count=0;count<numberOfExperiments;count++){
    				RandomMDP InstanceOfRandomMDP = new RandomMDP();
    				String[] TrainingData=InstanceOfRandomMDP.Trajectories(trajectories[tSize]).split("\n");
    				String[] fold1=Arrays.copyOfRange(TrainingData, 0, TrainingData.length/3);
    				String[] fold2=Arrays.copyOfRange(TrainingData, TrainingData.length/3, (2*TrainingData.length)/3);
    				String[] fold3=Arrays.copyOfRange(TrainingData, (2*TrainingData.length)/3, TrainingData.length);
    				utilityGammaTrajectoryPair[g][tSize] = utilityOf1Fold(fold1,fold2,fold3,possibleGammas[g],gammaEval,InstanceOfRandomMDP.dg,InstanceOfRandomMDP.domain,InstanceOfRandomMDP.hashFactory);
    				utilityGammaTrajectoryPair[g][tSize] = utilityGammaTrajectoryPair[g][tSize]+utilityOf1Fold(fold1,fold3,fold2,possibleGammas[g],gammaEval,InstanceOfRandomMDP.dg,InstanceOfRandomMDP.domain,InstanceOfRandomMDP.hashFactory);
    				utilityGammaTrajectoryPair[g][tSize] = utilityGammaTrajectoryPair[g][tSize]+utilityOf1Fold(fold2,fold3,fold1,possibleGammas[g],gammaEval,InstanceOfRandomMDP.dg,InstanceOfRandomMDP.domain,InstanceOfRandomMDP.hashFactory);
    				}
    			}
    		System.out.println("tsize " + Integer.toString(tSize)+" is done");
    		}
    	for (int g=0;g<possibleGammas.length;g++){
    		for(int tSize=0;tSize<trajectories.length;tSize++){
    			System.out.printf("%.2f",utilityGammaTrajectoryPair[g][tSize]);
    			System.out.print(" ");
    		}
    		System.out.println();
    	}
    	
    	for (int tSize=0;tSize<trajectories.length;tSize++){
    		double max=-1;
    		int argmax=-1;
    		for(int g=0;g<possibleGammas.length;g++){
    			if(utilityGammaTrajectoryPair[g][tSize]>max){
    				max=utilityGammaTrajectoryPair[g][tSize];
    				argmax=g;
    			}
    		}
    		System.out.println(possibleGammas[argmax]);
    		bestGammas[tSize] = possibleGammas[argmax];
    	}
    	return bestGammas;
    	
	}
	public static double utilityOf1Fold(String[] foldtrain1,String[] foldtrain2,String[] foldtest,double gammaLearn,double gammaEval,DomainGenerator dg,Domain domain,SimpleHashableStateFactory hashFactory){
		EstimatedMDP estimatedMDP = new EstimatedMDP(toExperience(ArrayUtils.addAll(foldtrain1, foldtrain2)), dg ,domain, hashFactory);
		PolicyIteration PI=estimatedMDP.getPolicyIterationOutput(gammaLearn,1000);
		Policy bestPolicyOnEstimatedMDP = PI.getComputedPolicy();
		
		EstimatedMDP testMDP = new EstimatedMDP(toExperience(foldtest), dg ,domain, hashFactory);
		
		PolicyIteration PI2 = new PolicyIteration(testMDP.domain, testMDP.rf, testMDP.tf, gammaEval , testMDP.hashFactory, 0.001, 1000, 1);// create
		PI2.setPolicyToEvaluate(bestPolicyOnEstimatedMDP);//feed PI3 by policy found from estimated mdp
    	PI2.planFromState(testMDP.initState);//do the actual policy evaluation
    	PI2.toggleDebugPrinting(false); // do not print BURLAP stuff
    	
    	double temp=0;
    	for(int i=0;i<10;i++){
    		temp += PI2.value(GraphDefinedDomain.getState(testMDP.domain, i));
    	}
		// TODO Auto-generated method stub
		return temp/10; 
	}
	
	public static String toExperience(String [] tuples){
		String temp="";
		for(int i=0;i<tuples.length;i++){
			if (i==0){
				temp=temp+tuples[i];
			}
			else{
				temp=temp+"\n"+tuples[i];
			}
		}
		return temp;
	}
}