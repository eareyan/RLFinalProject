package finalProject.Domain;

import java.util.ArrayList;
import java.util.List;

import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.MutableState;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.pomdp.beliefstate.BeliefState;

public class RockSampleTF implements TerminalFunction {
	private int width;

	public RockSampleTF(int width) {
		this.width = width;
	}

	@Override
	public boolean isTerminal(State s) {

		//		System.out.println("CALLING IS TERMINAL");
		//		System.out.println(s.getCompleteStateDescriptionWithUnsetAttributesAsNull());
		List<ObjectInstance> agentList = new ArrayList<ObjectInstance>();

		//		System.out.println(s.getClass().equals(MutableState.class));
		if (s.getClass().equals(MutableState.class)) {
			ObjectInstance agent = s.getObjectsOfClass(RockSampleDG.AGENTCLASS).get(0);
			int agentx = agent.getIntValForAttribute(RockSampleDG.XATT);
//			System.out.println(agentx == width);
			return agentx == width;
		}
		else {
			State sampledState = ((BeliefState)s).sampleStateFromBelief();
			ObjectInstance agent = sampledState.getObjectsOfClass(RockSampleDG.AGENTCLASS).get(0);
			int agentx = agent.getIntValForAttribute(RockSampleDG.XATT);
//			System.out.println(agentx == width);
			return agentx == width;
		}
	}
}
