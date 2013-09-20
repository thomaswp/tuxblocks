package tuxkids.tuxblocks.core.defense.round;

import java.util.ArrayList;
import java.util.List;

import pythagoras.f.FloatMath;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.defense.walker.FlipWalker;
import tuxkids.tuxblocks.core.defense.walker.FlyWalker;
import tuxkids.tuxblocks.core.defense.walker.InchWalker;
import tuxkids.tuxblocks.core.defense.walker.ShrinkWalker;
import tuxkids.tuxblocks.core.defense.walker.SlideWalker;
import tuxkids.tuxblocks.core.defense.walker.SpinWalker;
import tuxkids.tuxblocks.core.defense.walker.Walker;
import tuxkids.tuxblocks.core.title.Difficulty;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

/**
 * Represents a level in the game. Currently games only consist of
 * a single level, but the architecture exists to change this. The Level
 * contains {@link Round}s defining which {@link Walker}s will spawn
 * and when. The Level should be updated with {@link Level#update(int)}
 * and the {@link Level#popWalker()} can be checked to see if a Walker 
 * should be spawned at this point in time. When a Round is finished,
 * meaning all Walkers are destroyed, the {@link Level#finishRound()}
 * method should be called to indicate that timer until the next Round
 * should start.
 */
public abstract class Level implements Persistable {
	
	protected List<Round> rounds = new ArrayList<Round>();
	private int waitTime;
	
	private int timer;
	private Round currentRound;
	private boolean waitingForFinish;
	private Walker lastWalker;
	
	private int roundNumber = 0;
	
	/**
	 * Returns n, whether the current
	 * round is the nth round. Returns the
	 * most recent round if in between rounds.
	 */
	public int roundNumber() {
		return roundNumber;
	}
	
	/**
	 * Returns the current Round or null if in
	 * between rounds.
	 */
	public Round currentRound() {
		return currentRound;
	}
	
	/**
	 * Indicates to this Level that the current
	 * Round has finished and the timer to start
	 * the next Round should start.
	 */
	public void finishRound() {
		waitingForFinish = false;
		currentRound = null;
	}
	
	/**
	 * Checks to see if a new {@link Walker}
	 * should be spawned. If so, the walker is
	 * removed from this Level and returned, or
	 * if not null is returned.
	 */
	public Walker popWalker() {
		Walker walker = lastWalker;
		lastWalker = null;
		return walker;
	}
	
	/**
	 * Returns true if the current Level is
	 * waiting for the {@link Level#finishRound()} method
	 * to be called.
	 */
	public boolean waitingForFinish() {
		return waitingForFinish;
	}
	
	/**
	 * Skips the next n Rounds. This is useful for
	 * loading a saved game. The original Level can be
	 * loaded and Rounds skipped to get to where the player
	 * left off.
	 */
	public void skipRounds(int n) {
		for (int i = 0; i < n; i++) {
			rounds.remove(0);
		}
	}
	
	/**
	 * Returns the time remaining until the next {@link Round} starts 
	 * (in milliseconds) or 0 if a Round is in progress.
	 */
	public int timeUntilNextRound() {
		if (currentRound != null) return 0;
		if (rounds.size() == 0) return 0;
		if (waitTime == Difficulty.ROUND_TIME_INFINITE) 
			return Difficulty.ROUND_TIME_INFINITE;
		return waitTime - timer;
	}

	/**
	 * If between {@link Round}s, ends the countdown until the next Round
	 * and starts it immediately instead.
	 */
	public void startNextRound() {
		if (currentRound != null) return;
		if (rounds.size() == 0) return;
		nextRound();
	}
	
	/**
	 * Returns true if the Level is in the middle of a {@link Round}.
	 */
	public boolean duringRound() {
		return currentRound != null || rounds.size() == 0;
	}
	
	/**
	 * Returns true if the player has finished the Level.
	 */
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
		if (victory()) return null;
		if (currentRound != null) {
			if (waitingForFinish) return null; // wait for player to kill Walkers
			Walker walker = currentRound.update(delta); // get the next Walker from the Round
			if (currentRound.finished()) {
				waitingForFinish = true;
			}
			return walker;
		}
		timer += delta; // countdown until the next Round
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
	
	@Override
	public void persist(Data data) throws NumberFormatException, ParseDataException {
		waitTime = data.persist(waitTime);
		roundNumber = data.persist(roundNumber);
		timer = data.persist(timer);
		
		if (data.readMode()) {
			for (int i = 0; i < roundNumber; i++) rounds.remove(0);
		}
	}
	
	/**
	 * Generates a random Level with the given amount of time
	 * between each {@link Round}.
	 */
	public static Level generate(int secondsBetween) {
		Level level = new Level1();
		if (secondsBetween > 0) secondsBetween *= 1000;
		level.waitTime = secondsBetween;
		return level;
	}
	
	/**
	 * Randomly generated first Level, generated with
	 * {@link Level#generate(int)}.
	 */
	public static class Level1 extends Level {
		
		protected final static int ROUNDS = 25;
		
		@Override
		protected void populateLevel() {
			
			Walker[] walkers = new Walker[] {
					// three iterations of difficulty
					
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
			
			// create  Rounds out of the above walkers
			for (int i = 0; i < ROUNDS; i++) {
				possibleWalkers.clear();
				
				// create a number of "points" based on the round # that are distributed amongst
				// the Walkers in the round. This allows for variety at a (ideally) equal difficulty
				// These "points" are "spent" on Walkers (better ones cost more) and on speeding up
				// their spawn rate
				float base = i * i / (i + 2.5f) * 2f + FloatMath.pow(i * 5 / 2 / ROUNDS, 4.5f) + 1;
				int points = (int) (base * walkers[0].exp() * 5);
				
				// we choose possible Walkers that would make 8-20 in a Round 
				for (int j = 0; j < walkers.length; j++) {
					Walker walker = walkers[j];
					int count = points / walker.exp();
					// the most basic Walker can have less than 8 in a round (like for the 1st Round)
					if ((j == 0 || count >= 8)  && count <= 20) {
						possibleWalkers.add(walker);
					}
				}
				
				// Choose a Walker - note that in this generation algorithm we choose to use only 1
				// type of Walker per round, but this is not necessary. This was done to avoid slower
				// Walkers being overcome by faster ones and creating a jumble
				final Walker walker = possibleWalkers.get(
						(int) (possibleWalkers.size() * Math.random()));
				int exp = walker.exp();
				
				int minBase = exp * 5; // min points "spent" on # of Walkers
				int speedupCap = 6; // max times the spawn rate can be sped up
				// max speedup we can get with at least 5 walkers and our given points
				float maxSpeedup = Math.min(speedupCap - 1, (float) (points - minBase) / minBase);
				float speedup = (float) Math.random() * maxSpeedup + 1; // randomly allocate to speedup
				final int frequency = (int)(walker.walkCellTime() * 2 / speedup); // translate to frequency
				int frequencyPoints = (int) ((speedup - 1) * minBase); // and calculate the cost
				
				final int n = (points - frequencyPoints) / exp; // allocate the rest to # of Walkers
				// divide the Walkers into subgroups (of at least 3 each)
				int maxGroups = Math.min(5, n / 3);
				int groups = 1 + (int) (Math.random() * (maxGroups - 1));
				final int perGroup = Math.round((float) n / groups);
				
				addRound(new Round() {
					@Override
					protected void populateRound() {
						// add the Walkers as determined above
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
			
			// add applicable rewards to the Walkers
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
