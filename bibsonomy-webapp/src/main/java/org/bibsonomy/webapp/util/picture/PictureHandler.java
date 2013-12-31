package org.bibsonomy.webapp.util.picture;

import org.bibsonomy.webapp.util.View;

/**
 * Interface {@code PictureHandler} handles request for user's profile picture.</br>
 * Picture source may be either local filesystem, an external service, or whatever. 
 * 
 * @author cut
 * @version $Id:$
 */
public interface PictureHandler {

	/**
	 * Returns a view containing requested user's profile picture.<br/>
	 * 
	 * @param user - requested user
	 * @param command - the actual picture command
	 * @return view containing profile picture
	 */
	public View getProfilePictureView ();

}