package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.Wiki;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.CvPageViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.bibsonomy.wiki.WikiUtil;

/**
 * Controller for the cv page:
 * - /cv/user/USERNAME
 * 
 * 
 * @author Philipp Beau
 * @version $Id$
 */
public class CvPageController extends ResourceListController implements MinimalisticController<CvPageViewCommand> {

	private WikiUtil wikiRenderer;
	
	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	@Override
	public View workOn(final CvPageViewCommand command) {
		command.setPageTitle("Curriculum vitae");
		
		final String requUser = command.getRequestedUser();

		if (!present(requUser)) {
			throw new MalformedURLSchemeException("error.cvpage_without_username");
		}

		command.setUser(this.logic.getUserDetails(requUser));

		final GroupingEntity groupingEntity = GroupingEntity.USER;

		this.setTags(command, Resource.class, groupingEntity, requUser, null, command.getRequestedTagsList(), null, 1000, null);

		/*
		 * retrieve and set the requested publication(s) / bookmark(s) with the "myown" tag
		 */
		for (final Class<? extends Resource> resourceType : this.listsToInitialise) {
			final int entriesPerPage = command.getListCommand(resourceType).getEntriesPerPage();		
			this.setList(command, resourceType, groupingEntity, requUser, Collections.singletonList(SystemTagsUtil.CV_TAG), null, Order.ADDED, null, null, entriesPerPage);
		}
		
		/*
		 * convert the wiki syntax
		 */
		final User user = command.getUser();
		Wiki wiki = this.logic.getWiki(user.getName(), null);
		
		if(!present(wiki))
			wiki = new Wiki();
		
		command.setWikiText(wikiRenderer.render(wiki.getWikiText()));
		
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
	 * @param wikiRenderer the wikiRenderer to set
	 */
	public void setWikiRenderer(WikiUtil wikiRenderer) {
		this.wikiRenderer = wikiRenderer;
	}

	/**
	 * @return the wikiRenderer
	 */
	public WikiUtil getWikiRenderer() {
		return wikiRenderer;
	}	
}
