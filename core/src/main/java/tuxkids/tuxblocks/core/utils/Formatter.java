package tuxkids.tuxblocks.core.utils;

import playn.core.PlayN;

public class Formatter {

	public static String format(final String format, final Object... args) {
		final StringBuffer msg = new StringBuffer();
		int argIndex = 0;
		for (int i = 0; i < format.length(); i++) {
			if (i < format.length() - 1) {
				String sub = format.substring(i, i + 2);
				if (PlayN.regularExpression().matches("%[a-z]", sub)) {
					msg.append(args[argIndex++]);
					i++;
					continue;
				}
			}
			msg.append(format.charAt(i));
		}
		return msg.toString();
	}
}
