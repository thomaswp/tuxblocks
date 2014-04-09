package tuxkids.tuxblocks.core.student;

import tuxkids.tuxblocks.core.utils.persist.Persistable;

public class KnowledgeComponent implements Persistable {
	
	public final String humanDescription;
	public final double L_0, slip, guess, transition;	
	
	private double probLearned;
	
	public double probLearned() {
		return probLearned;
	}
	
	public KnowledgeComponent(String humanDescription, double initialLearn, double slip,
			double guess, double transition) {

		this.humanDescription = humanDescription;
		this.L_0 = initialLearn;
		this.slip = slip;
		this.guess = guess;
		this.transition = transition;
		this.probLearned = L_0;
	}

	public void studentAnswered(boolean wasCorrect) {
		double slip = this.slip;
		if (wasCorrect) slip = 1 - slip;
		double guess = this.guess;
		if (!wasCorrect) guess = 1 - slip;
		
		double lUpdated = (probLearned * slip) / (probLearned * slip + (1 - probLearned) * guess);
		probLearned = lUpdated + (1 - probLearned) * transition;
	}

	@Override
	public void persist(Data data) throws ParseDataException,
			NumberFormatException {
		probLearned = data.persist(probLearned);
	}

}
