package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.resource.ResourcePageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author pba
 */
public class PostHistoryController extends SingleResourceListControllerWithTags implements MinimalisticController<ResourcePageCommand<BibTex>> {

	
	@Override
	public ResourcePageCommand<BibTex> instantiateCommand() {
		return new ResourcePageCommand<BibTex>();
	}

	@Override
	public View workOn(ResourcePageCommand<BibTex> command) {
		final String format = command.getFormat();
		this.startTiming(format);
		
		/*
		 * This hash has 33 characters and contains at the first position the
		 * type of the hash (see SimHash class).
		 */
		final String longHash = command.getRequestedHash();
		final String requUser = command.getRequestedUser();
		final GroupingEntity groupingEntity = present(requUser) ? GroupingEntity.USER : GroupingEntity.ALL;
		

		final FilterEntity filter = FilterEntity.POSTS_HISTORY;
		Class<? extends Resource> resourceType = BibTex.class;

			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final int entriesPerPage = listCommand.getEntriesPerPage();
			
			this.setList(command, resourceType, groupingEntity,requUser, null, longHash, null, filter, null, command.getStartDate(), command.getEndDate(), entriesPerPage);
			this.postProcessAndSortList(command, resourceType);

		if ("html".equals(format)) {
			this.endTiming();	
			return Views.POSTHISTORYPAGE;
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);
	}

}
