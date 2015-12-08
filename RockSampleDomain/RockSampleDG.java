package finalProject;

import java.util.List;

import finalProject.Actions.CheckAction;
import finalProject.Actions.EastAction;
import finalProject.Actions.NorthAction;
import finalProject.Actions.SampleAction;
import finalProject.Actions.SouthAction;
import finalProject.Actions.WestAction;
import burlap.behavior.singleagent.auxiliary.StateEnumerator;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Attribute.AttributeType;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.SADomain;
import burlap.oomdp.singleagent.pomdp.PODomain;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import burlap.oomdp.stochasticgames.agentactions.SGAgentAction;


public class RockSampleDG implements DomainGenerator {
	
	public static String AGENTCLASS = "agent";
	public static String ROCKCLASS = "rock";
	public static String ROCKCLASSOBSERVABLE = "rockObservable";
	public static String OBSERVATIONCLASS = "observation class";

	public static String GOODNESSATT = "goodnessAtt";
	public static String XATT = "xATT";
	public static String YATT = "YATT";
	public static String OBSATT = "observationAttribute";
	public static String ROCKNUMBEROBSATT = "indexOfObservedRockAttribute";

	
	private int width;
	private int height;
	private int numRocks;
	
	public RockSampleDG(int width, int height, int numRocks) {
		this.width = width;
		this.height = height;
		this.numRocks = numRocks;
	}

	@Override
	public Domain generateDomain() {
		Domain domain = new PODomain();
		

		//----------ADD ATTRIBUTES------- 
		//x Position Attribute
		Attribute xAtt = new Attribute(domain, XATT, Attribute.AttributeType.DISC);
		xAtt.setDiscValuesForRange(0, this.width-1, 1); //-1 due to inclusivity vs exclusivity
		domain.addAttribute(xAtt);
		
		//y Position Attribute
		Attribute yAtt = new Attribute(domain, YATT, Attribute.AttributeType.DISC);
		yAtt.setDiscValuesForRange(0, this.width-1, 1); //-1 due to inclusivity vs exclusivity
		domain.addAttribute(yAtt);
		
		
		//Goodness (of rocks)
		Attribute goodAtt = new Attribute(domain, GOODNESSATT, Attribute.AttributeType.DISC);
		goodAtt.setDiscValuesForRange(0,1, 1); 	
		domain.addAttribute(goodAtt);
		
		//Observation attributes
		Attribute obAtt = new Attribute(domain, OBSATT, AttributeType.DISC);
		obAtt.setDiscValues(new String[]{RockSampleObservations.OBSBAD, RockSampleObservations.OBSGOOD, RockSampleObservations.OBSNOTHING});
		domain.addAttribute(obAtt);
		
		Attribute rockNumberAtt = new Attribute(domain, ROCKNUMBEROBSATT, AttributeType.DISC);
		goodAtt.setDiscValuesForRange(0, numRocks, 1); 	
		domain.addAttribute(rockNumberAtt);
		
		//----------ADD OBJECT CLASSES------- 
		//Add rover object class.
		ObjectClass agentClass = new ObjectClass(domain, AGENTCLASS);
		agentClass.addAttribute(xAtt);
		agentClass.addAttribute(yAtt);
		domain.addObjectClass(agentClass);
		
		//Add rock object class.
		ObjectClass rockClass = new ObjectClass(domain, ROCKCLASS);
		rockClass.addAttribute(xAtt);
		rockClass.addAttribute(yAtt);
		rockClass.addAttribute(goodAtt);
		domain.addObjectClass(rockClass);
		
		//Add observable rock object class.
		ObjectClass rockClassObs = new ObjectClass(domain, ROCKCLASSOBSERVABLE);
		rockClassObs.addAttribute(xAtt);
		rockClassObs.addAttribute(yAtt);
		domain.addObjectClass(rockClassObs);
		
		//Add observation class.
		ObjectClass obsClass = new ObjectClass(domain, OBSERVATIONCLASS);
		obsClass.addAttribute(obAtt); // Type of observation
		obsClass.addAttribute(rockNumberAtt); // Index of rock's goodness observed
		domain.addObjectClass(obsClass);
		
		//--------ADD ACTIONS--------- 
		domain.addAction(new EastAction(domain, width, height));
		domain.addAction(new NorthAction(domain, width, height));
		domain.addAction(new WestAction(domain, width, height));
		domain.addAction(new SouthAction(domain, width, height));
		domain.addAction(new SampleAction(domain));
		domain.addAction(new CheckAction(domain));
		
		//--------STATE ENUMERATOR--------- 
		StateEnumerator senum = new StateEnumerator(domain, new SimpleHashableStateFactory());
		
		((PODomain) domain).setStateEnumerator(senum);

		//--------OBSERVATION FUNCTION--------- 
		new RockSampleObservationFunction((PODomain) domain, this.numRocks);
		
		return domain;
	}
	
	
	

}
