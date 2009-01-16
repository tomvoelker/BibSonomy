package org.bibsonomy.layout.jabref;

import java.io.InputStream;

import org.bibsonomy.util.StringUtils;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class JabrefLayoutUtils {

	/**
	 * The file extension of layout filter file names.
	 */
	protected final static String layoutFileExtension = ".layout";

	/** Builds the hash for the custom layout files of the user. Depending on the 
	 * layout part the hash differs.
	 * 
	 * @param user
	 * @param part
	 * @return
	 */
	public static String userLayoutHash (final String user, final LayoutPart part) {
		return StringUtils.getMD5Hash("user." + user + "." + part + layoutFileExtension).toLowerCase();
	}

	/** Loads a resource using the classloader.
	 * 
	 * @param location
	 * @return
	 */
	public static InputStream getResourceAsStream (final String location) {
		final InputStream resourceAsStream = JabrefLayoutRenderer.class.getClassLoader().getResourceAsStream(location);
		if (resourceAsStream != null) 
			return resourceAsStream;
		return JabrefLayoutRenderer.class.getResourceAsStream(location);
	}

	/** Constructs the name of a layout file.
	 * 
	 * @param layout
	 * @param part
	 * @return
	 */
	protected static String getLayoutFileName(final String layout, final String part) {
		return layout + "." + part + layoutFileExtension;
	}

	protected static String getLayoutFileName(final String layout) {
		return layout + layoutFileExtension;
	}
}

