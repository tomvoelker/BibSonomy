package org.bibsonomy.layout.csl;

import org.bibsonomy.services.filesystem.CSLFileLogic;
import org.bibsonomy.services.renderer.LayoutRenderer;
import org.bibsonomy.util.StringUtils;

/**
 * TODO: add documentation to this class
 *
 * @author jan
 */
public class CslLayoutUtils {
	
	/** Builds the hash for the custom layout files of the user.
	 * 
	 * @param user
	 * @return
	 */
	public static String userLayoutHash (final String user) {
		return StringUtils.getMD5Hash("user." + user.toLowerCase() + "." + CSLFileLogic.LAYOUT_FILE_EXTENSION).toLowerCase();
	}
	
	/**
	 * Builds the name of a custom user layout, for the map and elsewhere. Typically "custom_" + userName.
	 * 
	 * @param userName
	 * @return the name of a custom layout
	 */
	public static String userLayoutName (final String userName) {
		return LayoutRenderer.CUSTOM_LAYOUT + "_" + userName;
	}
}
