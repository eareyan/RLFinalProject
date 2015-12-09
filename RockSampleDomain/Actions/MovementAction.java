package finalProject.Domain.Actions;

import java.util.List;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.FullActionModel;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.common.SimpleAction.SimpleDeterministicAction;

public abstract class MovementAction extends SimpleDeterministicAction implements FullActionModel {
	protected int width;
	protected int height;
	
	public MovementAction(String actionName, Domain d, int width, int height) {
		super(actionName, d);
		this.width = width;
		this.height = height;
	}

	@Override
	public List<TransitionProbability> getTransitions(State s,
			GroundedAction groundedAction) {
		return this.deterministicTransition(s, groundedAction);
	}

}
