package tuxkids.tuxblocks.core.tutorial.linear;

import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.defense.round.Reward;
import tuxkids.tuxblocks.core.defense.select.Problem;
import tuxkids.tuxblocks.core.solve.blocks.BlockHolder;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.NumberBlock;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;
import tuxkids.tuxblocks.core.title.Difficulty;

/** {@link GameState} to use when playing during the tutorial. */
public class TutorialGameState extends GameState {

	private int eqationIndex;
	
	// prepackaged equations to use during the tutorial
	private final static Equation[] equations = new Equation[] {
			new Equation.Builder()
			.addLeft(new VariableBlock("x").add(3).times(4))
			.addRight(new NumberBlock(16))
			.createEquation(),
			
			new Equation.Builder()
			.addLeft(new VariableBlock("x").times(3))
			.addLeft(new VariableBlock("x").minus(7).over(4))
			.addRight(new NumberBlock(8))
			.addRight(new BlockHolder())
			.createEquation(),
			
			new Equation.Builder()
			.addLeft(new VariableBlock("x"))
			.addRight(new NumberBlock(4).over(2))
			.createEquation(),
	};
	
	public TutorialGameState() {
		super(new Difficulty(0, 0, Difficulty.ROUND_TIME_INFINITE));
		upgrades = 1;
	}

	@Override
	protected Problem createProblem(int difficulty, float percFinished, Reward reward) {
		if (eqationIndex < equations.length) {
			return new Problem(equations[eqationIndex++], reward);
		}
		return super.createProblem(difficulty, percFinished, reward);
	}
}
