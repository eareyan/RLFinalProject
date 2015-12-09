package finalProject.Domain;

import finalProject.Domain.Actions.EastAction;
import finalProject.Domain.Actions.SampleAction;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;

public class RockSampleRF implements RewardFunction {
	private int width;

	public RockSampleRF(int width) {
		this.width = width;
	}


	@Override
	public double reward(State s, GroundedAction a, State sprime) {

		//Reward for sampling.		
		if (a.action instanceof SampleAction) {
			ObjectInstance agent = s.getObjectsOfClass(RockSampleDG.AGENTCLASS).get(0);
			int agentx = agent.getIntValForAttribute(RockSampleDG.XATT);
			int agenty = agent.getIntValForAttribute(RockSampleDG.YATT);

			for (ObjectInstance rock : s.getObjectsOfClass(RockSampleDG.ROCKCLASS)) {
				int rockx = rock.getIntValForAttribute(RockSampleDG.XATT);
				int rocky = rock.getIntValForAttribute(RockSampleDG.YATT);

				if (agentx == rockx && agenty == rocky) {
					if (rock.getNumericValForAttribute(RockSampleDG.GOODNESSATT) == 1) {
						return 10;
					} else {
						return -10;
					}
				}
			}
		}

		//Reward for terminal state off right edge of map.
		if (a.action instanceof EastAction) {
			ObjectInstance agent = sprime.getObjectsOfClass(RockSampleDG.AGENTCLASS).get(0);
			int agentx = agent.getIntValForAttribute(RockSampleDG.XATT);
			if (agentx == width) {
				return 10;
			}

		}

		return 0;
	}

}
