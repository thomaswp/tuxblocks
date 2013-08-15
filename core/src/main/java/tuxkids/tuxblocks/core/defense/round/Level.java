package tuxkids.tuxblocks.core.defense.round;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.defense.tower.TowerType;
import tuxkids.tuxblocks.core.defense.walker.FlipWalker;
import tuxkids.tuxblocks.core.defense.walker.InchWalker;
import tuxkids.tuxblocks.core.defense.walker.ShrinkWalker;
import tuxkids.tuxblocks.core.defense.walker.SlideWalker;
import tuxkids.tuxblocks.core.defense.walker.SpinWalker;
import tuxkids.tuxblocks.core.defense.walker.Walker;
import tuxkids.tuxblocks.core.title.Difficulty;
import tuxkids.tuxblocks.core.utils.Persistable;

public abstract class Level implements Persistable {
	private List<Round> rounds = new ArrayList<Round>();
	private int waitTime;
	
	private int timer;
	private Round currentRound;
	private boolean waitingForFinish;
	private Walker lastWalker;
	
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
	
	public Walker popWalker() {
		Walker walker = lastWalker;
		lastWalker = null;
		return walker;
	}
	
	public boolean waitingForFinish() {
		return waitingForFinish;
	}
	
	public int timeUntilNextRound() {
		if (currentRound != null) return 0;
		if (rounds.size() == 0) return 0;
		if (waitTime == Difficulty.ROUND_TIME_INFINITE) 
			return Difficulty.ROUND_TIME_INFINITE;
		return waitTime - timer;
	}

	public void startNextRound() {
		if (currentRound != null) return;
		if (rounds.size() == 0) return;
		nextRound();
	}
	
	public boolean duringRound() {
		return currentRound != null || rounds.size() == 0;
	}
	
	public boolean victory() {
		return currentRound == null && rounds.size() == 0;
	}
	
	protected abstract void populateLevel();
	
	public Level() {
		populateLevel();
	}
	
	protected void addRound(Round round) {
		rounds.add(round);
	}
	
	public void update(int delta) {
		Walker walker = updateWalker(delta);
		if (walker != null) lastWalker = walker;
	}
	
	private Walker updateWalker(int delta) {
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
		if (currentRound == null && rounds.size() > 0 && 
				waitTime >= 0 && timer >= waitTime) {
			nextRound();
			Audio.se().play(Constant.SE_PITCH_FINAL);
		}
		return null;
	}
	
	private void nextRound() {
		currentRound = rounds.remove(0);
		timer = 0;
		roundNumber++;
	}
	
	public boolean finished() {
		return rounds.size() == 0 && currentRound == null;
	}
	
	@Override
	public void persist(Data data) throws NumberFormatException, ParseDataException {
		waitTime = data.persist(waitTime);
		roundNumber = data.persist(roundNumber);
		timer = data.persist(timer);
		
		if (data.readMode()) {
			for (int i = 0; i < roundNumber; i++) rounds.remove(0);
		}
	}
	
	public static Level generate(int secondsBetween) {
		Level level = new Level1();
		if (secondsBetween > 0) secondsBetween *= 1000;
		level.waitTime = secondsBetween;
		return level;
	}
	
	public static class Level1 extends Level {
		
		@Override
		protected void populateLevel() {

			final Walker basic = new SlideWalker(10, 500);
			final Walker medium = new FlipWalker(30, 750);
			final Walker hard = new InchWalker(70, 1500);
			
			final Walker quick = new SpinWalker(15, 375);
			final Walker quicker = new ShrinkWalker(20, 250);
		
			addRound(new Round() {
				@Override
				protected void populateRound() {
					addWave(new Wave(basic, 1000, 5), 0);
					addReward(new Reward(TowerType.PeaShooter, 2));
				}
			});
			addRound(new Round() {
				@Override
				protected void populateRound() {
					addWave(new Wave(basic, 500, 5), 0);
					addWave(new Wave(basic, 500, 5), 2000);
					addReward(new Reward(TowerType.PeaShooter, 2));
				}
			});
			addRound(new Round() {
				@Override
				protected void populateRound() {
					addWave(new Wave(basic, 500, 3), 0);
					addWave(new Wave(medium, 1000, 3), 2000);
					addWave(new Wave(basic, 500, 3), 2000);
					addReward(new Reward(TowerType.BigShooter, 1));
				}
			});
			addRound(new Round() {
				@Override
				protected void populateRound() {
					addWave(new Wave(basic, 500, 15), 0);
					addReward(new Reward(TowerType.PeaShooter, 2));
				}
			});
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
			});
			addRound(new Round() {
				@Override
				protected void populateRound() {
					addWave(new Wave(quick, 250, 6), 0);
					addReward(new Reward(TowerType.HorizontalWall, 1));
					addReward(new Reward(TowerType.VerticalWall, 1));
				}
			});
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
			});
			addRound(new Round() {
				@Override
				protected void populateRound() {
					addWave(new Wave(basic, 300, 25), 0);
					addReward(new Reward(TowerType.Freezer, 1));
				}
			});
			addRound(new Round() {
				@Override
				protected void populateRound() {
					addWave(new Wave(medium, 500, 15), 0);
					addReward(new Reward(TowerType.Freezer, 1));
					addReward(new Reward(TowerType.BigShooter, 1));
				}
			});
			addRound(new Round() {
				@Override
				protected void populateRound() {
					addWave(new Wave(hard, 1500, 5), 0);
				}
			});
		}
		
		public static Constructor constructor() {
			return new Constructor() {
				@Override
				public Persistable construct() {
					return new Level1();
				}
			};
		}
	}
}
