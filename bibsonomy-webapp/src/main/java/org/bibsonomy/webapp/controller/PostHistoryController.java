package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Set;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.resource.ResourcePageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author pba
 * @author Nasim Nabavi
 */
public class PostHistoryController <R extends Resource> extends SingleResourceListControllerWithTags implements MinimalisticController<ResourcePageCommand<R>> {

	
	@Override
	public ResourcePageCommand<R> instantiateCommand() {
		return new ResourcePageCommand<R>();
	}

	@Override
	public View workOn(ResourcePageCommand command) {
		final String format = command.getFormat();
		this.startTiming(format);
		
		/*
		 * This hash has 33 characters and contains at the first position the
		 * type of the hash (see SimHash class).
		 */
		final String longHash = command.getRequestedHash();
		final String requUser = command.getRequestedUser();
		final String resourceClass = command.getResourceClass();
		final GroupingEntity groupingEntity = present(requUser) ? GroupingEntity.USER : GroupingEntity.ALL;
		

		final FilterEntity filter = FilterEntity.POSTS_HISTORY;
		Class<? extends Resource> resourceType = null;
		if (resourceClass.equals("bibtex")) {
			resourceType = BibTex.class;
		} else {
			resourceType = Bookmark.class;
		}
	
		this.setList(command, resourceType, groupingEntity, requUser, null, longHash, null, filter, null, command.getStartDate(), command.getEndDate(), command.getListCommand(resourceType).getEntriesPerPage());
		this.postProcessAndSortList(command, resourceType);

		//redirect to HistoryBM.jspx or HistoryBib.jspx
		if ("html".equals(format)) {
			this.endTiming();	
			if(BibTex.class.equals(resourceType)){
				return Views.HISTORYBIB;
			}
			else{
				return Views.HISTORYBM;
			}
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);
	}

}
