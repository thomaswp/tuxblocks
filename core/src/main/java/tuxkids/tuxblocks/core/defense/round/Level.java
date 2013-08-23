package tuxkids.tuxblocks.core.defense.round;

import java.util.ArrayList;
import java.util.List;

import pythagoras.f.FloatMath;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.defense.tower.TowerType;
import tuxkids.tuxblocks.core.defense.walker.FlipWalker;
import tuxkids.tuxblocks.core.defense.walker.FlyWalker;
import tuxkids.tuxblocks.core.defense.walker.InchWalker;
import tuxkids.tuxblocks.core.defense.walker.ShrinkWalker;
import tuxkids.tuxblocks.core.defense.walker.SlideWalker;
import tuxkids.tuxblocks.core.defense.walker.SpinWalker;
import tuxkids.tuxblocks.core.defense.walker.Walker;
import tuxkids.tuxblocks.core.title.Difficulty;
import tuxkids.tuxblocks.core.utils.Debug;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

public abstract class Level implements Persistable {
	
	protected List<Round> rounds = new ArrayList<Round>();
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
	
	public void skipRounds(int n) {
		for (int i = 0; i < n; i++) {
			rounds.remove(0);
		}
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
		
		protected final static int ROUNDS = 25;
		
		@Override
		protected void populateLevel() {

//			final Walker basic = new SlideWalker(10, 500);
//			final Walker medium = new FlipWalker(30, 750);
//			final Walker hard = new InchWalker(70, 1500);
//			
//			final Walker quick = new SpinWalker(15, 375);
//			final Walker quicker = new ShrinkWalker(20, 250);
			
			Walker[] walkers = new Walker[] {
					new SlideWalker(10, 500),
					new FlipWalker(30, 750),
					new InchWalker(100, 1500),
					new SpinWalker(20, 375),
					new ShrinkWalker(30, 250),
					new FlyWalker(40, 750),
					

					new SlideWalker(40, 400).setLevel(1),
					new FlipWalker(120, 650).setLevel(1),
					new InchWalker(400, 1200).setLevel(1),
					new SpinWalker(80, 325).setLevel(1),
					new ShrinkWalker(120, 200).setLevel(1),
					new FlyWalker(160, 650).setLevel(1),
					
					
					new SlideWalker(160, 300).setLevel(2),
					new FlipWalker(500, 550).setLevel(2),
					new InchWalker(1500, 1000).setLevel(2),
					new SpinWalker(320, 300).setLevel(2),
					new ShrinkWalker(480, 175).setLevel(2),
					new FlyWalker(600, 550).setLevel(2),
			};
			
			ArrayList<Walker> possibleWalkers = new ArrayList<Walker>(); 
			
			for (int i = 0; i < ROUNDS; i++) {
				possibleWalkers.clear();
				float base = i * i / (i + 2.5f) * 2f + FloatMath.pow(i * 5 / 2 / ROUNDS, 4.5f) + 1;
				int points = (int) (base * walkers[0].exp() * 5);
				for (int j = 0; j < walkers.length; j++) {
					Walker walker = walkers[j];
					int count = points / walker.exp();
					if ((j == 0 || count >= 8)  && count <= 20) {
						possibleWalkers.add(walker);
					}
				}
				final Walker walker = possibleWalkers.get(
						(int) (possibleWalkers.size() * Math.random()));
				int exp = walker.exp();
				
				int minBase = exp * 5;
				int speedupCap = 6;
				float maxSpeedup = Math.min(speedupCap - 1, (float) (points - minBase) / minBase);
				float speedup = (float) Math.random() * maxSpeedup + 1;
				final int frequency = (int)(walker.walkCellTime() * 2 / speedup);
				int frequencyPoints = (int) ((speedup - 1) * minBase);
				
				final int n = (points - frequencyPoints) / exp;
				int maxGroups = Math.min(5, n / 3);
				int groups = 1 + (int) (Math.random() * (maxGroups - 1));
				final int perGroup = Math.round((float) n / groups);
				
				addRound(new Round() {
					@Override
					protected void populateRound() {
						int wait = 0;
						int remaining = n;
						while (remaining > 0) {
							int count = Math.min(remaining, perGroup);
							addWave(new Wave(walker, frequency, count), wait);
							remaining -= count;
							wait = 2000;
						}
					}
				});
			}
		
//			addRound(new Round() {
//				@Override
//				protected void populateRound() {
//					addWave(new Wave(basic, 1000, 5), 0);
//				}
//			});
//			addRound(new Round() {
//				@Override
//				protected void populateRound() {
//					addWave(new Wave(basic, 500, 5), 0);
//					addWave(new Wave(basic, 500, 5), 2000);
//				}
//			});
//			addRound(new Round() {
//				@Override
//				protected void populateRound() {
//					addWave(new Wave(medium, 1000, 3), 2000);
//					addWave(new Wave(medium, 1000, 3), 2000);
//				}
//			});
//			addRound(new Round() {
//				@Override
//				protected void populateRound() {
//					addWave(new Wave(basic, 500, 15), 0);
//				}
//			});
//			addRound(new Round() {
//				@Override
//				protected void populateRound() {
//					addWave(new Wave(basic, 500, 2), 0);
//					addWave(new Wave(medium, 500, 2), 500);
//					addWave(new Wave(basic, 500, 2), 500);
//					addWave(new Wave(medium, 500, 2), 500);
//					addWave(new Wave(basic, 500, 2), 500);
//					addWave(new Wave(medium, 500, 2), 500);
//				}
//			});
//			addRound(new Round() {
//				@Override
//				protected void populateRound() {
//					addWave(new Wave(quick, 250, 6), 0);
//				}
//			});
//			addRound(new Round() {
//				@Override
//				protected void populateRound() {
//					addWave(new Wave(medium, 500, 3), 0);
//					addWave(new Wave(quick, 500, 3), 1000);
//					addWave(new Wave(medium, 500, 4), 1000);
//					addWave(new Wave(quick, 500, 4), 1000);
//				}
//			});
//			addRound(new Round() {
//				@Override
//				protected void populateRound() {
//					addWave(new Wave(basic, 300, 25), 0);
//				}
//			});
//			addRound(new Round() {
//				@Override
//				protected void populateRound() {
//					addWave(new Wave(medium, 500, 15), 0);
//				}
//			});
//			addRound(new Round() {
//				@Override
//				protected void populateRound() {
//					addWave(new Wave(hard, 1500, 5), 0);
//				}
//			});
			
			int i = 0;
			for (Round round : rounds) {
				round.addRandomReward((float) i / rounds.size());
				i++;
			}
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
