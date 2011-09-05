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
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.bibsonomy.wiki.CVWikiModel;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

/**
 * Controller for the cv page: - /cv/user/USERNAME
 * 
 * @author Philipp Beau
 * @author Bernd Terbrack
 * @version $Id$
 */
public class CvPageController extends ResourceListController implements ErrorAware, MinimalisticController<CvPageViewCommand> {

	private static final Log log = LogFactory.getLog(CvPageController.class);
	private CVWikiModel wikiRenderer;
	private Errors errors;

	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	@Override
	public View workOn(final CvPageViewCommand command) {
		final String reqUser = command.getRequestedUser();
		final String reqGroup = command.getRequestedGroup();

		/*
		 * Since we want both (user/group) CV requests being handled within one controller, the controller is branched at this point
		 */
		if (present(reqUser)) {
			return handleUserCV(reqUser, command);
		} else if (present(reqGroup)) {
			return handleGroupCV(reqGroup, command);
		} else {
			return handleError("error.cv.missing_user_group");
		}
	}

	/**
	 * Handles the group cv page request
	 * 
	 * @param reqGroup
	 * @param command
	 * @return The group-cv-page view
	 */
	private View handleGroupCV(String reqGroup, CvPageViewCommand command) {
		final Group requestedGroup = this.logic.getGroupDetails(reqGroup);
		final GroupingEntity groupingEntity = GroupingEntity.GROUP;
		
		// TODO: add todo
		final List<User> groupUsers = this.logic.getUsers(null, groupingEntity, requestedGroup.getName(), null, null, null, null, null, 0, 1000);
		requestedGroup.setUsers(groupUsers);
		
		
		
		this.setTags(command, Resource.class, groupingEntity, reqGroup, null, command.getRequestedTagsList(), null, 1000, null);
		
		Wiki wiki = this.logic.getWiki(reqGroup, null);

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
	private View handleUserCV(String reqUser, CvPageViewCommand command) {
		final User requestedUser = this.logic.getUserDetails(reqUser);
		command.setUser(requestedUser);

		final GroupingEntity groupingEntity = GroupingEntity.USER;

		this.setTags(command, Resource.class, groupingEntity, reqUser, null, command.getRequestedTagsList(), null, 1000, null);

		/*
		 * TODO: remove (lists are loaded by wiki tags) retrieve and set the
		 * requested publication(s) / bookmark(s) with the "myown" tag
		 */
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command.getFormat(), command.getResourcetype())) {
			final int entriesPerPage = command.getListCommand(resourceType).getEntriesPerPage();
			this.setList(command, resourceType, groupingEntity, reqUser, Collections.singletonList(MyOwnSystemTag.NAME), null, Order.ADDED, null, null, entriesPerPage);
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
	 * Method to handle Errors based on urlError enum.
	 * 
	 * @return Error View
	 */
	private View handleError(final String messageKey) {
		log.debug("An error occured: " + messageKey);
		errors.reject(messageKey);
		return Views.ERROR;
	}

	/**
	 * @param wikiRenderer
	 *            the wikiRenderer to set
	 */
	@Required
	public void setWikiRenderer(CVWikiModel wikiRenderer) {
		this.wikiRenderer = wikiRenderer;
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

}
