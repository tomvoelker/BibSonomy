package org.bibsonomy.util;


/**
 * TODO: tests
 * 
 * @author rja
 * @author dzo
 * 
 * @version $Id$
 */
public final class JSONUtils {

	private JSONUtils() {}

	/**
	 * Quotes a String such that it is usable for JSON.
	 * 
	 * @param value
	 * @return The quoted String.
	 */
	public static String quoteJSON(final String value) {
		if (value == null) {
			return null;
		}
		final StringBuffer sb = new StringBuffer();
		escapeJSON(value, sb);
		return sb.toString();
	}

	/**
	 * Taken from http://code.google.com/p/json-simple/
	 * 
	 * @param s - Must not be null.
	 * @param sb
	 */
	private static void escapeJSON(final String s, final StringBuffer sb) {
		for (int i = 0; i < s.length(); i++) {
			final char ch=s.charAt(i);
			switch(ch){
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			default:
				// Reference: http://www.unicode.org/versions/Unicode5.1.0/
				if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
					final String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				}
				else{
					sb.append(ch);
				}
			}
		}
	}
}
