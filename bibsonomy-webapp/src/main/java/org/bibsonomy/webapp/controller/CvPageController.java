package org.bibsonomy.webapp.controller;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.CvPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Philipp Beau
 * @version $Id$
 */
public class CvPageController extends ResourceListController implements MinimalisticController<CvPageCommand> {
	private static final Log log = LogFactory.getLog(CvPageController.class);


	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	public View workOn(CvPageCommand command) {
		command.setPageTitle("Curriculum vitae");
		

		final String requUser = command.getRequestedUser();
		User requUserDetail;

		if(!(requUser == null)) {
		requUserDetail = this.logic.getUserDetails(requUser);
		command.setUser(requUserDetail);
		} else {
			return Views.ERROR;
		}
		
		final GroupingEntity groupingEntity = GroupingEntity.USER;
		
		
		this.setTags(command,Resource.class, GroupingEntity.USER, requUser, null, command.getRequestedTagsList(), null, null, 0, Integer.MAX_VALUE, null);
					
		/*
		 * retrieve and set the requested bibtex(s) / bookmark(s) with the "myown" tag
		 */
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			ArrayList<String> myOwnTag = new ArrayList<String>();
			myOwnTag.add("myown");
			final int entriesPerPage = command.getListCommand(resourceType).getEntriesPerPage();		
			this.setList(command, resourceType, groupingEntity, requUser, myOwnTag, null, Order.ADDED, null, null, entriesPerPage);

		}
		return Views.CVPAGE;
	}

	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	public CvPageCommand instantiateCommand() {
		return new CvPageCommand();
	}	
}

	
