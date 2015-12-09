package finalProject.Domain.Actions;

import finalProject.Domain.RockSampleDG;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;

public class WestAction extends MovementAction {
	public WestAction(Domain d, int width, int height) {
		super("west", d, width, height);
	}

	@Override
	protected State performActionHelper(State s, GroundedAction groundedAction) {
		ObjectInstance agent = s.getObjectsOfClass(RockSampleDG.AGENTCLASS).get(0);
		int agentx = agent.getIntValForAttribute(RockSampleDG.XATT);
		if (agentx > 0) {
			agent.setValue(RockSampleDG.XATT, agentx-1);
		}
		
		return s;
	}

	@Override
	public String toString() {
		return "west";
	}
}
