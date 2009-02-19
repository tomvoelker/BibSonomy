package org.bibsonomy.webapp.controller;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.BibtexResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author mwa
 * @version $Id$
 */
public class BasketPageController extends SingleResourceListController implements MinimalisticController<BibtexResourceViewCommand>{

	private static final Logger LOGGER = Logger.getLogger(BasketPageController.class);
	
	public View workOn(BibtexResourceViewCommand command) {
		
		LOGGER.debug(this.getClass().getSigners());
		this.startTiming(this.getClass(), command.getFormat());
		

		final String hash     = "1f72e35e5615a7da4c61de4d830305c38";
		final String requUser = command.getRequestedUser();
		final GroupingEntity groupingEntity = (requUser != null ? GroupingEntity.USER : GroupingEntity.ALL);

		
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {			
			final int entriesPerPage = command.getListCommand(resourceType).getEntriesPerPage();
			
			this.setList(command, resourceType, groupingEntity, requUser, null, hash, null, null, null, entriesPerPage);
			this.postProcessAndSortList(command, resourceType);

			this.setTotalCount(command, resourceType, groupingEntity, requUser, null, hash, null, null, null, command.getListCommand(resourceType).getEntriesPerPage(), null);
		}	
		
		//get the title of the publication with the requested hash (intrahash)
		if(command.getBibtex().getList().size() > 0){
			command.setTitle(command.getBibtex().getList().get(0).getResource().getTitle());
		}
		
		if (command.getFormat().equals("html")) {
			this.endTiming();
			
			return Views.BASKETPAGE;
	
		}
		this.endTiming();
		
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());
		
	}
	
	public BibtexResourceViewCommand instantiateCommand() {
		return new BibtexResourceViewCommand();
	}

}
