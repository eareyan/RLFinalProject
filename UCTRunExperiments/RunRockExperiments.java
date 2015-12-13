package final_project;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import rocksampledomain.RockSampleDG;
import rocksampledomain.RockSampleInitialStateGenerator;
import rocksampledomain.RockSampleRF;
import rocksampledomain.RockSampleTF;
import rocksampledomain.SimulatedBeliefPOEnvironment;
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
import burlap.oomdp.singleagent.pomdp.beliefstate.BeliefState;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;



public class RunRockExperiments {

	// Make the Rock Sample Domain and the partially observable environment for the agent to act in.
	static int width = 7;
	static int height = 8;
	static int numRocks = 1;
	static RockSampleDG dg = new RockSampleDG(width, height, numRocks);
	static PODomain domain = (PODomain)dg.generateDomain();
	static RewardFunction rf = new RockSampleRF(width);
	static TerminalFunction tf = new RockSampleTF(width);
	
	// Turn domain into a Belief MDP.
	static BeliefMDPGenerator mdpGen = new BeliefMDPGenerator(domain);
	static Domain bdomain = mdpGen.generateDomain();
	static RewardFunction brf = new BeliefMDPGenerator.BeliefRF(domain, rf);
	
	static boolean stateEnumerationDone = false;
	

	
	/**
	 * Runs a single instance of UCT for the given number of trajectories and planning depth.
	 * @param numTrajectories
	 * @param planningDepth
	 * @return
	 */
	public static double runUCT(int numTrajectories, int planningDepth) {

		
		
		State initialState = RockSampleInitialStateGenerator.getInitialState(domain, width, height);
		
		if (!stateEnumerationDone) {
			// Only do this once since the init state is the same for all trials.
			domain.getStateEnumerator().findReachableStatesAndEnumerate(initialState);
			stateEnumerationDone = true;
			System.out.println("\t\tDone enumerating states.");
		}
		
		BeliefState initialBeliefState = RockSampleInitialStateGenerator.getInitialBeliefState(domain, initialState);
		SimulatedBeliefPOEnvironment penv = new SimulatedBeliefPOEnvironment(domain, rf, tf, initialState);
		
		
		// Create and run UCT.
		double gamma = .9;
		int explorationBias = 74; // 10 * exp(2) ish.
		HashableStateFactory hf = new SimpleHashableStateFactory();
		UCT planner = new UCT(bdomain, brf, tf, gamma, hf, planningDepth, numTrajectories, explorationBias);
		
		System.out.println("\t\tStarting to plan.");
		GreedyQPolicy policy = planner.planFromState(initialBeliefState); // NOTE: Should be planning over belief state.
		System.out.println("\t\tDone planning.");
//		BeliefPolicyToPOMDPPolicy pomdpPolicy = new BeliefPolicyToPOMDPPolicy(policy); 
		
		// Create agent using UCT's policy and evaluate the agent.
		BeliefPolicyAgent agent = new BeliefPolicyAgent(domain, penv, policy);
		agent.setBeliefState(initialBeliefState);
		int maxStepsForRollout = 35;
		System.out.println("\t\tEvaluating agent.");
		EpisodeAnalysis ea = agent.actUntilTerminalOrMaxSteps(maxStepsForRollout);
		System.out.println("\t\tDone evaluating agent.");
		penv.resetEnvironment(); // Reset.
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
		int[] trajectories = new int[] {1000}; // 200, 50, 1000
		int[] planningDepths = new int[] {10};
		int numTrials = 10;
		
		List<Double> avgCumulativeRewards = new ArrayList<Double>();
		
		System.out.println("Running UCT Experiments...\n");
		for (int i= 0; i < trajectories.length; i++) {
			System.out.println("Trajectory: " + trajectories[i]);
			for (int j = 0; j < planningDepths.length; j++) {
				System.out.println("\tPlanning Depth: " + planningDepths[j]);
				double result = runManyUCTAndAverageRewards(numTrials, trajectories[i], planningDepths[j]);
				writeDataPointToFile(trajectories[i], planningDepths[j], result);
				avgCumulativeRewards.add(result);
			}
		}
		
		System.out.println(avgCumulativeRewards);
	}
	
	private static void writeDataPointToFile(int trajectory, int planningDepth, double avgCumulReward) {
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("src/final_project/rock_results.txt", true)))) {
			out.println("Traj: " + trajectory + ", " + "PlanDepth: " + planningDepth + ", " + " Rew: " + avgCumulReward);  
		}
		catch (IOException e) {
		    //exception handling left as an exercise for the reader
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DPrint.toggleUniversal(false);
		runUCTExperiments();
	}
	
}
