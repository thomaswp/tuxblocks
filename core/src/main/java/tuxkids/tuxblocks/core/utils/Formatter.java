package tuxkids.tuxblocks.core.utils;

import java.security.InvalidParameterException;

/**
 * Used in lieu of {@link String#format(String, Object...)},
 * which GWT does not support.
 */
public abstract class Formatter {

	protected abstract String formatInstance(final String format, final Object... args);
	
	public static void setInstance(Formatter instance) {
		if (instance == null) throw new InvalidParameterException("Formatter cannot be null");
		Formatter.instance = instance;
	}
	
	private static Formatter instance = new Formatter() {
		@Override
		protected String formatInstance(String format, Object... args) {
			final StringBuilder msg = new StringBuilder();
			int argIndex = 0;
			for (int i = 0; i < format.length(); i++) {
				if (i < format.length() - 1) {
					if (format.charAt(i) == '%' && i < format.length() - 1 && format.charAt(i + 1) != '%') {
						int j = i + 1;
						for ( ; j < format.length(); j++) {
							char c = format.charAt(j);
							if (Character.isWhitespace(c)) break;
						}
						if (argIndex >= args.length) { 
							throw new InvalidParameterException("Too many arguments for string " + format);
						}
						msg.append(args[argIndex++]);
						i = j - 1;
						continue;
					}
				}
				msg.append(format.charAt(i));
			}
			return msg.toString();
		}
	};
	
	/** Used in lieu of {@link String#format(String, Object...)}, which GWT does not support. */
	public static String format(final String format, final Object... args) {
		return instance.formatInstance(format, args);
	}
}
