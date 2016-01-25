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
			final Group requestedGroup = this.logic.getGroupDetails(requestedUser);
			/*
			 * Check if the group is present. If it should be a user. If its no
			 * user the we will catch the exception and return an error message
			 * to the user
			 */
			if (present(requestedGroup)) {
				return handleGroupCV(requestedGroup, command);
			}
			
			return handleUserCV(requestedUserWithDetails, command);
		} catch (RuntimeException e) {
			// If the name does not fit to anything a runtime exception is thrown while attempting to get the requestedUser
			throw new MalformedURLSchemeException("Something went wrong! You are most likely looking for a non existant user/group.");
		} catch (Exception e) {
			throw new MalformedURLSchemeException("Something went wrong while working on your request. Please try again.");
		}
	}

	/**
	 * Handles the group cv page request
	 * 
	 * @param reqGroup
	 * @param command
	 * @return The group-cv-page view
	 */
	private View handleGroupCV(final Group requestedGroup, final CvPageViewCommand command) {
		final String groupName = requestedGroup.getName();
		command.setIsGroup(true);

		this.setTags(command, Resource.class, GroupingEntity.GROUP, requestedGroup.getName(), null, command.getRequestedTagsList(), null, 1000, null);
		
		// TODO: Implement date selection on the editing page
		final Wiki wiki = this.logic.getWiki(groupName, null);
		final String wikiText;

		if (present(wiki)) {
			wikiText = wiki.getWikiText();
		} else {
			wikiText = "";
		}
		
		/*
		 * set the group to render
		 */
		this.wikiRenderer.setRequestedGroup(requestedGroup);
		command.setRenderedWikiText(this.wikiRenderer.render(wikiText));
		command.setWikiText(wikiText);

		return Views.WIKICVPAGE;
	}

	/**
	 * Handles the user cv page request
	 * 
	 * @param reqUser
	 * @param command
	 * @return The user-cv-page view
	 */
	private View handleUserCV(final User requestedUser, final CvPageViewCommand command) {
		command.setUser(requestedUser);
		final String userName = requestedUser.getName();
		this.setTags(command, Resource.class, GroupingEntity.USER, userName, null, command.getRequestedTagsList(), null, 1000, null);

		/*
		 * convert the wiki syntax
		 */
		// TODO: Implement date selection on the editing page
		final Wiki wiki = this.logic.getWiki(userName, null);
		final String wikiText;
		
		/*
		 * only show cv wiki if the user is no spammer and classified by at least
		 * one classifier as no spammer
		 * always show own wiki to the loggedin user
		 */
		final boolean isNoSpammer = !requestedUser.isSpammer();
		final boolean isClassified = requestedUser.getToClassify() != null && requestedUser.getToClassify() != 1;
		final boolean ownCVPage = requestedUser.equals(command.getContext().getLoginUser());
		if (present(wiki) && (ownCVPage || isNoSpammer && isClassified)) {
			wikiText = wiki.getWikiText();
		} else {
			wikiText = "";
		}
		
		/*
		 * set the user to render
		 */
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
	public void setWikiRenderer(CVWikiModel wikiRenderer) {
		this.wikiRenderer = wikiRenderer;
	}

}
