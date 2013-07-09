package tuxkids.tuxblocks.core.solve.blocks.n;

import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.screen.GameScreen;

public class SolveScene extends GameScreen {

	public SolveScene(ScreenStack screens, GameState state) {
		super(screens, state);
	}
	
	@Override
	public void wasAdded() {
		BaseBlock block = new VariableBlock("x");
		block.addModifier(new PlusBlock(5));
		block.addModifier(new MinusBlock(2));
		block.addModifier(new OverBlock(3));
		block.addModifier(new PlusBlock(3));
		
		System.out.println(block.toMathString());
	}

}
