package org.bibsonomy.webapp.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.systemstags.SystemTags;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;
//import org.bibsonomy.model.enums.Order;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.AuthorResourceCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author daill
 * @version $Id$
 */
public class AuthorPageController extends SingleResourceListControllerWithTags implements MinimalisticController<AuthorResourceCommand>{
	private static final Log log = LogFactory.getLog(AuthorPageController.class);

	public View workOn(AuthorResourceCommand command) {
		log.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());

		// get author query - it might still contain some system tags at this point!
		String authorQuery = command.getRequestedAuthor();

		// if no author given throw error 		
		if (!ValidationUtils.present(authorQuery)) {
			log.error("Invalid query /author without author name");
			throw new MalformedURLSchemeException("error.author_page_without_authorname");
		}
						
		// set grouping entity = ALL
		final GroupingEntity groupingEntity = GroupingEntity.ALL;
		
		/*
		 * FIXME: the query supports only ONE tag!
		 */
		final List<String> requTags = command.getRequestedTagsList();
		boolean tags = false;
		if(requTags.size() > 0){
			tags = true;
		}
		// check for further system tags
		List<String> sysTags = SystemTagsUtil.extractSystemTagsFromString(authorQuery, " ");
		if (sysTags.size() > 0) {
			// remove them from the query
			authorQuery = removeSystemtagsFromQuery(authorQuery, sysTags);
			// add them to the tags list
			requTags.addAll(sysTags);
		}
				
		// add the requested author as a system tag
		requTags.add(SystemTagsUtil.buildSystemTagString(SystemTags.AUTHOR, authorQuery));
		
		// handle case when only tags are requested
		this.handleTagsOnly(command, groupingEntity, null, null, requTags, null, null, 0, 1000, null);
				
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			this.setList(command, resourceType, groupingEntity, null, requTags, null, null, null, null, command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessAndSortList(command, resourceType);
		}		
		
		// html format - retrieve tags and return HTML view
		if ("html".equals(command.getFormat())) {
			this.setTags(command, BibTex.class, groupingEntity, null, null, requTags, null, null, 0, 1000, null);
			this.endTiming();
			if(tags){
				//this.setRelatedTags(command, Resource.class, groupingEntity, authorQuery, null, requTags, Order.ADDED, 0, 20, null);
				return Views.AUTHORTAGPAGE;
			}
			return Views.AUTHORPAGE;			
		}
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());		
	}
	
	public AuthorResourceCommand instantiateCommand() {
		return new AuthorResourceCommand();
	}
	
	
	private String removeSystemtagsFromQuery(String authorQuery, List<String> sysTags) {
		for (String sysTag : sysTags) {
			// remove them from author query string
			authorQuery = authorQuery.replace(sysTag, "");
		}
		return authorQuery;
	}
}
