package finalProject;

import java.util.List;

import finalProject.Actions.EastAction;
import finalProject.Actions.NorthAction;
import finalProject.Actions.SampleAction;
import finalProject.Actions.SouthAction;
import finalProject.Actions.WestAction;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.SADomain;
import burlap.oomdp.stochasticgames.agentactions.SGAgentAction;


public class RockSampleDG implements DomainGenerator {
	
	public static String AGENTCLASS = "agent";
	public static String ROCKCLASS = "rock";
	public static String GOODNESSATT = "goodnessAtt";
	public static String XATT = "xATT";
	public static String YATT = "YATT";
	
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
		Domain domain = new SADomain();
		

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
		
		//--------ADD ACTIONS--------- 
		domain.addAction(new EastAction(domain, width, height));
		domain.addAction(new NorthAction(domain, width, height));
		domain.addAction(new WestAction(domain, width, height));
		domain.addAction(new SouthAction(domain, width, height));
		domain.addAction(new SampleAction(domain));
		
		return domain;
	}
	
	
	

}
