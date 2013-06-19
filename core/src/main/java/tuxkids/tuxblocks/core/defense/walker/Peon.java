package tuxkids.tuxblocks.core.defense.walker;


public class Peon extends SlideWalker {

	@Override
	public int getMaxHp() {
		return 10;
	}

	@Override
	public int walkCellTime() {
		return 500;
	}

	@Override
	public Walker copy() {
		return new Peon();
	}

}
