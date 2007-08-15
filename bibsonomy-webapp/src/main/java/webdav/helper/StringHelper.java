package webdav.helper;

public class StringHelper {

	static boolean getPositiveCount(final String str) {
		try {
			if (Integer.parseInt(str) > 0) return true;
		} catch (final NumberFormatException ex) {}
		return false;
	}

	public static boolean getBool(final String str) {
		if (StringHelper.getPositiveCount(str) || "true".equals(str.toLowerCase())) {
			return true;
		}
		return false;
	}
}