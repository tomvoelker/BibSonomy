package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
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
 * Controller for the cv page: - /cv/name
 * 
 * @author Bernd Terbrack
 * @version $Id$
 */
public class WikiCvPageController extends ResourceListController implements MinimalisticController<CvPageViewCommand> {

	private static final Log log = LogFactory.getLog(WikiCvPageController.class);
	private CVWikiModel wikiRenderer;

	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	@Override
	public View workOn(final CvPageViewCommand command) {
		log.debug("cvPageController accessed.");

		try {
			final String requestedUser = command.getRequestedUser();
			final Group requestedGroup = this.logic.getGroupDetails(requestedUser);
			/* Check if the group is present. If it should be a user. If its no
			   user the we will catch the exception and return an error message
			   to the user. */
			if (present(requestedGroup)) {
				return handleGroupCV(this.logic.getGroupDetails(requestedUser), command);
			} else {
				return handleUserCV(this.logic.getUserDetails(requestedUser), command);
			}
		} catch (RuntimeException e) {
			//If the name does not fit to anything a runtime exception is thrown while attempting to get the requestedUser
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
		// TODO: add todo
		final List<User> groupUsers = this.logic.getUsers(null, GroupingEntity.GROUP, groupName, null, null, null, null, null, 0, 1000);
		requestedGroup.setUsers(groupUsers);

		//this.setTags(command, Resource.class, GroupingEntity.GROUP, requestedGroup.getName(), null, command.getRequestedTagsList(), null, 1000, null);

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
		final Wiki wiki = this.logic.getWiki(userName, null);
		final String wikiText;

		if (present(wiki) && (requestedUser.equals(command.getContext().getLoginUser())
				|| !requestedUser.isSpammer() && requestedUser.getToClassify() != null && requestedUser.getToClassify() != 1)) {
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
