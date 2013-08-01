package tuxkids.tuxblocks.core;

public class Difficulty {

	private static double rankN(double n) {
		return Math.pow(Math.log(Math.abs(n) + 1), 2) / 3 + 1;
	}
	
	public static int rankPlus(int a, int b) {
		return (int)Math.min(rankN(a), rankN(b));
	}
	
	public static int rankMinus(int a, int b) {
		return (int)Math.min(rankN(a), rankN(b));
	}
	
	public static int rankTimes(int a, int b) { 
		return (int)rankN(a * b);
	}
	
	public static int rankOver(int a, int b)  {
		return (int)rankN(a + b);
	}
}
