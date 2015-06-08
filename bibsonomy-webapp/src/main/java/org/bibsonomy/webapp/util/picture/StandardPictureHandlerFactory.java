package org.bibsonomy.webapp.util.picture;

import org.bibsonomy.model.User;

/**
 * simple picture handler factory
 *
 * @author dzo
 */
public class StandardPictureHandlerFactory implements PictureHandlerFactory {

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.picture.PictureHandlerFactory#getPictureHandler(org.bibsonomy.model.User)
	 */
	@Override
	public PictureHandler getPictureHandler(User requestedUser) {
		if (requestedUser.isUseExternalPicture()) {
			// XXX: currently we only support gravatar
			return new GravatarPictureHandler();
		}
		return new ServerPictureHandler();
	}

}
