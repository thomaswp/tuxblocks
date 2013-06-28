package tuxkids.tuxblocks.core.defense.walker;


public class Peon extends SlideWalker {

	public Peon() {
		super(10, 500);
	}

	@Override
	public Walker copy() {
		return new Peon();
	}

}
