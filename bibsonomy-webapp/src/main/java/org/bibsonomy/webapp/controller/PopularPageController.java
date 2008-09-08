package org.bibsonomy.webapp.controller;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.PopularResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author mwa
 * @version $Id$
 */
public class PopularPageController extends MultiResourceListController implements MinimalisticController<PopularResourceViewCommand>{
	private static final Logger LOGGER = Logger.getLogger(PopularPageController.class);
	
	public View workOn(PopularResourceViewCommand command) {
		
		LOGGER.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
				
		//set the groupingentity and the order
		final GroupingEntity groupingEntity = GroupingEntity.ALL;
		final Order order = Order.POPULAR;
		
		/*
		 * the next popular days in the database
		 * TODO: This should not be fixed! The loop to gather the days should 
		 * be programmed in a way, that it automatically stops when all available
		 * days are gathered. 
		 */
		final int steps = 3; 
		
		final ArrayList<String> tags = new ArrayList<String>();
		
		/*
		 * only show 5 entries for each list
		 * TODO: never put constants like this ("5") as literals into the code!
		 * Use configurable (via bibsonomy2-servlets.xml) attributes instead!
		 */
		command.getListCommand(BibTex.class).setEntriesPerPage(5);
		command.getListCommand(Bookmark.class).setEntriesPerPage(5);
		
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		
		for(int step = 0; step < steps; step++){
			// retrieve and set the requested resource lists
			for (final Class<? extends Resource> resourceType : listsToInitialise) {
				tags.add(0, "sys:days:" + step);
				this.setList(command, resourceType, groupingEntity, null, tags, null, order, null, null, command.getListCommand(resourceType).getEntriesPerPage());
				this.postProcessAndSortList(command, resourceType);
				/*
				 * determine the value of popular days, e.g. the last 10 days
				 * TODO: wouldn't it make more sense, to first get the days and then use 
				 * them to retrieve the posts?
				 */
				int days = this.logic.getPostStatistics(resourceType, groupingEntity, null, tags, null, order, null, command.getListCommand(resourceType).getStart(), command.getListCommand(resourceType).getStart()+command.getListCommand(resourceType).getEntriesPerPage(), null, null);
				
				if(resourceType == BibTex.class){
					command.getPopularListsBibTex().add(command.getBibtex());
					command.getPopularBibtexDays().add(days);
				}
				
				if(resourceType == Bookmark.class){
					command.getPopularListsBookmark().add(command.getBookmark());
					command.getPopularBookmarkDays().add(days);
				}
			}
		}	
	
		// only html format, exports are not possible atm 
		this.setTags(command, Resource.class, groupingEntity, null, null, null, null, order, 0, 1000, null);
		this.endTiming();
		return Views.POPULAR;			
		
	}
	
	public PopularResourceViewCommand instantiateCommand() {
		return new PopularResourceViewCommand();
	}

	

}
