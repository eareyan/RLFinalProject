package finalProject.Domain;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.pomdp.BeliefPolicyAgent;
import burlap.behavior.singleagent.pomdp.wrappedmdpalgs.BeliefSparseSampling;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.pomdp.PODomain;
import burlap.oomdp.singleagent.pomdp.SimulatedPOEnvironment;
import burlap.oomdp.singleagent.pomdp.beliefstate.BeliefState;
import burlap.oomdp.singleagent.pomdp.beliefstate.tabular.HashableTabularBeliefStateFactory;

public class RockSamplePartiallyObservableTest {

	public static void main(String[] args) {
		int width = 7;
		int height = 8;
		int numRocks = 2;
		RockSampleDG dg = new RockSampleDG(width, height, numRocks);

		PODomain domain = (PODomain)dg.generateDomain();
		RewardFunction rf = new RockSampleRF(width);
		TerminalFunction tf = new RockSampleTF(width);
		State initialState = RockSampleInitialStateGenerator.getInitialState(domain, width, height, numRocks);
		domain.getStateEnumerator().findReachableStatesAndEnumerate(initialState, tf);
		BeliefState initialBelief = RockSampleInitialStateGenerator.getInitialBeliefState(domain, initialState);

		BeliefSparseSampling bss = new BeliefSparseSampling(domain, rf, 0.8, new HashableTabularBeliefStateFactory(), 2, 2);
		Policy p = new GreedyQPolicy(bss);

		SimulatedPOEnvironment env = new SimulatedPOEnvironment(domain, rf, tf);
		env.setCurStateTo(initialState);

		BeliefPolicyAgent agent = new BeliefPolicyAgent(domain, env, p);
		agent.setBeliefState(initialBelief);

		agent.setEnvironment(env);

		EpisodeAnalysis ea = agent.actUntilTerminalOrMaxSteps(1);

		for(int i = 0; i < ea.numTimeSteps()-1; i++){
			System.out.println(ea.getAction(i) + " " + ea.getReward(i+1));
		}
	}
}
