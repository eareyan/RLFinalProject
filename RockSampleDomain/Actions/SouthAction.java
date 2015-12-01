package finalProject.Actions;

import finalProject.RockSampleDG;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;

public class SouthAction extends MovementAction {
	public SouthAction(Domain d, int width, int height) {
		super("south", d, width, height);
	}

	@Override
	protected State performActionHelper(State s, GroundedAction groundedAction) {
		ObjectInstance agent = s.getObjectsOfClass(RockSampleDG.AGENTCLASS).get(0);
		int agenty = agent.getIntValForAttribute(RockSampleDG.YATT);
		if (agenty > 1) {
			agent.setValue(RockSampleDG.YATT, agenty-1);
		}
		
		return s;
	}
	
	@Override
	public String toString() {
		return "south";
	}

}
