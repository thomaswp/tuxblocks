package tuxkids.tuxblocks.core.student;

import java.io.Serializable;

public class KnowledgeComponent implements Serializable {
	
	private String humanDescription;
	
	private final double L_0, slip, guess, transition;	
	
	private double probLearned;
	
	

	public KnowledgeComponent(String humanDescription, double initialLearn, double slip,
			double guess, double transition) {

		this.humanDescription = humanDescription;
		this.L_0 = initialLearn;
		this.slip = slip;
		this.guess = guess;
		this.transition = transition;
	}

	public void studentAnswered(boolean wasCorrect) {
		
	}
	
	public String humanDescription() {
		return humanDescription;
	}
	
	public double probLearned() {
		return probLearned;
	}

}
