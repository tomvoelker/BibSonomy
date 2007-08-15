package webdav.helper;

public class IdiomHelper {

	public static <T> T getTernaryExp(final boolean exp, final T left, final T right) {
		if (exp) {
			return left;
		} else {
			return right;
		}
	}
}