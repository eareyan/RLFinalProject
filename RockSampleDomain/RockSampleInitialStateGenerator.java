package finalProject;

import java.util.Random;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.MutableObjectInstance;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.MutableState;
import burlap.oomdp.core.states.OOMDPState;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.pomdp.PODomain;
import burlap.oomdp.singleagent.pomdp.beliefstate.tabular.TabularBeliefState;

public class RockSampleInitialStateGenerator {

	public static State getInitialState(Domain d, int width, int height, int numRocks) {
		Random rand = new Random(1);

		// Randomize agent initial start.
		int randomAgentX = rand.nextInt(width);
		int randomAgentY = rand.nextInt(height);
		State toReturn = new MutableState();
		ObjectInstance agent = new MutableObjectInstance(d.getObjectClass(RockSampleDG.AGENTCLASS), "dahAgent");
		agent.setValue(RockSampleDG.XATT, randomAgentX);
		agent.setValue(RockSampleDG.YATT, randomAgentY);
		toReturn.addObject(agent);

		// Randomize initial rocks.
		for (int i = 0; i < numRocks; i++) {
			ObjectInstance rock = new MutableObjectInstance(d.getObjectClass(RockSampleDG.ROCKCLASS), "rock" + i);
			int randomRockX = rand.nextInt(width);
			int randomRockY = rand.nextInt(height);
			rock.setValue(RockSampleDG.XATT, randomRockX);
			rock.setValue(RockSampleDG.YATT, randomRockY);
			rock.setValue(RockSampleDG.GOODNESSATT, 1); // ASSUMING ALL ROCKS START GOOD RIGHT NOW
			toReturn.addObject(rock);
		}

		return toReturn;
	}
	
	public static TabularBeliefState getInitialBeliefState(PODomain domain){
		TabularBeliefState bs = new TabularBeliefState(domain, domain.getStateEnumerator());
		bs.initializeBeliefsUniformly();
		return bs;
	}

}
