/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util.picture;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.ProfilePrivlevel;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.filesystem.FileLogic;

import java.util.List;

/**
 * simple picture handler factory
 *
 * @author dzo
 */
public class StandardPictureHandlerFactory implements PictureHandlerFactory {

	/*
	 * The reason for using the admin Logic is that for
	 * gravatar we need the email-address (or more precisely its hash) of the
	 * requested user. Since the mail address is private, only admins and the
	 * respective owner may see it.
	 *
	 * Using the admin logic here is unfortunate.
	 *
	 * a) when the admin logic is used this should be marked clearly by naming
	 * it admin logic
	 *
	 * b) the use of the admin logic can be avoided by sorting out the problem
	 * at hand in the database module.
	 *
	 * b1) We could make the hash of a users email a public field and always
	 * have it in the user object. Thus we can construct the link by using this
	 * public field * b2) We could store the gravatar link directly into the
	 * user object. Since it includes the hash, the privacy concerns are the
	 * same as for
	 *
	 * b1). However, we only need the hash for those users who use gravatar. *
	 * For both versions we will have to load the users email from the database
	 * (and delete it after we computed its hash in cases where the email should
	 * not be visible).
	 *
	 * This would be best solved by some preprocessor in iBatis that creates the
	 * hash or the link directly after the object has been constructed from the
	 * database rows and before it is handled in any manager (take a look at
	 * typeHandlers in iBatis).
	 */
	private LogicInterface adminLogic;

	private FileLogic fileLogic;

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.picture.PictureHandlerFactory#getPictureHandler(org.bibsonomy.model.User)
	 */
	@Override
	public PictureHandler getPictureHandler(final String requestedUserName, final User loggedinUser) {
		final User requestedUser = this.adminLogic.getUserDetails(requestedUserName);
		if (!this.isVisibleForLoggedInUser(requestedUser, loggedinUser)) {
			// return a server picture handler with a dummy user
			return new ServerPictureHandler(this.adminLogic.getUserDetails(""));
		}

		if (requestedUser.isUseExternalPicture()) {
			// XXX: currently we only support gravatar
			return new GravatarPictureHandler(requestedUser);
		}

		return new ServerPictureHandler(requestedUser);
	}

	@Override
	public boolean hasVisibleProfilePicture(String requestedUserName, User loggedinUser) {
		final User requestedUser = this.adminLogic.getUserDetails(requestedUserName);
		if (!this.isVisibleForLoggedInUser(requestedUser, loggedinUser)) {
			return false;
		}

		if (requestedUser.isUseExternalPicture()) {
			return true;
		}

		return this.fileLogic.hasProfilePicture(requestedUserName);
	}

	/**
	 * Checks if the loginUser may see the profile picture of the requested
	 * user.
	 *
	 * @param requestedUser
	 * @param loginUser
	 * @return true if and only if the user logged in may see the picture of the
	 *         user requested
	 */
	private boolean isVisibleForLoggedInUser(final User requestedUser, final User loginUser) {
		final String requestedUserName = requestedUser.getName();
		final String loginUserName = loginUser.getName();

		/*
		 * login user may always see his/her photo
		 */
		if (present(loginUserName) && loginUserName.equalsIgnoreCase(requestedUserName)) {
			return true;
		}

		/*
		 * check if requested user is spammer
		 * prevents others to see the photo of a spammer
		 */
		if (requestedUser.isSpammer()) {
			return false;
		}

		/*
		 * Check the visibility depending on the profile privacy level.
		 */
		final ProfilePrivlevel visibility = requestedUser.getSettings().getProfilePrivlevel();
		switch (visibility) {
			case PUBLIC:
				return true;
			case FRIENDS:
				// only a logged in user can be friend of somebody else
				if (present(loginUserName)) {
					final List<User> friends = this.adminLogic.getUserRelationship(requestedUserName, UserRelation.OF_FRIEND, null);
					for (final User friend : friends) {
						if (loginUserName.equals(friend.getName())) {
							return true;
						}
					}
				}
				// all else:
				//$FALL-THROUGH$
			case PRIVATE:
				// only the requested user her-/hisself may see her/his profile
				// picture;
				// we already tested above if login equals requested user! (nothing
				// to do)
				//$FALL-THROUGH$
			default:
				return false;
		}
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(final LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	/**
	 * @param fileLogic the fileLogic to set
	 */
	public void setFileLogic(FileLogic fileLogic) {
		this.fileLogic = fileLogic;
	}
}
