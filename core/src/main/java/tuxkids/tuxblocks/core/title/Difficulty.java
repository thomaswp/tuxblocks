package tuxkids.tuxblocks.core.title;

import tuxkids.tuxblocks.core.utils.persist.Persistable;

public class Difficulty implements Persistable {

	public final static int ROUND_TIME_INFINITE = -1;
	public final static int[] TIMES = new int[] { ROUND_TIME_INFINITE, 90, 70, 50, 30 };
	public static final int MAX_MATH_DIFFICULTY = 4;
	
	private static double rankN(double n) {
		return Math.pow(Math.log(Math.abs(n) + 1), 2) / 3 + 1;
	}
	
	private static double rankM(double n) {
		return (int)(n / 5) + 1;
	}	
	
	public static int rankPlus(int a, int b) {
		return (int)Math.min(rankN(a), rankN(b));
	}
	
	public static int rankMinus(int a, int b) {
		return (int)Math.min(rankN(a), rankN(b));
	}
	
	public static int rankTimes(int a, int b) { 
		return (int)rankM(Math.pow(Math.abs(a * b), 0.5f));
	}
	
	public static int rankOver(int a, int b)  {
		return (int)rankM(Math.pow(Math.abs(a), 0.5f) + Math.abs(b));
	}
	
	public int mathDifficulty, gameDifficulty, roundTime;
	
	public Difficulty() {
		this(0, 0, ROUND_TIME_INFINITE);
	}
	
	public Difficulty(int mathDifficulty, int gameDifficulty, int roundTime) {
		this.mathDifficulty = mathDifficulty;
		this.gameDifficulty = gameDifficulty;
		this.roundTime = roundTime;
	}
	
	public float getWalkerHpMultiplier() {
		return 1 +  (gameDifficulty - 2) * 0.25f;
	}

	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new Difficulty();
			}
			
		};
	}

	@Override
	public void persist(Data data) throws ParseDataException,
			NumberFormatException {
		mathDifficulty = data.persist(mathDifficulty);
		gameDifficulty = data.persist(gameDifficulty);
		roundTime = data.persist(roundTime);
	}
}
