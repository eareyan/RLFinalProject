package finalProject.Domain;

import java.util.ArrayList;
import java.util.List;

import finalProject.Domain.Actions.CheckAction;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.oomdp.auxiliary.common.NullTermination;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.common.UniformCostRF;
import burlap.oomdp.singleagent.pomdp.ObservationFunction;
import burlap.oomdp.singleagent.pomdp.PODomain;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;

public class RockSampleObservationFunction extends ObservationFunction {

	private int numRocks;
	private State initialState;
	private List<State> allStates;

	public RockSampleObservationFunction(PODomain domain, int width, int height, int numRocks) {
		super(domain);
		this.numRocks = numRocks;
		this.initialState = RockSampleInitialStateGenerator.getInitialState(domain, width, height, numRocks);

		ValueIteration vi = new ValueIteration(domain, new UniformCostRF(), new NullTermination(), 1.0, new SimpleHashableStateFactory(), .01, 1);
		vi.planFromState(initialState);
		this.allStates = vi.getAllStates();
	}

	@Override
	public boolean canEnumerateObservations() {
		return true;
	}

	@Override
	public List<State> getAllPossibleObservations() {
		List<State> toReturn = new ArrayList<State>(); //TODO RETURNING JUNK BECAUSE canEnumerateObservations is false
		for (State s : allStates) {
			for (int i = 0; i < this.numRocks; i++) {
				toReturn.add(RockSampleObservations.observationGood(domain, i, s));
				toReturn.add(RockSampleObservations.observationBad(domain, i, s));
			}
			toReturn.add(RockSampleObservations.observationNothing(domain, s));
		}
		return toReturn;
	}

	//	private boolean agentAtSomeLocationInStates(State s1, State s2) {
	//		ObjectInstance agent1 = s1.getObjectsOfClass(RockSampleDG.AGENTCLASS).get(0);
	//		int agent1X = agent1.getIntValForAttribute(RockSampleDG.XATT);
	//		int agent1Y = agent1.getIntValForAttribute(RockSampleDG.YATT);
	//		
	//		
	//		ObjectInstance agent2 = s2.getObjectsOfClass(RockSampleDG.AGENTCLASS).get(0);
	//		int agent2X = agent2.getIntValForAttribute(RockSampleDG.XATT);
	//		int agent2Y = agent2.getIntValForAttribute(RockSampleDG.YATT);
	//		
	//		return (agent1X == agent2X && agent1Y == agent2Y);
	//	}

	@Override
	public double getObservationProbability(State observation, State state,
			GroundedAction action) {
		// If a non-check action was taken, observe normal state.
		if (!(action.action instanceof CheckAction)) {
			return 1.0; // 50/50 because not
		}
		// If a check action, probability of it being true is inversely dependent on distance to rock.
		else {
			boolean observedGood = observation.getFirstObjectOfClass(RockSampleDG.OBSERVATIONCLASS).
					getStringValForAttribute(RockSampleDG.OBSATT).equals(RockSampleObservations.OBSGOOD);
			int checkedRockIndex = ((CheckAction) action.action).getRockNumber(state, action);
			boolean checkedRockIsGood = state.getObject(RockSampleDG.ROCKCLASS + checkedRockIndex).getBooleanValForAttribute(RockSampleDG.GOODNESSATT);

			double probabilityTrueReading = getProbTrueReading(state, checkedRockIndex);

			// Check action returned a true observation
			if (observedGood == checkedRockIsGood) {
				return probabilityTrueReading;
			}
			//Check action returned a false observation
			else {
				return 1.0 - probabilityTrueReading;
			}
		}
	}

	//If efficiency is 0, 50/50 chance of true reading.
	public static double getProbTrueReading(State s, int rockIndex) {
		double distanceToRock = getRoverDistanceToRockOfNumber(s, rockIndex);
		double efficiencyOfSensor = Math.pow(.5, distanceToRock); //TODO Figure out what the base of the exponential falloff is.
		return efficiencyOfSensor * (1.0-efficiencyOfSensor)*.5; //If efficiency is 0, 50/50 chance of true reading.
	}

	public static double getRoverDistanceToRockOfNumber(State s, int rockIndex) {
		ObjectInstance agent = s.getObjectsOfClass(RockSampleDG.AGENTCLASS).get(0);
		int agentX = agent.getIntValForAttribute(RockSampleDG.XATT);
		int agentY = agent.getIntValForAttribute(RockSampleDG.YATT);

		ObjectInstance rock = s.getObject(RockSampleDG.ROCKCLASS + rockIndex);
		int rockX = rock.getIntValForAttribute(RockSampleDG.XATT);
		int rockY = rock.getIntValForAttribute(RockSampleDG.YATT);

		return Math.sqrt(Math.pow(agentX - rockX, 2) + Math.pow(agentY - rockY, 2));//Return Euc distance
	}


	@Override
	public State sampleObservation(State state, GroundedAction action) {
		// No observation if not a check action.
		if (!(action.action instanceof CheckAction)) {
			return RockSampleObservations.observationNothing(this.domain, state);
		}
		// Took a check action.
		else {
			int checkedRockIndex = ((CheckAction) action.action).getRockNumber(state, action);
			boolean checkedRockIsGood = state.getObject(RockSampleDG.ROCKCLASS + checkedRockIndex).getBooleanValForAttribute(RockSampleDG.GOODNESSATT);
			double probTrueReading = getProbTrueReading(state, checkedRockIndex);

			//True reading.
			if (Math.random() < probTrueReading) { 
				if (checkedRockIsGood) return RockSampleObservations.observationGood(this.domain, checkedRockIndex, state);
				else return RockSampleObservations.observationBad(this.domain, checkedRockIndex, state);
			} 
			//False reading.
			else {
				if (!checkedRockIsGood) return RockSampleObservations.observationGood(this.domain, checkedRockIndex, state);
				else return RockSampleObservations.observationBad(this.domain, checkedRockIndex, state);
			}
		}
	}

	@Override
	public List<ObservationProbability> getObservationProbabilities(
			State state, GroundedAction action) {
		return this.getObservationProbabilitiesByEnumeration(state, action);
	}

}
