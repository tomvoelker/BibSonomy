package org.bibsonomy.webapp.controller;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.systemstags.SystemTags;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.SearchViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for search page
 * 
 * @author Beate Krause
 * @version $Id$
 */
public class SearchPageController extends SingleResourceListController implements MinimalisticController<SearchViewCommand>{
	private static final Logger LOGGER = Logger.getLogger(SearchPageController.class);

	public View workOn(SearchViewCommand command) {
		
		LOGGER.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
		
		// no search given -> error 
		if (command.getRequestedSearch().length() == 0){
			LOGGER.error("Invalid query /search without search term");
			throw new MalformedURLSchemeException("error.search_page_without_search");	
		}
		String search = command.getRequestedSearch();
		GroupingEntity groupingEntity = GroupingEntity.ALL;
		String groupingName = null;

		/* DEBUG */
		LOGGER.debug("SearchPageController: command.getSearchmode()="+command.getSearchmode());
		
		if ("lucene".equals(command.getSearchmode())) {
			command.getRequestedTagsList().add(SystemTags.SEARCH.getPrefix() + ":lucene");
		} 

		/* DEBUG */
		LOGGER.debug("SearchPageController: command.getRequestedTagsList().toString()="+command.getRequestedTagsList().toString());
		
		// search in a specific user's entries		
		if (search.matches(".*user:.*")) {
			// get username of first user
			int start = search.indexOf("user:");
			int ende  = search.indexOf(" ", start);
			if (ende == -1) {ende = search.length();} // if user:* is last word
			groupingName = search.substring(start + "user:".length(), ende);
			
			//warning if more than one user in search string
			if (search.replaceFirst("user:[^\\s]*", "").matches(".*user:.*")){
				throw new MalformedURLSchemeException("error.search_more_than_one_user");
			}
			// replace all occurences of "user:*"
			search = search.replaceAll("user:[^\\s]*", "").trim();
		}
		
		// // determine which lists to initalize depending on the output format and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			this.setList(command, resourceType, groupingEntity, groupingName, command.getRequestedTagsList(), null, null, null, search, command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessAndSortList(command, resourceType);
			
		}
		
		// html format - retrieve tags and return HTML view
		if ("html".equals(command.getFormat())) {
			command.setPageTitle("search");
			return Views.SEARCHPAGE;			
		}
		
		this.endTiming();
		
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());		
	}

	public SearchViewCommand instantiateCommand() {
		return new SearchViewCommand();
	}

}
