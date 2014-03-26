package tuxkids.tuxblocks.java;

import playn.core.Game;

public class TestGame extends Game.Default {

	private Runnable test;
	
	public TestGame(Runnable test) {
		super(1);
		this.test = test;
	}

	@Override
	public void init() {
		test.run();
	}

}
