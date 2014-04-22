package tuxkids.tuxblocks.core.tutorial;

import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;
import tuxkids.tuxblocks.core.tutorial.gen.StarredTutorial1_Base;

public class StarredTutorial1 extends AbstractStarredTutorial implements StarredTutorial1_Base {

	@Override
	public String filename() {
		return filename;
	}

	@Override
	protected void setUpStates() {
		FSMState introState = addStartState(id_intro);

	}

	@Override
	public Equation createEquation() {
		//x+8 = 5
		return new Equation.Builder().addLeft(new VariableBlock("x").plus(8))
				.addRight(5).createEquation();
	}

}
