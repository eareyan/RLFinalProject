package finalProject.Actions;

import java.util.List;

import finalProject.RockSampleDG;
import burlap.oomdp.core.AbstractObjectParameterizedGroundedAction;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.FullActionModel;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.ObjectParameterizedAction;

public class CheckAction extends ObjectParameterizedAction implements FullActionModel {


	public CheckAction(Domain domain) {
		super("check", domain, new String[]{RockSampleDG.ROCKCLASS});
	}

	@Override
	public boolean parametersAreObjectIdentifierIndependent() {
		return false;//TODO NOT SURE THIS SHOULD BE FALSE
	}
	
	public int getRockNumber(State s, GroundedAction groundedAction) {
		String [] params = ((AbstractObjectParameterizedGroundedAction)groundedAction).getObjectParameters();

		ObjectInstance src = s.getObject(params[0]);
		
		String rockIndexString = src.getName().split(RockSampleDG.ROCKCLASS)[1];
//		System.out.println("CheckAction: rockIndexString: " + rockIndexString);
		int rockIndex = Integer.parseInt(rockIndexString);
		
		return rockIndex;
		
	}

	@Override
	public boolean applicableInState(State s, GroundedAction groundedAction) {
		return true;
	}

	@Override
	public boolean isPrimitive() {
		return true;
	}

	@Override
	protected State performActionHelper(State s, GroundedAction groundedAction) {
		return s;
	}

	@Override
	public String toString() {
		return "check";
	}

	@Override
	public List<TransitionProbability> getTransitions(State s,
			GroundedAction groundedAction) {
		return this.deterministicTransition(s, groundedAction);
	}
	

}
