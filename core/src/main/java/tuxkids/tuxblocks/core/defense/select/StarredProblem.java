package tuxkids.tuxblocks.core.defense.select;

import tuxkids.tuxblocks.core.defense.round.Reward;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

public class StarredProblem extends Problem {

	private int tutorialId;
	
	public int tutorialId() {
		return tutorialId;
	}
	
	public StarredProblem(Equation equation, Reward reward, int tutorialId) {
		super(equation, reward);
		this.tutorialId = tutorialId;
	}
	
	@Override
	public void persist(Data data) throws NumberFormatException, ParseDataException {
		super.persist(data);
		tutorialId = data.persist(tutorialId);
	}

	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new StarredProblem(null, null, 0);
			}
		};
	}
}
