package finalProject;

import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;

public class RockSampleTF implements TerminalFunction {
	private int width;

	public RockSampleTF(int width) {
		this.width = width;
	}

	@Override
	public boolean isTerminal(State s) {
		ObjectInstance agent = s.getObjectsOfClass(RockSampleDG.AGENTCLASS).get(0);
		int agentx = agent.getIntValForAttribute(RockSampleDG.XATT);
		return agentx == width;
	}

}
