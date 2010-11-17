package org.bibsonomy.webapp.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.search.AuthorSystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.AuthorResourceCommand;
import org.bibsonomy.webapp.command.ListCommand;
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

	@Override
	public View workOn(AuthorResourceCommand command) {
		log.debug(this.getClass().getSimpleName());
		final String format = command.getFormat();
		this.startTiming(this.getClass(), format);

		// get author query - it might still contain some system tags at this point!
		String authorQuery = command.getRequestedAuthor();

		// if no author given throw error 		
		if (!ValidationUtils.present(authorQuery)) {
			throw new MalformedURLSchemeException("error.author_page_without_authorname");
		}
						
		// set grouping entity = ALL
		final GroupingEntity groupingEntity = GroupingEntity.ALL;
		
		/*
		 * FIXME: the query supports only ONE tag!
		 */
		final List<String> requTags = command.getRequestedTagsList();
		/*
		 * remember if tags were given by user - if so, forward to special page
		 * (this also checks of only systemtags are contained) 
		 */		
		final boolean hasTags = (SystemTagsUtil.countNonSystemTags(requTags) > 0);	
		
		// check for further system tags
		// FIXME: how may this happen? http://www.bibsonomy.org/author<tag>/tag
		final List<String> sysTags = SystemTagsUtil.extractSearchSystemTagsFromString(authorQuery, " ");
		if (sysTags.size() > 0) {
			// remove them from the query
			authorQuery = removeSystemtagsFromQuery(authorQuery, sysTags);
			// add them to the tags list
			requTags.addAll(sysTags);
		}
		sysTags.addAll(SystemTagsUtil.extractSystemTags(requTags));
				
		// add the requested author as a system tag
		String sysAuthor = SystemTagsUtil.buildSystemTagString(AuthorSystemTag.NAME, authorQuery);
		requTags.add(sysAuthor);
		sysTags.add(sysAuthor);
		
		// handle case when only tags are requested
		this.handleTagsOnly(command, groupingEntity, null, null, requTags, null, 1000, null);
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(format, command.getResourcetype())) {
			this.setList(command, resourceType, groupingEntity, null, requTags, null, null, null, null, command.getListCommand(resourceType).getEntriesPerPage());

			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final List<?> list = listCommand.getList();

			if (list instanceof ResultList<?>) {
				@SuppressWarnings("unchecked")
				final ResultList<Post<?>> resultList = (ResultList<Post<?>>) list;
				listCommand.setTotalCount(resultList.getTotalCount()); 
				log.debug("AuthorPageController: resultList.getTotalCount()=" + resultList.getTotalCount());
			}			
			
			this.postProcessAndSortList(command, resourceType);
		}		
		
		// html format - retrieve tags and return HTML view
		if ("html".equals(format)) {
			this.setTags(command, BibTex.class, groupingEntity, null, null, sysTags, null, 1000, null);
			this.endTiming();
			if(hasTags){
				this.setRelatedTags(command, BibTex.class, groupingEntity, null, null, requTags, Order.ADDED, 0, 20, null);
				return Views.AUTHORTAGPAGE;
			}
			return Views.AUTHORPAGE;			
		}
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);		
	}
	
	@Override
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
