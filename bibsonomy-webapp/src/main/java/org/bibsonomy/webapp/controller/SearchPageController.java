package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.webapp.command.ListCommand;
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
	private static final Log log = LogFactory.getLog(SearchPageController.class);
	
	/**
	 * We can restrict search to a group's or a user's posts ... here we list all
	 * supported grouping entities.
	 */
	private static final List<GroupingEntity> SUPPORTED_GROUPING_ENTITIES = Arrays.asList(GroupingEntity.USER, GroupingEntity.GROUP);

	@Override
	public View workOn(final SearchViewCommand command) {
		log.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
		
		// no search given -> error 
		if (!present(command.getRequestedSearch())){
			throw new MalformedURLSchemeException("error.search_page_without_search");	
		}
		
		String search = command.getRequestedSearch();
		GroupingEntity groupingEntity = GroupingEntity.ALL;
		String groupingName = null;

		/*
		 * search only in a user's, group's, etc. posts ...
		 */
		for (final GroupingEntity groupingEnt : SUPPORTED_GROUPING_ENTITIES) {
			final String groupingEntString = groupingEnt.name().toLowerCase();
			
			if (search.matches(".*" + groupingEntString  + ":.*")) {
				groupingEntity = groupingEnt;
				
				// extract name of grouping entity
				int start = search.indexOf(groupingEntString + ":");
				int end  = search.indexOf(" ", start);
				if (end == -1) {end = search.length();} // if group:* is last word
				
				groupingName = search.substring(start + (groupingEntString + ":").length(), end);
				
				// warning if more than one group in search string
				if (search.replaceFirst(groupingEntString + ":[^\\s]*", "").matches(".*" + groupingEntString + ":.*")){
					throw new MalformedURLSchemeException("error.search_more_than_one_" + groupingEntString);
				}
				
				// replace all occurences of "group:*"
				search = search.replaceAll(groupingEntString + ":[^\\s]*", "").trim();
				
				// don't search for other grouping entities
				break;
			}
		}
		
		// // determine which lists to initalize depending on the output format and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			this.setList(command, resourceType, groupingEntity, groupingName, command.getRequestedTagsList(), null, null, null, search, command.getListCommand(resourceType).getEntriesPerPage());

			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final List<?> list = listCommand.getList();

			if (list instanceof ResultList<?>) {
				@SuppressWarnings("unchecked")
				final ResultList<Post<?>> resultList = (ResultList<Post<?>>) list;
				listCommand.setTotalCount(resultList.getTotalCount()); 
				log.debug("SearchPageController: resultList.getTotalCount()=" + resultList.getTotalCount());
			}			
			
			this.postProcessAndSortList(command, resourceType);
		}
		
		// html format - retrieve tags and return HTML view
		if ("html".equals(command.getFormat())) {
			command.setPageTitle("search");
			// fill the tag cloud with all tag assignments of the relevant documents
			this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, Integer.MAX_VALUE, search);
			this.endTiming();
			return Views.SEARCHPAGE;			
		}
		
		this.endTiming();
		
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());		
	}

	@Override
	public SearchViewCommand instantiateCommand() {
		return new SearchViewCommand();
	}

}
