package tuxkids.tuxblocks.core.defense.round;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.defense.Inventory;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.walker.Walker;

public abstract class Round {
	private List<Wave> waves = new ArrayList<Wave>();
	private List<Integer> waitTimes = new ArrayList<Integer>();
	
	private int timer;
	private Wave currentWave;
	private int nextDepth;
	private List<Reward> rewards = new ArrayList<Reward>();
	
	protected abstract void populateRound();

	public float nextDepth() {
		return nextDepth;
	}
	
	public Round() {
		populateRound();
	}
	
	protected void addWave(Wave wave, int waitTime) {
		waves.add(wave);
		waitTimes.add(waitTime);
	}
	
	protected void addReward(Reward reward) {
		rewards.add(reward);
	}
	
	public void winRound(GameState gameState) {
		for (Reward reward : rewards) {
			gameState.addProblemWithReward(reward);
		}
	}
	
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
			nextDepth = Walker.MAX_BASE_DEPTH;
			timer = 0;
		}
		return null;
	}
	
	public boolean finished() {
		return waves.size() == 0 && currentWave == null;
	}
}
