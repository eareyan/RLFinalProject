package finalProject.Domain;

import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.environment.EnvironmentOutcome;
import burlap.oomdp.singleagent.pomdp.PODomain;
import burlap.oomdp.singleagent.pomdp.SimulatedPOEnvironment;

public class SimulatedBeliefPOEnvironment extends SimulatedPOEnvironment {

	public SimulatedBeliefPOEnvironment(PODomain domain, RewardFunction rf,
			TerminalFunction tf, State initialHiddenState) {
		super(domain, rf, tf, initialHiddenState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public EnvironmentOutcome executeAction(GroundedAction ga) {

		GroundedAction simGA = (GroundedAction)ga.copy();
		simGA.action = this.domain.getAction(ga.actionName());
		if(simGA.action == null){
			throw new RuntimeException("Cannot execute action " + ga.toString() + " in this SimulatedEnvironment because the action is to known in this Environment's domain");
		}
		State nextState = this.curState;
		State nextObservation = this.curObservation;
		if(this.allowActionFromTerminalStates || !this.isInTerminalState()) {
			nextState = simGA.executeIn(this.curState);
			this.lastReward = this.rf.reward(this.curState, simGA, nextState);
			nextObservation = ((PODomain)domain).getObservationFunction().sampleObservation(nextState, simGA);
		}
		else{
			this.lastReward = 0.;
		}

		EnvironmentOutcome eo = new EnvironmentOutcome(this.curObservation.copy(), simGA, nextObservation.copy(), this.lastReward, this.tf.isTerminal(nextState));

		this.curState = nextState;
		this.curObservation = nextObservation;

		return eo;

	}
	
}
