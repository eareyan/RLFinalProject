package finalProject;

import java.util.List;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.MutableObjectInstance;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.MutableState;
import burlap.oomdp.core.states.State;

public class RockSampleObservations {

	public static String OBSGOOD = "observationOfGood";
	public static String OBSBAD = "observationOfBad";
	public static String OBSNOTHING = "observationNothing";
	
	private static ObjectInstance convertRockToWithoutGoodness(Domain d, ObjectInstance rock) {
		int rockX = rock.getIntValForAttribute(RockSampleDG.XATT);
		int rockY = rock.getIntValForAttribute(RockSampleDG.YATT);

		ObjectInstance toReturn = new MutableObjectInstance(d.getObjectClass(RockSampleDG.ROCKCLASSOBSERVABLE),  rock.getName() + "(Observable)");
		toReturn.setValue(RockSampleDG.XATT, rockX);
		toReturn.setValue(RockSampleDG.YATT, rockY);
		
		return toReturn;
	}
	
	private static State stripStateOfRockGoodnessObservations(Domain d, State s) {
		State toReturn = s.copy();
		List<ObjectInstance> rocks = s.getObjectsOfClass(RockSampleDG.ROCKCLASS);
		for (ObjectInstance rock : rocks) {
			toReturn.removeObject(rock);
			toReturn.addObject(convertRockToWithoutGoodness(d, rock));
		}
		
		return toReturn;
		
	}
	

	public static State observationGood(Domain rsD, int rockNumber, State s){
		State observeGood = stripStateOfRockGoodnessObservations(rsD, s);
		ObjectInstance obsObject = new MutableObjectInstance(rsD.getObjectClass(RockSampleDG.OBSERVATIONCLASS), RockSampleDG.OBSERVATIONCLASS);
		obsObject.setValue(RockSampleDG.OBSATT, OBSGOOD);
		obsObject.setValue(RockSampleDG.ROCKNUMBEROBSATT, rockNumber);
		observeGood.addObject(obsObject);
		return observeGood;
	}

	public static State observationBad(Domain rsD, int rockNumber, State s){
		State observeBad = stripStateOfRockGoodnessObservations(rsD, s);
		ObjectInstance obsObject = new MutableObjectInstance(rsD.getObjectClass(RockSampleDG.OBSERVATIONCLASS), RockSampleDG.OBSERVATIONCLASS);
		obsObject.setValue(RockSampleDG.OBSATT, OBSBAD);
		obsObject.setValue(RockSampleDG.ROCKNUMBEROBSATT, rockNumber);
		observeBad.addObject(obsObject);
		return observeBad;
	}
	
	public static State observationNothing(Domain rsD, State s){
		State nothing = stripStateOfRockGoodnessObservations(rsD, s);
		ObjectInstance obsObject = new MutableObjectInstance(rsD.getObjectClass(RockSampleDG.OBSERVATIONCLASS), RockSampleDG.OBSERVATIONCLASS);
		obsObject.setValue(RockSampleDG.OBSATT, OBSNOTHING);
		nothing.addObject(obsObject);
		return nothing;
	}
}
