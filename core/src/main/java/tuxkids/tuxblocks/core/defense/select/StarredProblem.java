package tuxkids.tuxblocks.core.defense.select;

import tuxkids.tuxblocks.core.defense.round.Reward;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.tutorial.TutorialInstance;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

public class StarredProblem extends Problem {

	private TutorialInstance tutorial;
	
	public TutorialInstance tutorial() {
		return tutorial;
	}
	
	public StarredProblem(Equation equation, Reward reward, TutorialInstance tutorial) {
		super(equation, reward);
		this.tutorial = tutorial;
	}
	
	@Override
	public void persist(Data data) throws NumberFormatException, ParseDataException {
		super.persist(data);
		// TODO: Persist the tutorial (meaning don't actually hold the tutorial)
	}

	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new StarredProblem(null, null, null);
			}
		};
	}
}
