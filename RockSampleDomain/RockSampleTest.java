package finalProject;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;

public class RockSampleTest {
	
	public static void main (String[] args) {
		int width = 7;
		int height = 8;
		int numRocks = 1;
		RockSampleDG dg = new RockSampleDG(width, height, numRocks);
		Domain d = dg.generateDomain();
		RewardFunction rf = new RockSampleRF(width);
		TerminalFunction tf = new RockSampleTF(width);
		State initialState = RockSampleInitialStateGenerator.getInitialState(d, width, height, numRocks);

		System.out.println(d.getActions());
		
		
		//Run value iteration on rock sample.
		double gamma = .99;
		HashableStateFactory hf = new SimpleHashableStateFactory();
		double maxDelta = .00001;
		int maxIterations = 10000;
		
		ValueIteration vi = new ValueIteration(d, rf, tf, gamma, hf, maxDelta, maxIterations);
		GreedyQPolicy pol = vi.planFromState(initialState);
		System.out.println("Value of intial state is: "+ vi.value(initialState));
		System.out.println(pol.evaluateBehavior(initialState, rf, tf, 100).actionSequence);
	}

}
