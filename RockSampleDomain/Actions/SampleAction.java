package finalProject.Actions;

import java.util.List;

import finalProject.RockSampleDG;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.FullActionModel;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.common.SimpleAction;
import burlap.oomdp.singleagent.common.SimpleAction.SimpleDeterministicAction;

public class SampleAction extends SimpleDeterministicAction implements FullActionModel{

	public SampleAction(Domain d) {
		super("sample", d);
	}

	@Override
	protected State performActionHelper(State s, GroundedAction groundedAction) {
		ObjectInstance agent = s.getObjectsOfClass(RockSampleDG.AGENTCLASS).get(0);
		int agentx = agent.getIntValForAttribute(RockSampleDG.XATT);
		int agenty = agent.getIntValForAttribute(RockSampleDG.YATT);
		
		for (ObjectInstance rock : s.getObjectsOfClass(RockSampleDG.ROCKCLASS)) {
			int rockx = rock.getIntValForAttribute(RockSampleDG.XATT);
			int rocky = rock.getIntValForAttribute(RockSampleDG.YATT);
			
			if (agentx == rockx && agenty == rocky) {
				rock.setValue(RockSampleDG.GOODNESSATT, 0);
			}
		}
		return s;
	}

	@Override
	public List<TransitionProbability> getTransitions(State s,
			GroundedAction groundedAction) {
		return this.deterministicTransition(s, groundedAction);
	}

	@Override
	public String toString() {
		return "sample";
	}
}
