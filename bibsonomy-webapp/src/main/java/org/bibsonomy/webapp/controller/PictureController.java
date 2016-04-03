/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.ProfilePrivlevel;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.actions.PictureCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.picture.PictureHandler;
import org.bibsonomy.webapp.util.picture.PictureHandlerFactory;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * this controller handles picture download
 * 
 * @author wla, cut
 */
public class PictureController implements MinimalisticController<PictureCommand>, ErrorAware, RequestAware {

	static {
		/*
		 * set the headless mode for awt library FIXME does it work? Should we
		 * really do this here? Better in a Tomcat config file, right?!
		 */
		System.setProperty("java.awt.headless", "true");
	}

	/**
	 * This is the default to state whether a Gravatar profile picture shall be
	 * used preferred to any locally uploaded file.
	 */
	protected static boolean PREFER_GRAVATAR_DEFAULT = true;

	private RequestLogic requestLogic;

	/*
	 * FIXME: The injected logic in bibsonomy2-actions-servlet.xml is in Fact
	 * the admin Logic. The reason for using the admin Logic is that for
	 * gravatar we need the email-address (or more precisely its hash) of the
	 * reqeusted user. Since the mail address is private, only admins and the
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
	 * hash or the link directy after the object has been constructed from the
	 * database rows and before it is handled in any manager (take a look at
	 * typeHandlers in iBatis).
	 */
	private LogicInterface logic;

	private Errors errors = null;

	private PictureHandlerFactory pictureHandlerFactory;

	/**
	 * Creates a new {@code PictureController} instance.
	 */
	public PictureController() {
		// nothing to do
	}

	@Override
	public PictureCommand instantiateCommand() {
		return new PictureCommand();
	}

	@Override
	public View workOn(final PictureCommand command) {
		final String method = this.requestLogic.getMethod();

		if ((command.getRequestedUser() != null) && "GET".equals(method)) {
			/*
			 * picture download
			 */
			return this.downloadPicture(command);
		}

		// else:
		return Views.ERROR;
	}

	/**
	 * Returns a view with the requested picture.
	 * 
	 * @param command
	 * @return
	 */
	private View downloadPicture(final PictureCommand command) {
		final String requestedUserName = command.getRequestedUser();
		User requestedUser = this.logic.getUserDetails(requestedUserName);
		// test if user's profile picture is visible
		if (!this.isPictureVisible(requestedUser, command.getLoginUser())) {
			// elsewise handle request like a request for default user
			requestedUser = this.logic.getUserDetails("");
		}
		final PictureHandler handler = this.pictureHandlerFactory.getPictureHandler(requestedUser);
		return handler.getProfilePictureView(requestedUser, command);
	}

	/**
	 * Checks if the loginUser may see the profile picture of the requested
	 * user.
	 * 
	 * @param requestedUser
	 * @param loginUserName
	 * @return true if and only if the user logged in may see the picture of the
	 *         user requested
	 */
	private boolean isPictureVisible(final User requestedUser, final User loginUser) {
		final String requestedUserName = requestedUser.getName();
		final String loginUserName = loginUser.getName();

		/*
		 * login user may always see his/her photo
		 */
		if (present(loginUserName) && loginUserName.equals(requestedUserName)) {
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
				final List<User> friends = this.logic.getUserRelationship(requestedUserName, UserRelation.OF_FRIEND, null);
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

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/**
	 * @param requestLogic
	 *            the requestLogic to set
	 */
	@Override
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * Sets this controller's DBLogic.
	 * 
	 * @param dbl
	 */
	public void setLogic(final LogicInterface dbl) {
		this.logic = dbl;
	}

	/**
	 * Sets this controller's {@link PictureHandlerFactory} instance.
	 * 
	 * @param factory
	 */
	public void setPictureHandlerFactory(final PictureHandlerFactory factory) {
		this.pictureHandlerFactory = factory;
	}
}
