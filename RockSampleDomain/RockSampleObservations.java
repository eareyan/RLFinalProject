package finalProject;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.MutableObjectInstance;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.MutableState;
import burlap.oomdp.core.states.State;

public class RockSampleObservations {

	public static String OBSGOOD = "observationOfGood";
	public static String OBSBAD = "observationOfBad";
	public static String OBSNOTHING = "observationNothing";

	public static State observationGood(Domain rsD, int rockNumber){
		State observeGood = new MutableState();
		ObjectInstance obsObject = new MutableObjectInstance(rsD.getObjectClass(RockSampleDG.OBSERVATIONCLASS), RockSampleDG.OBSERVATIONCLASS);
		obsObject.setValue(RockSampleDG.OBSATT, OBSGOOD);
		obsObject.setValue(RockSampleDG.ROCKNUMBEROBSATT, rockNumber);
		observeGood.addObject(obsObject);
		return observeGood;
	}

	public static State observationBad(Domain rsD, int rockNumber){
		State observeBad = new MutableState();
		ObjectInstance obsObject = new MutableObjectInstance(rsD.getObjectClass(RockSampleDG.OBSERVATIONCLASS), RockSampleDG.OBSERVATIONCLASS);
		obsObject.setValue(RockSampleDG.OBSATT, OBSBAD);
		obsObject.setValue(RockSampleDG.ROCKNUMBEROBSATT, rockNumber);
		observeBad.addObject(obsObject);
		return observeBad;
	}
	
	public static State observationNothing(Domain rsD){
		State nothing = new MutableState();
		ObjectInstance obsObject = new MutableObjectInstance(rsD.getObjectClass(RockSampleDG.OBSERVATIONCLASS), RockSampleDG.OBSERVATIONCLASS);
		obsObject.setValue(RockSampleDG.OBSATT, OBSNOTHING);
		nothing.addObject(obsObject);
		return nothing;
	}
}
