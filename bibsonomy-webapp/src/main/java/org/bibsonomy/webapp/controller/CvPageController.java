package org.bibsonomy.webapp.controller;

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.CvPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Philipp Beau
 * @version $Id$
 */
public class CvPageController extends ResourceListController implements MinimalisticController<CvPageCommand> {
	private static final String TAG_MYOWN = "myown";
	private static final Log log = LogFactory.getLog(CvPageController.class);


	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	public View workOn(CvPageCommand command) {
		command.setPageTitle("Curriculum vitae");


		final String requUser = command.getRequestedUser();

		if (!ValidationUtils.present(requUser)) {
			/*
			 * FIXME: a valuable error message would be very helpful!
			 */
			return Views.ERROR;
		}

		command.setUser(this.logic.getUserDetails(requUser));

		final GroupingEntity groupingEntity = GroupingEntity.USER;

		this.setTags(command, Resource.class, groupingEntity, requUser, null, command.getRequestedTagsList(), null, null, 0, Integer.MAX_VALUE, null);

		/*
		 * retrieve and set the requested bibtex(s) / bookmark(s) with the "myown" tag
		 */
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			final int entriesPerPage = command.getListCommand(resourceType).getEntriesPerPage();		
			this.setList(command, resourceType, groupingEntity, requUser, Collections.singletonList(TAG_MYOWN), null, Order.ADDED, null, null, entriesPerPage);
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


