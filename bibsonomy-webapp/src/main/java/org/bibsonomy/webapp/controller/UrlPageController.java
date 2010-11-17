package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.util.StringUtils;
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

	@Override
	public View workOn(final UrlCommand command) {
		log.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());

		final GroupingEntity groupingEntity;
		final String groupingName;

		if (present(command.getRequestedUser())) {
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
		if (!present(command.getRequUrl()) && !present(requHash)) {
			throw new MalformedURLSchemeException("error.url_no_hash");
		}		

		// handle the case when only tags are requested
		command.setResourcetype(Collections.<Class<? extends Resource>>singleton(Bookmark.class));
		this.handleTagsOnly(command, groupingEntity, groupingName, null, null, requHash, 1000, null);
		
		// send redirect to /url/HASH
		if (present(command.getRequUrl())) {
			// TODO: add format in front of /url/? (probably not, this URL should only be called by input form)
			return new ExtendedRedirectView("/url/" + StringUtils.getMD5Hash(command.getRequUrl()));
		}

		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command.getFormat(), command.getResourcetype())) {
			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final int entriesPerPage = listCommand.getEntriesPerPage();

			this.setList(command, resourceType, groupingEntity, groupingName, null, requHash, null, null, null, entriesPerPage);
			this.postProcessAndSortList(command, resourceType);

			this.setTotalCount(command, resourceType, groupingEntity, groupingName, null, requHash, null, null, null, entriesPerPage, null);
		}
		
		/*
		 * build page title
		 */
		command.setPageTitle("url ::");
		if (present(command.getBookmark().getList())) {	
			command.setPageTitle(command.getPageTitle() + command.getBookmark().getList().get(0).getResource().getUrl());
		}

		this.endTiming();

		// html format - retrieve tags and return HTML view
		if ("html".equals(command.getFormat())) {
			// FIXME: here we assume, bookmarks are handled, further above we use listsToInitialize ...
			setTags(command, Bookmark.class, groupingEntity, groupingName, null, null, requHash, 1000, null);
			return Views.URLPAGE;	
		}

		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());				
	}

	@Override
	public UrlCommand instantiateCommand() {
		return new UrlCommand();
	}
}
