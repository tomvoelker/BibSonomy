package webdav.helper;

public class PathHelper {

	public static String getParent(final String path) {
		String parentPath = path.substring(0, path.lastIndexOf('/') + 1);
		if (parentPath.length() > 1) parentPath = parentPath.substring(0, parentPath.length() - 1);
		return parentPath;
	}

	public static String getName(final String path) {
		String name = path.substring(path.lastIndexOf('/'));
		if (name.length() > 1) name = name.substring(1);
		return name;
	}

	public static String buildPath(final String basepath, final String childpath) {
		return basepath + (basepath.endsWith("/") ? "" : ((childpath.startsWith("/") ? "" : "/"))) + childpath;
	}

	/**
	 * Concatenates the two pathes and removes the first directory from <i>basepath</i>.<br>
	 * If the need arises this method could be refactored to remove even more than just the first
	 * directory from <i>basepath</i>.
	 * 
	 * @param basepath Path to the file which should be added to the <i>rootpath</i>
	 * @param rootpath The path which will be the new root of the basepath
	 * @return The concatenated path
	 */
	public static String getCombinedPath(final String basepath, final String rootpath) {
		final int basepathLength = basepath.split("/").length;

		if (basepathLength < 2) {
			throw new UnsupportedOperationException("Ambiguous path not supported (" + basepath + ")");
		} else if (basepathLength == 2) {
			return PathHelper.buildPath(rootpath, PathHelper.getName(basepath));
		} else {
			return PathHelper.buildPath(rootpath, basepath.substring(basepath.substring(1).indexOf('/') + 1));
		}
	}
}