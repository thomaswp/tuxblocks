package tuxkids.tuxblocks.core.utils;

import playn.core.PlayN;


/**
 * A class for centralizing debugging, so that it can be universally
 * enabled/disabled or redirected. Currently uses {@link PlayN#log()} for output.
 */
public class Debug {
	/** Set to true to have Debug do output, and false to stifle it	 */
	public static boolean DEBUG = true;
	
	/** Set to true to show what class is printing a debug statement */
	public static boolean SHOW_TRACE = false;
	
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
	
	public static void write() {
		write("");
	}
	
	public static void write(Exception e) {
		if (DEBUG) e.printStackTrace();
	}
	
	/**
	 * A method to write specially formatted debug text.
	 * 
	 * @param text The text to be written.
	 */
	public static void write(String text) {
		if (!DEBUG) return;
		
		if (SHOW_TRACE) {
			try
			{
				throw new Exception("Who called me?");
			}
			catch( Exception e )
			{
				int i = 1;
				while (e.getStackTrace()[i].getClassName().equals(Debug.class.getName()) &&
						"write".equals(e.getStackTrace()[i].getMethodName())) i++;
				while (e.getStackTrace()[i].getClassName().equals(PlayNObject.class.getName()) &&
						"debug".equals(e.getStackTrace()[i].getMethodName())) i++;
				while (e.getStackTrace()[i].getClassName().equals(CanvasUtils.class.getName())) i++;
				String cName = e.getStackTrace()[i].getClassName();
				int index = cName.lastIndexOf(".");
				if (index > 0 && index < cName.length() - 1) 
					cName = cName.substring(index + 1);
				text = 
				cName + "." +
				e.getStackTrace()[i].getMethodName() + 
				"(): " + text;
			}
		}
		PlayN.log().debug(text);
	}
}
