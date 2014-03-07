package tuxkids.tuxblocks.core.student;

public class KnowledgeComponent {
	
	private String humanDescription;
	
	private float L_0, slip, guess, transition;		//TODO make final
	
	private float probLearned;
	
	
	
	
	public void studentAnswered(boolean wasCorrect) {
		
	}
	
	public String humanDescription() {
		return humanDescription;
	}
	
	public float probLearned() {
		return probLearned;
	}

}
