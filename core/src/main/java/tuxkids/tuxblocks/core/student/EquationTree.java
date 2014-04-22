package tuxkids.tuxblocks.core.student;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.EquationGenerator.EGenerator;
import tuxkids.tuxblocks.core.tutorial.AbstractStarredTutorial;


public class EquationTree {

	private List<EquationTreeNode> equationNodes = new ArrayList<EquationTreeNode>();
	private EquationTreeNode root;

	public EquationTree() {
		root = new EquationTreeNode(null) {
			@Override
			public Equation generateEquation() {
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
		return node(i).generateEquation();
	}

	public EquationTreeNode randomWeightedUnlockedNode(Random rand) {
		
		float totalWeights = 0.0f;
		for(EquationTreeNode node: equationNodes) {
			if (node.isUnlocked()) {
				totalWeights += (1.1 - node.confidence());
			}
		}
		
		float randomWeight = totalWeights *= rand.nextFloat();
		
		EquationTreeNode lastNode = root;
		
		for(EquationTreeNode node: equationNodes) {
			if (node.isUnlocked()) {
				randomWeight -= (1 - node.confidence());
				if (randomWeight <= 0.0f) {
					return node;
				}
				lastNode = node;
			}
		}
		
		return lastNode;	
		
	}

	public EquationTreeNode root() {
		return root;
	}
	
	public boolean isReadyForStarredEquation() {
		for (EquationTreeNode node : equationNodes) {
			if (node.readyForTutorial()) return true;
		}
		return false;
	}
	
	public EquationTreeNode getStarredEquation() {
		for (EquationTreeNode node : equationNodes) {
			if (node.readyForTutorial()) {
				node.setTutorialShown(true);
				return node;
			}
		}
		return null;
	}

	public EquationTreeNode addInitialNode(EGenerator generator) {
		EquationTreeNode newNode = addNode(generator, new BlankCriteria());
			
		return newNode;
	}

	public EquationTreeNode addNode(EGenerator generator, Criteria c) {
		EquationTreeNode newNode = new EquationTreeNode(generator);
		
		newNode.preRequisites.add(c);
		
		equationNodes.add(newNode);	//cache the tree into a linear list for easy access
		
		return newNode;
	}

	public static class EquationTreeNode {
		private EGenerator generator;

		private List<Criteria> preRequisites = new ArrayList<Criteria>();

		private Confidence confidence = new DefaultConfidence();
		private AbstractStarredTutorial tutorial;
		private boolean tutorialShown;
		
		private EquationTreeNode(EGenerator generator) {
			this.generator = generator;
		}
		

		public boolean isUnlocked() {
			for(Criteria c: preRequisites) {
				if (!c.hasBeenSatisfied())		//TODO is it more efficient to pass the list here or to use the final 
												//references in the student model
					return false;
			}
			return true;
		}

		public boolean readyForTutorial() {
			return tutorial != null && !tutorialShown && isUnlocked();
		}
		
		public Equation generateEquation() {
			return generator.generate();
		}

		public float confidence() {
			return confidence == null ? 0 : confidence.confidence();
		}

		public void setConfidence(Confidence newConfidence) {
			this.confidence = newConfidence;
		}


		public AbstractStarredTutorial tutorial() {
			return tutorial;
		}


		public void setTutorial(AbstractStarredTutorial tutorial) {
			this.tutorial = tutorial;
		}


		public boolean isTutorialShown() {
			return tutorialShown;
		}


		public void setTutorialShown(boolean tutorialShow) {
			this.tutorialShown = tutorialShow;
		}

		public void setDependency(SingleValueDependency dependency) {
			confidence = dependency.createConfidence();
			preRequisites.add(dependency.createCriteria());
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

class DefaultConfidence implements Confidence {
	@Override
	public float confidence() {
		return 0.5f;
	}
}

interface Confidence {
	float confidence();
}

abstract class SingleValueDependency {
	abstract float value();
	abstract float minValue();
	
	public Criteria createCriteria() {
		return new Criteria() {
			@Override
			public boolean hasBeenSatisfied() {
				return value() > minValue();
			}
		};
	}
	
	public Confidence createConfidence() {
		return new Confidence() {
			@Override
			public float confidence() {
				return (value() - minValue()) / (1 - minValue());
			}
		};
	}
}