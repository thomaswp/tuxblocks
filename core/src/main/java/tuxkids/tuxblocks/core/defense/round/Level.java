package tuxkids.tuxblocks.core.defense.round;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.defense.tower.TowerType;
import tuxkids.tuxblocks.core.defense.walker.FlipWalker;
import tuxkids.tuxblocks.core.defense.walker.InchWalker;
import tuxkids.tuxblocks.core.defense.walker.ShrinkWalker;
import tuxkids.tuxblocks.core.defense.walker.SlideWalker;
import tuxkids.tuxblocks.core.defense.walker.SpinWalker;
import tuxkids.tuxblocks.core.defense.walker.Walker;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;

public abstract class Level {
	private List<Round> rounds = new ArrayList<Round>();
	private List<Integer> waitTimes = new ArrayList<Integer>();
	
	private int timer;
	private Round currentRound;
	private boolean waitingForFinish;
	
	private int roundNumber = 0;
	
	public int roundNumber() {
		return roundNumber;
	}
	
	public Round currentRound() {
		return currentRound;
	}
	
	public void finishRound() {
		waitingForFinish = false;
		currentRound = null;
	}
	
	public boolean waitingForFinish() {
		return waitingForFinish;
	}
	
	public int timeUntilNextRound() {
		if (currentRound != null) return 0;
		if (waitTimes.size() == 0) return 0;
		return waitTimes.get(0) - timer;
	}

	public void startNextRound() {
		if (currentRound != null) return;
		if (waitTimes.size() == 0) return;
		timer = waitTimes.get(0);
	}
	
	public boolean duringRound() {
		return currentRound != null;
	}
	
	protected abstract void populateLevel();
	
	public Level() {
		populateLevel();
	}
	
	protected void addRound(Round round, float waitTimeSeconds) {
		rounds.add(round);
		waitTimes.add((int)(waitTimeSeconds * 1000));
	}
	
	public Walker update(int delta) {
		if (finished()) return null;
		if (currentRound != null) {
			if (waitingForFinish) return null;
			Walker walker = currentRound.update(delta);
			if (currentRound.finished()) {
				waitingForFinish = true;
			}
			return walker;
		}
		timer += delta;
		if (currentRound == null && waitTimes.size() > 0 && timer >= waitTimes.get(0)) {
			currentRound = rounds.remove(0);
			waitTimes.remove(0);
			timer = 0;
			roundNumber++;
		}
		return null;
	}
	
	public boolean finished() {
		return rounds.size() == 0 && currentRound == null;
	}
	
	public static Level generate(final int timeBetween) {
		
		final Walker basic = new SlideWalker(10, 500);
		final Walker medium = new FlipWalker(30, 750);
		final Walker hard = new InchWalker(70, 1500);
		
		final Walker quick = new SpinWalker(15, 375);
		final Walker quicker = new ShrinkWalker(20, 250);
		
		return new Level() {
			@Override
			protected void populateLevel() {
				addRound(new Round() {
					@Override
					protected void populateRound() {
						addWave(new Wave(basic, 1000, 5), 0);
						addReward(new Reward(TowerType.PeaShooter, 2));
					}
				}, timeBetween);
				addRound(new Round() {
					@Override
					protected void populateRound() {
						addWave(new Wave(basic, 500, 5), 0);
						addWave(new Wave(basic, 500, 5), 2000);
						addReward(new Reward(TowerType.PeaShooter, 2));
					}
				}, timeBetween);
				addRound(new Round() {
					@Override
					protected void populateRound() {
						addWave(new Wave(basic, 500, 3), 0);
						addWave(new Wave(medium, 1000, 3), 2000);
						addWave(new Wave(basic, 500, 3), 2000);
						addReward(new Reward(TowerType.BigShooter, 1));
					}
				}, timeBetween);
				addRound(new Round() {
					@Override
					protected void populateRound() {
						addWave(new Wave(basic, 500, 15), 0);
						addReward(new Reward(TowerType.PeaShooter, 2));
					}
				}, timeBetween);
				addRound(new Round() {
					@Override
					protected void populateRound() {
						addWave(new Wave(basic, 500, 2), 0);
						addWave(new Wave(medium, 500, 2), 500);
						addWave(new Wave(basic, 500, 2), 500);
						addWave(new Wave(medium, 500, 2), 500);
						addWave(new Wave(basic, 500, 2), 500);
						addWave(new Wave(medium, 500, 2), 500);
						addReward(new Reward(TowerType.BigShooter, 1));
					}
				}, timeBetween);
				addRound(new Round() {
					@Override
					protected void populateRound() {
						addWave(new Wave(quick, 250, 6), 0);
						addReward(new Reward(TowerType.HorizontalWall, 1));
						addReward(new Reward(TowerType.VerticalWall, 1));
					}
				}, timeBetween);
				addRound(new Round() {
					@Override
					protected void populateRound() {
						addWave(new Wave(medium, 500, 3), 0);
						addWave(new Wave(quick, 500, 3), 1000);
						addWave(new Wave(medium, 500, 4), 1000);
						addWave(new Wave(quick, 500, 4), 1000);
						addReward(new Reward(TowerType.BigShooter, 1));
						addReward(new Reward(TowerType.PeaShooter, 2));
					}
				}, timeBetween);
				addRound(new Round() {
					@Override
					protected void populateRound() {
						addWave(new Wave(basic, 300, 25), 0);
						addReward(new Reward(TowerType.Freezer, 1));
					}
				}, timeBetween);
				addRound(new Round() {
					@Override
					protected void populateRound() {
						addWave(new Wave(medium, 500, 15), 0);
						addReward(new Reward(TowerType.Freezer, 1));
						addReward(new Reward(TowerType.BigShooter, 1));
					}
				}, timeBetween);
				addRound(new Round() {
					@Override
					protected void populateRound() {
						addWave(new Wave(hard, 1500, 5), 0);
					}
				}, timeBetween);
			}
		};
	}
}
