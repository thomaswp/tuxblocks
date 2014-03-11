package tuxkids.tuxblocks.core.student;

import static tuxkids.tuxblocks.core.student.ActionType.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BasicStudentModel implements StudentModel, Serializable {

	private Map<ActionType, KnowledgeComponent> knowledgeBits = new HashMap<ActionType, KnowledgeComponent>();

	public BasicStudentModel() {
		initializeKnowledgeComponents();
	}

	private void initializeKnowledgeComponents() {
		knowledgeBits.put(ADD_INTEGERS, new KnowledgeComponent(
				"Add Integers < 100", L0_HIGH, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
		knowledgeBits.put(SUBTRACT_INTEGERS, new KnowledgeComponent(
				"Subtract Integers < 100", L0_HIGH, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
		knowledgeBits.put(MULTIPLY_INTEGERS_LOW, new KnowledgeComponent(
				"Multiply Integers 1-4", L0_HIGH, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
		knowledgeBits.put(MULTIPLY_INTEGERS_MED, new KnowledgeComponent(
				"Multiply Integers 4-9", L0_HIGH, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
		knowledgeBits.put(MULTIPLY_INTEGERS_HIGH, new KnowledgeComponent(
				"Multiply Integers 10+", L0_MED, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
		knowledgeBits.put(DIVIDE_INTEGERS_LOW, new KnowledgeComponent(
				"Divide Integers 1-4", L0_HIGH, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
		knowledgeBits.put(DIVIDE_INTEGERS_MED, new KnowledgeComponent(
				"Divide Integers 4-9", L0_HIGH, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
		knowledgeBits.put(DIVIDE_INTEGERS_HIGH, new KnowledgeComponent(
				"Divide Integers 10+", L0_MED, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));

		knowledgeBits.put(ADD_UNKNOWNS, new KnowledgeComponent("Add Unknowns",
				L0_MED, BASE_SLIP, BASE_GUESS, TRANSITION_MED));
		knowledgeBits.put(SUBTRACT_UNKNOWNS, new KnowledgeComponent(
				"Subtract Unknowns", L0_MED, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));

		knowledgeBits.put(ADD_EQUATION_SIDES, new KnowledgeComponent(
				"Add Sides of Equations", L0_MED, BASE_SLIP, BASE_GUESS,
				TRANSITION_LOW));
		knowledgeBits.put(SUBTRACT_EQUATION_SIDES, new KnowledgeComponent(
				"Subtract Sides of Equations", L0_MED, BASE_SLIP, BASE_GUESS,
				TRANSITION_LOW));

		knowledgeBits.put(MULTIPLY_SINGLE_SIDE, new KnowledgeComponent(
				"Multiply Sides of Equations 1 term", L0_MED, BASE_SLIP,
				BASE_GUESS, TRANSITION_LOW));
		knowledgeBits.put(MULTIPLY_MULTIPLE_SIDES, new KnowledgeComponent(
				"Multiply Sides of Equations 2+ terms", L0_LOW, BASE_SLIP,
				BASE_GUESS, TRANSITION_LOW));
		knowledgeBits.put(DIVIDE_SINGLE_SIDE, new KnowledgeComponent(
				"Divide Sides of Equations 1 term", L0_MED, BASE_SLIP,
				BASE_GUESS, TRANSITION_LOW));
		knowledgeBits.put(DIVIDE_MULTIPLE_SIDES, new KnowledgeComponent(
				"Divide Sides of Equations 2+ terms", L0_LOW, BASE_SLIP,
				BASE_GUESS, TRANSITION_LOW));

		knowledgeBits.put(DISTRIBUTION, new KnowledgeComponent("Distribution",
				L0_LOW, BASE_SLIP, BASE_GUESS, TRANSITION_MED));
		knowledgeBits.put(COMBINATION, new KnowledgeComponent("Combination",
				L0_LOW, BASE_SLIP, BASE_GUESS, TRANSITION_LOW));

		knowledgeBits.put(BUILDING_SYMBOLIC_EQUATIONS, new KnowledgeComponent(
				"Building Symbolic Equations", L0_MED, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
		knowledgeBits.put(BUILDING_WRITTEN_EQUATIONS, new KnowledgeComponent(
				"Building Written Equations", L0_MED, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
	}

	@Override
	public KnowledgeComponent getKnowledgeComponentForAction(StudentAction a) {
		return knowledgeBits.get(a.type);
	}

}
