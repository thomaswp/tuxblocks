package tuxkids.tuxblocks.core.defense.walker;

import pythagoras.i.Point;
import tuxkids.tuxblocks.core.defense.Grid;

public class Peon extends SlideWalker {

	@Override
	public int getMaxHp() {
		return 10;
	}

	@Override
	public int walkCellTime() {
		return 500;
	}

}
