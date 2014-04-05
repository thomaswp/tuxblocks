package tuxkids.tuxblocks.core.student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.EquationGenerator.EGenerator;


public class EquationTree {

	private List<EquationTreeNode> equationNodes = new ArrayList<EquationTreeNode>();
	private EquationTreeNode root;

	public EquationTree() {
		root = new EquationTreeNode(null) {
			@Override
			public Equation equation() {
				throw new RuntimeException("Root node is just a place holder.  It should never actually be called");
			}
		};

	}

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

	public EquationTreeNode root() {
		return root;
	}
	
	

	public EquationTreeNode addInitialNode(EGenerator generator) {
		EquationTreeNode newNode = addNode(root, generator, new BlankCriteria());
		
		newNode.unlocked = true;
		
		return newNode;
	}

	public EquationTreeNode addNode(EquationTreeNode parent, EGenerator generator, Criteria c) {
		EquationTreeNode newNode = new EquationTreeNode(generator);
		
		newNode.preRequisites.put(c, parent);
		
		return newNode;
	}

	public static class EquationTreeNode {
		private EGenerator generator;

		private Map<Criteria, EquationTreeNode> preRequisites = new HashMap<Criteria, EquationTreeNode>();

		private boolean unlocked;
		private float confidence;
		
		private EquationTreeNode(EGenerator generator) {
			this.generator = generator;
			this.unlocked = false;
			this.confidence = 0;
		}
		

		public boolean isUnlocked() {
			return unlocked;
		}

		public Equation equation() {
			return generator.generate();
		}

		public float confidence() {
			return confidence;
		}

		private void setConfidence(float newConfidence) {
			this.confidence = newConfidence;
		}


	}
}

class BlankCriteria implements Criteria {
	@Override
	public boolean hasBeenSatisfied() {
		return true;
	}
}

interface Criteria {

	//TODO: this class will represent what knowledge components need to advance to
	//what levels to progress.
	boolean hasBeenSatisfied();
	//or something like that.  Perhaps be an interface with a single method.
}