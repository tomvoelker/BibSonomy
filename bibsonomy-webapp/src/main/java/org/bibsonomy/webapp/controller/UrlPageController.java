package org.bibsonomy.webapp.controller;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.UrlCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
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
 *          --> should work as expected, maps to requestUrl
 * 		<li>^/+url/+([0-9a-fA-F]+)(\?(.*))?$
 *          --> should work as expected, maps to requestUrlHash
 * </ul>
 *
 * @author Flori
 * @version $Id$
 */
public class UrlPageController extends MultiResourceListController implements MinimalisticController<UrlCommand> {
	private static final Logger LOGGER = Logger.getLogger(UrlPageController.class);

	public View workOn(UrlCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
		
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());		
		
		// no URL hash given -> error
		if (!ValidationUtils.present(command.getRequUrl()) && !ValidationUtils.present(command.getRequUrlHash())) {
			LOGGER.error("Invalid query /url without URL hash");
			throw new MalformedURLSchemeException("error.url_no_hash");
		}
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			// disable manual setting of start value for homepage
			command.getListCommand(resourceType).setStart(0);
			
			if (ValidationUtils.present(command.getRequUrl())) {
				command.setRequUrlHash(resources.Resource.hash(command.getRequUrl()));
			}
			
			setList(command, resourceType, GroupingEntity.ALL, null, null, command.getRequUrlHash(), null, null, null, 20);
			setTags(command, resourceType, GroupingEntity.ALL, null, null, null, command.getRequUrlHash(), null, 0, 20, null);
			postProcessAndSortList(command, resourceType);
		}

		if (ValidationUtils.present(command.getBookmark().getList())) {	
			command.setPageTitle("url :: " + command.getBookmark().getList().get(0).getResource().getUrl() );
		}
		else {
			command.setPageTitle("url ::");
		}
		
		// html format - retrieve tags and return HTML view
		if (command.getFormat().equals("html")) {
			this.endTiming();
			return Views.URLPAGE;	
		}
		
		this.endTiming();
		
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());				
	}

	public UrlCommand instantiateCommand() {
		return new UrlCommand();
	}
}
