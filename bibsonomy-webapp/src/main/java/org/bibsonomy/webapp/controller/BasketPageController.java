package org.bibsonomy.webapp.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.BibtexResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for the basket page
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class BasketPageController extends SingleResourceListController implements MinimalisticController<BibtexResourceViewCommand>{

	private static final Log log = LogFactory.getLog(BasketPageController.class);
	
	public View workOn(BibtexResourceViewCommand command) {
		
		log.debug(this.getClass().getSigners());
		this.startTiming(this.getClass(), command.getFormat());
						
		// if user is not logged in, redirect him to login page
		if (command.getContext().isUserLoggedIn() == false) {
			log.info("Trying to access basket without being logged in");
			return new ExtendedRedirectView("/login");
		}				
		
		// set login user name + grouping entity = BASKET
		final String loginUserName = command.getContext().getLoginUser().getName();
		final GroupingEntity groupingEntity = GroupingEntity.BASKET;
		
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {			
			final int entriesPerPage = command.getListCommand(resourceType).getEntriesPerPage();
			
			this.setList(command, resourceType, groupingEntity, loginUserName, null, null, null, null, null, entriesPerPage);
			this.postProcessAndSortList(command, resourceType);
			
			/*
			 * set all posts from basket page to "picked" such that their "pick"
			 * link changes to "unpick"
			 */
			for (final Post<? extends Resource> post : command.getListCommand(resourceType).getList()){
					post.setPicked(true);
			}

			// the number of items in this user's basket has already been fetched
			command.getListCommand(resourceType).setTotalCount(command.getContext().getLoginUser().getBasket().getNumPosts());
		}	
		
				
		if (command.getFormat().equals("html")) {
			// TODO i18n
			command.setPageTitle(" :: basket" );
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
