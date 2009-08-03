package org.bibsonomy.webapp.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.ResourceType;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.UrlCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for Urls 
 * 
 * <pre>
 * matches for urls like:
 * <ul>
 * 		<li>^/+url/*$ 
 *          --> error, need at least the url hash
 * 		<li>^/+url\?requestUrl=(.*)$
 *          --> send redirect
 * 		<li>^/+url/+([0-9a-fA-F]+)(\?(.*))?$
 *          --> should work as expected, maps to requestUrlHash
 * </ul>
 *
 * @author Flori
 * @version $Id$
 */
public class UrlPageController extends SingleResourceListController implements MinimalisticController<UrlCommand> {
	private static final Log log = LogFactory.getLog(UrlPageController.class);

	public View workOn(UrlCommand command) {
		log.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
		
		final GroupingEntity groupingEntity;
		final String groupingName;
		
		if(command.getRequestedUser() != null && !command.getRequestedUser().equals("")){
			/*
			 * handle /url/HASH/USER
			 */
			groupingEntity = GroupingEntity.USER;
			groupingName = command.getRequestedUser();
		} else {
			/*
			 * handle /url/HASH
			 */
			groupingEntity = GroupingEntity.ALL;
			groupingName = null;
		}
		
		// no URL hash given -> error
		final String requHash = command.getRequUrlHash();
		if (!ValidationUtils.present(command.getRequUrl()) && !ValidationUtils.present(requHash)) {
			log.error("Invalid query /url without URL hash");
			throw new MalformedURLSchemeException("error.url_no_hash");
		}		
		
		// handle the case when only tags are requested
		command.setResourcetype(ResourceType.BOOKMARK.getLabel());
		this.handleTagsOnly(command, groupingEntity, groupingName, null, null, requHash, null, 0, 1000, null);
		
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());		
		
		// send redirect to /url/HASH
		if (ValidationUtils.present(command.getRequUrl())) {
			// TODO: add format in front of /url/? (probably not, this URL should only be called by input form)
			return new ExtendedRedirectView("/url/" + resources.Resource.hash(command.getRequUrl()));
		}

		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final int entriesPerPage = listCommand.getEntriesPerPage();
			
			this.setList(command, resourceType, groupingEntity, groupingName, null, requHash, null, null, null, entriesPerPage);
			this.postProcessAndSortList(command, resourceType);
			
			this.setTotalCount(command, resourceType, groupingEntity, groupingName, null, requHash, null, null, null, entriesPerPage, null);
		}

		if (ValidationUtils.present(command.getBookmark().getList())) {	
			command.setPageTitle("url :: " + command.getBookmark().getList().get(0).getResource().getUrl() );
		} else {
			command.setPageTitle("url ::");
		}

		this.endTiming();
		
		// html format - retrieve tags and return HTML view
		if (command.getFormat().equals("html")) {
			// FIXME: here we assume, bookmarks are handled, further above we use listsToInitialize ...
			setTags(command, Bookmark.class, groupingEntity, groupingName, null, null, requHash, null, 0, 1000, null);

			return Views.URLPAGE;	
		}
		
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());				
	}

	public UrlCommand instantiateCommand() {
		return new UrlCommand();
	}
}
