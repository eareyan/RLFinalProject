package finalProject.Actions;

import burlap.oomdp.core.Domain;
import burlap.oomdp.singleagent.common.SimpleAction.SimpleDeterministicAction;

public abstract class MovementAction extends SimpleDeterministicAction {
	protected int width;
	protected int height;
	
	public MovementAction(String actionName, Domain d, int width, int height) {
		super(actionName, d);
		this.width = width;
		this.height = height;
	}


}
