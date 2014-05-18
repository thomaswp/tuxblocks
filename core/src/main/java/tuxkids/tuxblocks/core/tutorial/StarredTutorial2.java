package tuxkids.tuxblocks.core.tutorial;

import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.NumberBlock;
import tuxkids.tuxblocks.core.tutorial.gen.StarredTutorial2_Base;

public class StarredTutorial2 extends AbstractStarredTutorial implements StarredTutorial2_Base {

	@Override
	public String filename() {
		return filename;
	}

	@Override
	protected void setUpStates() {
		addStartState(id_intro);
	}

	@Override
	public Equation createEquation() {
		return new Equation.Builder()
		.addLeft("x")
		.addRight(new NumberBlock(6).plus(3).over(3))
		.createEquation();
	}

}
