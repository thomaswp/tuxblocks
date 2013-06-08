package tuxkids.tuxblocks.core.utils;

import playn.core.PlayN;


/**
 * A class for centralizing debugging, so that it can be universally
 * enabled/disabled or redirected. Currently uses System.out for output.
 */
public class Debug {
	/**
	 * Set to true to have Debug do output, and false to stifle it
	 */
	public static boolean DEBUG = true;
	
	public static void write(long x) {
		write("" + x);
	}
	
	public static void write(int x) {
		write("" + x);
	}
	
	public static void write(Object o) {
		write(o == null ? "null" : o.toString());
	}

	public static void write(float x) {
		write("" + x);
	}

	public static void write(String format, Object... args) {
		try {
			write(Formatter.format(format, args));
		} catch (Exception e) {
			write(e);
		}
	}
	
	public static void write(Exception e) {
		if (DEBUG) e.printStackTrace();
	}
	
	/**
	 * A method to write specially formatted debug text.
	 * 
	 * @param text The text to be written.
	 */
	@SuppressWarnings("unused")
	public static void write(String text) {
		if (!DEBUG) return;
		
		String msg = "---{" + text + "}---";
//		String tag = "";
//		try
//		{
//			throw new Exception("Who called me?");
//		}
//		catch( Exception e )
//		{
//			int i = 1;
//			while (e.getStackTrace()[i].getClassName().equals(Debug.class.getName()) &&
//					e.getStackTrace()[i].getMethodName().equals("write")) i++;
//			tag += ": " +
//			e.getStackTrace()[i].getClassName() + 
//			"." +
//			e.getStackTrace()[i].getMethodName() + 
//			"()" ;
//		}
		PlayN.log().debug(msg);
	}
}
