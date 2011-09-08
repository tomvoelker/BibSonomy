package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.systemstags.markup.MyOwnSystemTag;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.Wiki;
import org.bibsonomy.model.enums.Order;
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
public class CvPageController extends ResourceListController implements MinimalisticController<CvPageViewCommand> {

	private static final Log log = LogFactory.getLog(CvPageController.class);
	private CVWikiModel wikiRenderer;

	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	@Override
	public View workOn(final CvPageViewCommand command) {
		log.debug("cvPageController accessed.");

		try {
			final Group requestedGroup = this.logic.getGroupDetails(command.getRequestedUser());
			/* Check if the group is present. If it should be a user. If its no
			   user the we will catch the exception and return an error message
			   to the user. */
			return present(requestedGroup) ? handleGroupCV(this.logic.getGroupDetails(command.getRequestedUser()), command) : handleUserCV(this.logic.getUserDetails(command.getRequestedUser()), command);
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
	private View handleGroupCV(Group requestedGroup, CvPageViewCommand command) {
		final String groupName = requestedGroup.getName();
		final GroupingEntity groupingEntity = GroupingEntity.GROUP;

		// TODO: add todo
		final List<User> groupUsers = this.logic.getUsers(null, groupingEntity, groupName, null, null, null, null, null, 0, 1000);
		requestedGroup.setUsers(groupUsers);

		this.setTags(command, Resource.class, groupingEntity, requestedGroup.getName(), null, command.getRequestedTagsList(), null, 1000, null);

		Wiki wiki = this.logic.getWiki(groupName, null);

		if (!present(wiki)) {
			wiki = new Wiki();
		}

		/*
		 * set the group to render
		 */
		this.wikiRenderer.setRequestedGroup(requestedGroup);
		command.setRenderedWikiText(this.wikiRenderer.render(wiki.getWikiText()));
		command.setWikiText(wiki.getWikiText());

		return Views.CVPAGE;
	}

	/**
	 * Handles the user cv page request
	 * 
	 * @param reqUser
	 * @param command
	 * @return The user-cv-page view
	 */
	private View handleUserCV(User requestedUser, CvPageViewCommand command) {
		command.setUser(requestedUser);
		final String userName = requestedUser.getName();
		final GroupingEntity groupingEntity = GroupingEntity.USER;

		this.setTags(command, Resource.class, groupingEntity, userName, null, command.getRequestedTagsList(), null, 1000, null);

		/*
		 * TODO: remove (lists are loaded by wiki tags) retrieve and set the
		 * requested publication(s) / bookmark(s) with the "myown" tag
		 */
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command.getFormat(), command.getResourcetype())) {
			final int entriesPerPage = command.getListCommand(resourceType).getEntriesPerPage();
			this.setList(command, resourceType, groupingEntity, userName, Collections.singletonList(MyOwnSystemTag.NAME), null, Order.ADDED, null, null, entriesPerPage);
		}

		/*
		 * convert the wiki syntax
		 */
		Wiki wiki = this.logic.getWiki(requestedUser.getName(), null);

		if (!present(wiki)) {
			wiki = new Wiki();
		}

		/*
		 * set the user to render
		 */
		this.wikiRenderer.setRequestedUser(requestedUser);
		command.setRenderedWikiText(this.wikiRenderer.render(wiki.getWikiText()));
		command.setWikiText(wiki.getWikiText());

		return Views.CVPAGE;

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
