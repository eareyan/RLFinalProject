package finalProject.Actions;

import java.util.List;

import finalProject.RockSampleDG;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;

public class EastAction extends MovementAction {
	public EastAction(Domain d, int width, int height) {
		super("east", d, width, height);
	}

	@Override
	protected State performActionHelper(State s, GroundedAction groundedAction) {
		ObjectInstance agent = s.getObjectsOfClass(RockSampleDG.AGENTCLASS).get(0);
		int agentx = agent.getIntValForAttribute(RockSampleDG.XATT);
		if (agentx < width) { // Note that can go over width to terminal state.
			agent.setValue(RockSampleDG.XATT, agentx+1);
		}
		
		return s;
	}

	@Override
	public String toString() {
		return "east";
	}
}
