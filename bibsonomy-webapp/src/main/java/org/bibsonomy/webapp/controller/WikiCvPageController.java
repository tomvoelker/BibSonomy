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
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.Wiki;
import org.bibsonomy.webapp.command.CvPageViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
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

		// prevent showing cv pages of deleted and not existiing users
		if (!present(requestedUserWithDetails.getName())) {
			throw new ObjectNotFoundException(requestedUser);
		}

		try {
			final Group requestedGroup = this.logic.getGroupDetails(requestedUser, false);
			/*
			 * Check if the group is present. If it should be a user. If its no
			 * user the we will catch the exception and return an error message
			 * to the user
			 */
			if (present(requestedGroup)) {
				command.setIsGroup(true);
				return this.handleCV(command, null, requestedGroup);
			}

			command.setUser(requestedUserWithDetails);
			return this.handleCV(command, requestedUserWithDetails, null);
		} catch (final RuntimeException e) {
			// If the name does not fit to anything a runtime exception is thrown while attempting to get the requestedUser
			throw new MalformedURLSchemeException("Something went wrong! You are most likely looking for a non existant user/group.");
		} catch (final Exception e) {
			throw new MalformedURLSchemeException("Something went wrong while working on your request. Please try again.");
		}
	}

	/**
	 * Handles the cv page request
	 * @param command
	 * @param requestedUser
	 * @param reqGroup
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