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
package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.actions.DeletePostCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

/**
 * @author Christian Kramer
 */
public class DeletePostController implements MinimalisticController<DeletePostCommand>, ErrorAware{
	private static final Log log = LogFactory.getLog(DeletePostController.class);

	private RequestLogic requestLogic;
	private LogicInterface logic;
	private Errors errors;
	private URLGenerator urlGenerator;

	@Override
	public DeletePostCommand instantiateCommand() {
		return new DeletePostCommand();
	}

	@Override
	public View workOn(final DeletePostCommand command) {
		final RequestWrapperContext context = command.getContext();

		/*
		 * user has to be logged in to delete himself
		 */
		if (!context.isUserLoggedIn()){
			this.errors.reject("error.general.login");
		}

		/*
		 * check the ckey
		 */
		final String resourceHash = command.getResourceHash();
		final String owner = command.getOwner();

		if (!this.errors.hasErrors() && !this.canDeletePost(context.getLoginUser(), owner)) {
			this.errors.reject("error.general.edit");
		}

		if (context.isValidCkey() && !this.errors.hasErrors()) {
			log.debug("User is logged in, ckey is valid");

			try {
				// delete the post
				this.logic.deletePosts(owner, Collections.singletonList(resourceHash));
			} catch (final IllegalStateException e) {
				this.errors.reject("error.post.notfound", new Object[]{resourceHash}, " The resource with ID [" + resourceHash + "] does not exist and could hence not be deleted.");
			}
		} else {
			this.errors.reject("error.field.valid.ckey");
		}

		/*
		 * if there are errors, show them
		 */
		if (this.errors.hasErrors()){
			return Views.ERROR;
		}

		/*
		 * redirect to the user page when the user is coming from the page of
		 * the resource.
		 */
		final String referer = this.requestLogic.getReferer();
		if (this.urlGenerator.matchesResourcePage(referer, owner, resourceHash)) {
			return new ExtendedRedirectView(this.urlGenerator.getUserUrlByUserName(owner));
		}

		/*
		 * go back where we've come from
		 */
		return new ExtendedRedirectView(referer);
	}

	/**
	 * @param logic
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @return errors
	 */
	@Override
	public Errors getErrors() {
		return this.errors;
	}

	/**
	 * @param errors
	 */
	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/**
	 * @param requestLogic
	 */
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * @param urlGenerator
	 */
	@Required
	public void setUrlGenerator(final URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	private boolean canDeletePost(final User loginUser, final String postOwner) {
		// community post
		if (!present(postOwner)) {
			return Role.ADMIN.equals(loginUser.getRole());
		}
		
		// if the loginUser is the postOwner
		if (loginUser.getName().equals(postOwner)) {
			return true;
		}

		// is the postOwner a group user?
		final Group group = this.logic.getGroupDetails(postOwner, false);
		if (group == null) {
			return false;
		}

		// is loginUser a member of this group?
		final GroupMembership membership = group.getGroupMembershipForUser(loginUser.getName());
		if (membership == null) {
			return false;
		}

		// does loginUser occupy a sufficiently high role for this operation?
		return membership.getGroupRole().hasRole(GroupRole.MODERATOR);
	}
}