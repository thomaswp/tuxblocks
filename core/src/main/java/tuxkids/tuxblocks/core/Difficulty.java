package tuxkids.tuxblocks.core;

public class Difficulty {

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
}
