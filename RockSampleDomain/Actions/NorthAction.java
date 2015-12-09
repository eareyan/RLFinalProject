package finalProject.Domain.Actions;

import finalProject.Domain.RockSampleDG;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;

public class NorthAction extends MovementAction {
	public NorthAction(Domain d, int width, int height) {
		super("north", d, width, height);
	}

	@Override
	protected State performActionHelper(State s, GroundedAction groundedAction) {
		ObjectInstance agent = s.getObjectsOfClass(RockSampleDG.AGENTCLASS).get(0);
		int agenty = agent.getIntValForAttribute(RockSampleDG.YATT);
		if (agenty < height-1) {
			agent.setValue(RockSampleDG.YATT, agenty+1);
		}
		
		return s;
	}

	@Override
	public String toString() {
		return "north";
	}
}
