package final_project;

import java.util.ArrayList;
import java.util.List;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.singleagent.planning.stochastic.montecarlo.uct.UCT;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.pomdp.PODomain;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import burlap.debugtools.DPrint;
import rocksampledomain.RockSampleDG;
import rocksampledomain.RockSampleInitialStateGenerator;
import rocksampledomain.RockSampleRF;
import rocksampledomain.RockSampleTF;


public class RunRockExperiments {

	/**
	 * Runs a single instance of UCT for the given number of trajectories and planning depth.
	 * @param numTrajectories
	 * @param planningDepth
	 * @return
	 */
	public static double runUCT(int numTrajectories, int planningDepth) {

		// Make the Rock Sample Domain.
		int width = 7;
		int height = 8;
		int numRocks = 1;
		RockSampleDG dg = new RockSampleDG(width, height, numRocks);
		PODomain domain = (PODomain)dg.generateDomain();
		RewardFunction rf = new RockSampleRF(width);
		TerminalFunction tf = new RockSampleTF(width);
		State initialBeliefState = RockSampleInitialStateGenerator.getInitialState(domain, width, height, numRocks);
		domain.getStateEnumerator().findReachableStatesAndEnumerate(initialBeliefState);
		
		// Run UCT.
		double gamma = 1.0;
		int explorationBias = 60;
		HashableStateFactory hf = new SimpleHashableStateFactory();
		UCT planner = new UCT(domain, rf, tf, gamma, hf, planningDepth, numTrajectories, explorationBias);
		GreedyQPolicy policy = planner.planFromState(initialBeliefState);
		System.out.println(policy.evaluateBehavior(initialBeliefState, rf, tf, 100).actionSequence);
		return sumRewards(policy.evaluateBehavior(initialBeliefState, rf, tf, 100).rewardSequence);
	}
	
	/**
	 * Computes the sum of a given reward sequence.
	 * @param rewardSequence: a list containing rewards from an episode.
	 * @return
	 */
	private static double sumRewards(List<Double> rewardSequence) {
		double total = 0.0;
		for (Double d : rewardSequence) {
			total += d;
		}
		return total;
	}
	
	/**
	 * Runs @numTrials instances of UCT for the given number of trajectories and planning depth and averages the resulting cumulative rewards.
	 * @param numTrials
	 * @param numTrajectories
	 * @param planningDepth
	 * @return
	 */
	public static double runManyUCTAndAverageRewards(int numTrials, int numTrajectories, int planningDepth) {
		
		double cumulativeReward = 0.0;
		for (int i = 0; i < numTrials; i++) {
			cumulativeReward += runUCT(numTrajectories, planningDepth);
		}
		return cumulativeReward / ((double) numTrials);
	}
	
	/**
	 * Runs all UCT experiments that generate results for the plot. 
	 */
	public static void runUCTExperiments() {
		int[] trajectories = new int[] {5000, 20000, 100000};
		int[] planningDepths = new int[] {4, 6, 8, 10, 15, 20};
		int numTrials = 25;
		
		List<Double> avgCumulativeRewards = new ArrayList<Double>();
		
		for (int i= 0; i < trajectories.length; i++) {
			for (int j = 0; j < planningDepths.length; j++) {
				double result = runManyUCTAndAverageRewards(numTrials, trajectories[i], planningDepths[j]);
				avgCumulativeRewards.add(result);
			}
		}
		
		System.out.println(avgCumulativeRewards);
	}
	
	public static void main(String[] args) {
		DPrint.toggleUniversal(false);
		runUCTExperiments();
	}
	
	
}
