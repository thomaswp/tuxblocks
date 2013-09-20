package tuxkids.tuxblocks.core.defense.round;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.defense.GridObject;
import tuxkids.tuxblocks.core.defense.walker.Walker;

/**
 * Represents on round in a {@link Level}, consisting of
 * multiple {@link Wave}s of {@link Walker}.
 *
 */
public abstract class Round {
	
	private List<Wave> waves = new ArrayList<Wave>();
	// times in between waves
	private List<Integer> waitTimes = new ArrayList<Integer>();
	private float percFinished;
	
	private int timer;
	private Wave currentWave;
	private int nextDepth;
	
	protected abstract void populateRound();

	public float nextDepth() {
		return nextDepth;
	}
	
	public Round() {
		populateRound();
	}
	
	/** Called in the {@link Round#populateRound()} method to add a Wave */
	protected void addWave(Wave wave, int waitTime) {
		waves.add(wave);
		waitTimes.add(waitTime);
	}
	
	/** 
	 * Called in the {@link Round#populateRound()} method to add a 
	 * {@link Reward} for completing the Round 
	 */
	protected void addRandomReward(float percFinished) {
		this.percFinished = percFinished;
	}
	
	/**
	 * Indicates that this Round has been successfully completed
	 * and the {@link Reward}s should be added to the given GameSate.
	 */
	public void winRound(GameState gameState) {
		for (int i = 0; i < 2; i++) {
			gameState.addProblemWithReward(percFinished);
		}
	}
	
	/**
	 * Updates the Round and returns a Walker if one
	 * should be spawned.
	 */
	public Walker update(int delta) {
		if (finished()) return null;
		if (currentWave != null) {
			Walker walker = currentWave.update(delta);
			if (walker != null) nextDepth--;
			if (currentWave.finished()) {
				currentWave = null;
			}
			return walker;
		}
		timer += delta;
		if (currentWave == null && waitTimes.size() > 0 && timer >= waitTimes.get(0)) {
			currentWave = waves.remove(0);
			waitTimes.remove(0);
			nextDepth = GridObject.MAX_BASE_DEPTH;
			timer = 0;
		}
		return null;
	}
	
	/**
	 * Returns true if this round is completed.
	 */
	public boolean finished() {
		return waves.size() == 0 && currentWave == null;
	}
}
