package tuxkids.tuxblocks.core.student;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BasicStudentModel implements StudentModel, Serializable {

	//this will likely be a Map or something
	private List<KnowledgeComponent> knowledgeBits = new ArrayList<KnowledgeComponent>();

	public BasicStudentModel() {
		initializeKnowledgeComponents();
	}

	private void initializeKnowledgeComponents() {
		knowledgeBits.add(new KnowledgeComponent("Add Integers < 100", L0_HIGH,
				BASE_SLIP, BASE_GUESS, TRANSITION_MED));
		knowledgeBits.add(new KnowledgeComponent("Subtract Integers < 100",
				L0_HIGH, BASE_SLIP, BASE_GUESS, TRANSITION_MED));
		knowledgeBits.add(new KnowledgeComponent("Multiply Integers 1-4",
				L0_HIGH, BASE_SLIP, BASE_GUESS, TRANSITION_MED));
		knowledgeBits.add(new KnowledgeComponent("Multiply Integers 4-9",
				L0_HIGH, BASE_SLIP, BASE_GUESS, TRANSITION_MED));
		knowledgeBits.add(new KnowledgeComponent("Multiply Integers 10+",
				L0_MED, BASE_SLIP, BASE_GUESS, TRANSITION_MED));
		knowledgeBits.add(new KnowledgeComponent("Divide Integers 1-4",
				L0_HIGH, BASE_SLIP, BASE_GUESS, TRANSITION_MED));
		knowledgeBits.add(new KnowledgeComponent("Divide Integers 4-9",
				L0_HIGH, BASE_SLIP, BASE_GUESS, TRANSITION_MED));
		knowledgeBits.add(new KnowledgeComponent("Divide Integers 10+", L0_MED,
				BASE_SLIP, BASE_GUESS, TRANSITION_MED));

		knowledgeBits.add(new KnowledgeComponent("Add Unknowns", L0_MED,
				BASE_SLIP, BASE_GUESS, TRANSITION_MED));
		knowledgeBits.add(new KnowledgeComponent("Subtract Unknowns",
				L0_MED, BASE_SLIP, BASE_GUESS, TRANSITION_MED));
		
		knowledgeBits.add(new KnowledgeComponent("Add Sides of Equations",
				L0_MED,	BASE_SLIP, BASE_GUESS, TRANSITION_LOW));
		knowledgeBits.add(new KnowledgeComponent("Subtract Sides of Equations",
				L0_MED, BASE_SLIP, BASE_GUESS, TRANSITION_LOW));
		
		knowledgeBits.add(new KnowledgeComponent("Multiply Sides of Equations 1 term",
				L0_MED,	BASE_SLIP, BASE_GUESS, TRANSITION_LOW));
		knowledgeBits.add(new KnowledgeComponent("Multiply Sides of Equations 2+ terms",
				L0_LOW, BASE_SLIP, BASE_GUESS, TRANSITION_LOW));
		knowledgeBits.add(new KnowledgeComponent("Divide Sides of Equations 1 term",
				L0_MED,	BASE_SLIP, BASE_GUESS, TRANSITION_LOW));
		knowledgeBits.add(new KnowledgeComponent("Divide Sides of Equations 2+ terms",
				L0_LOW, BASE_SLIP, BASE_GUESS, TRANSITION_LOW));
		
		knowledgeBits.add(new KnowledgeComponent("Distribution",
				L0_LOW, BASE_SLIP, BASE_GUESS, TRANSITION_MED));
		knowledgeBits.add(new KnowledgeComponent("Combination",
				L0_LOW, BASE_SLIP, BASE_GUESS, TRANSITION_LOW));
		
		knowledgeBits.add(new KnowledgeComponent("Building Symbolic Equations",
				L0_MED, BASE_SLIP, BASE_GUESS, TRANSITION_MED));
		knowledgeBits.add(new KnowledgeComponent("Building Written Equations",
				L0_MED, BASE_SLIP, BASE_GUESS, TRANSITION_MED));
	}

}
