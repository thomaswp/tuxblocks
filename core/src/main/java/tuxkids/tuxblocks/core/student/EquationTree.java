package tuxkids.tuxblocks.core.student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.EquationGenerator.EGenerator;


public class EquationTree {
	
	
	//generated at runtime
	private List<EquationTreeNode> equationNodes = new ArrayList<EquationTreeNode>();
	
	public EquationTreeNode node(int index) {
		return equationNodes.get(index);
	}

	public int size() {
		return equationNodes.size();
	}

	public boolean unlocked(int i) {
		return node(i).isUnlocked();
	}

	public List<Float> confidences() {
		List<Float> retVal = new ArrayList<Float>();
		for(EquationTreeNode node:equationNodes) {
			retVal.add(node.confidence());
		}
		return retVal;
	}

	public Equation equation(int i) {
		return node(i).equation();
	}

	public EquationTreeNode randomUnlockedNode(Random rand) {
		EquationTreeNode toReturn = node(rand.nextInt(size()));
		while (!toReturn.isUnlocked()) {
			toReturn = node(rand.nextInt(size()));
		}
		return toReturn;
	}
	
	
}


class EquationTreeNode {
	private EGenerator generator;
	
	private Map<Criteria, EquationTreeNode> ancestors = new HashMap<Criteria, EquationTreeNode>();
	
	private boolean unlocked;
	private float confidence;

	public boolean isUnlocked() {
		return unlocked;
	}

	public Equation equation() {
		return generator.generate();
	}

	public float confidence() {
		return confidence;
	}
	
	
}


class Criteria {
	//TODO: this class will represent what knowledge components need to advance to
	//what levels to progress.
	
	//or something like that.  Perhaps be an interface with a single method.
}