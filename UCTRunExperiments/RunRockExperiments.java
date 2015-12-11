package finalProject;

import java.util.ArrayList;
import java.util.List;

import finalProject.Domain.RockSampleDG;
import finalProject.Domain.RockSampleInitialStateGenerator;
import finalProject.Domain.RockSampleRF;
import finalProject.Domain.RockSampleTF;
import finalProject.Domain.SimulatedBeliefPOEnvironment;
import burlap.behavior.policy.BeliefPolicyToPOMDPPolicy;
import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.planning.stochastic.montecarlo.uct.UCT;
import burlap.behavior.singleagent.pomdp.BeliefPolicyAgent;
import burlap.debugtools.DPrint;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.pomdp.BeliefMDPGenerator;
import burlap.oomdp.singleagent.pomdp.PODomain;
import burlap.oomdp.singleagent.pomdp.SimulatedPOEnvironment;
import burlap.oomdp.singleagent.pomdp.beliefstate.BeliefState;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;



public class RunRockExperiments {

	/**
	 * Runs a single instance of UCT for the given number of trajectories and planning depth.
	 * @param numTrajectories
	 * @param planningDepth
	 * @return
	 */
	public static double runUCT(int numTrajectories, int planningDepth) {

		// Make the Rock Sample Domain and the partially observable environment for the agent to act in.
		int width = 7;
		int height = 8;
		int numRocks = 2;
		RockSampleDG dg = new RockSampleDG(width, height, numRocks);
		PODomain domain = (PODomain)dg.generateDomain();
		RewardFunction rf = new RockSampleRF(width);
		TerminalFunction tf = new RockSampleTF(width);
		State initialState = RockSampleInitialStateGenerator.getInitialState(domain, width, height, numRocks);
		domain.getStateEnumerator().findReachableStatesAndEnumerate(initialState);
		BeliefState initialBeliefState = RockSampleInitialStateGenerator.getInitialBeliefState(domain, initialState);
		
		// Turn domain into a Belief MDP.
		BeliefMDPGenerator mdpGen = new BeliefMDPGenerator(domain);
		Domain bdomain = mdpGen.generateDomain();
		RewardFunction brf = new BeliefMDPGenerator.BeliefRF(domain, rf);
				
		SimulatedBeliefPOEnvironment penv = new SimulatedBeliefPOEnvironment(domain, rf, tf, initialState);
		
		
		// Create and run UCT.
		double gamma = 1.0;
		int explorationBias = 60;
		HashableStateFactory hf = new SimpleHashableStateFactory();
		UCT planner = new UCT(bdomain, brf, tf, gamma, hf, planningDepth, numTrajectories, explorationBias);
		GreedyQPolicy policy = planner.planFromState(initialBeliefState); // NOTE: Should be planning over belief state.
		
//		BeliefPolicyToPOMDPPolicy pomdpPolicy = new BeliefPolicyToPOMDPPolicy(policy); 
		
		// Create agent using UCT's policy and evaluate the agent.
		BeliefPolicyAgent agent = new BeliefPolicyAgent(domain, penv, policy);
		agent.setBeliefState(initialBeliefState);
		int maxStepsForRollout = 50;
		EpisodeAnalysis ea = agent.actUntilTerminalOrMaxSteps(maxStepsForRollout);
		
		System.out.println(ea.actionSequence);
		return sumRewards(ea.rewardSequence);
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
		int[] trajectories = new int[] {100, 1000};
		int[] planningDepths = new int[] {4, 6, 8, 10};
		int numTrials = 100;
		
		List<Double> avgCumulativeRewards = new ArrayList<Double>();
		
		System.out.println("Running UCT Experiments...\n");
		for (int i= 0; i < trajectories.length; i++) {
			System.out.println("Trajectory: " + trajectories[i]);
			for (int j = 0; j < planningDepths.length; j++) {
				System.out.println("\tPlanning Depth: " + planningDepths[j]);
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
