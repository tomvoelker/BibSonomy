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
package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.UserNotFoundException;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.Wiki;
import org.bibsonomy.webapp.command.CvPageViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.bibsonomy.wiki.CVWikiModel;
import org.springframework.beans.factory.annotation.Required;

/**
 * Controller for the cv page:
 * - /cv/user/<USERNAME>
 * - /cv/group/<GROUPNAME>
 *
 * @author Bernd Terbrack
 */
public class WikiCvPageController extends ResourceListController implements MinimalisticController<CvPageViewCommand> {
	private static final Log log = LogFactory.getLog(WikiCvPageController.class);


	private CVWikiModel wikiRenderer;

	@Override
	public View workOn(final CvPageViewCommand command) {
		log.debug("cvPageController accessed.");

		final String requestedUser = command.getRequestedUser();
		final User requestedUserWithDetails = this.logic.getUserDetails(requestedUser);

		// prevent showing cv pages of deleted and not existing users and groups
		if (!present(requestedUserWithDetails.getName())) {
			throw new UserNotFoundException(requestedUser);
		}

		// check if the requested type is a group and then, if the requested
		// user
		// is a group user. if not, throw a 404.
		if (command.getRequestedType().equals("group")) {
			/*
			 * Check if the group is present. If it should be a user. If its no
			 * user the we will catch the exception and return an error message
			 * to the user
			 */
			final Group requestedGroup = this.logic.getGroupDetails(requestedUser, false);
			if (present(requestedGroup)) {
				command.setIsGroup(true);
				return this.handleCV(command, null, requestedGroup);
			}

			return new ExtendedRedirectView("/cv/user/" + requestedUser);
		}

		// requested type was user.

		// if the requestedUser was a group user, we redirect to the group page.
		final Group requestedGroup = this.logic.getGroupDetails(requestedUser, false);
		if (present(requestedGroup)) {
			return new ExtendedRedirectView("/cv/group/" + requestedUser);
		}
		// requested type was a user and the user exists.
		command.setUser(requestedUserWithDetails);
		return this.handleCV(command, requestedUserWithDetails, null);
	}

	/**
	 * Handles the cv page request
	 * @param command
	 * @param requestedUser
	 * @param requestedGroup
	 *
	 * @return The cv-page view
	 */
	private View handleCV(final CvPageViewCommand command, final User requestedUser, final Group requestedGroup) {
		final String entityName;
		final GroupingEntity groupingEntity;
		if (present(requestedGroup)) {
			entityName = requestedGroup.getName();
			groupingEntity = GroupingEntity.GROUP;
		} else {
			entityName = requestedUser.getName();
			groupingEntity = GroupingEntity.USER;
		}

		if (!present(entityName)) {
			throw new ObjectNotFoundException(entityName);
		}

		this.setTags(command, Resource.class, groupingEntity, entityName, null, null, null, null, 1000, null);

		// TODO: Implement date selection on the editing page
		final Wiki wiki = this.logic.getWiki(entityName, null);
		final String wikiText;

		boolean showCV = present(wiki);

		/*
		 * hide cv page of spammers
		 */
		if (present(requestedUser)) {
			final boolean isNoSpammer = !requestedUser.isSpammer();
			final boolean isClassified = requestedUser.getToClassify() != null && requestedUser.getToClassify() != 1;
			final boolean ownCVPage = requestedUser.equals(command.getContext().getLoginUser());
			showCV &= ownCVPage || isNoSpammer && isClassified;
		}
		if (showCV) {
			wikiText = wiki.getWikiText();
		} else {
			wikiText = "";
		}

		/*
		 * set the group/user to render
		 */
		this.wikiRenderer.setRequestedGroup(requestedGroup);
		this.wikiRenderer.setRequestedUser(requestedUser);
		command.setRenderedWikiText(this.wikiRenderer.render(wikiText));
		command.setWikiText(wikiText);

		return Views.WIKICVPAGE;
	}

	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	@Override
	public CvPageViewCommand instantiateCommand() {
		return new CvPageViewCommand();
	}

	/**
	 * @param wikiRenderer
	 *            the wikiRenderer to set
	 */
	@Required
	public void setWikiRenderer(final CVWikiModel wikiRenderer) {
		this.wikiRenderer = wikiRenderer;
	}
}