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
	public View workOn(ResourcePageCommand command) {
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
//		Class<? extends Resource> resourceClass = null;
		final Set<Class<? extends Resource>> resourceTypes = this.getListsToInitialize(format, command.getResourcetype());
		Class<? extends Resource> resourceType = null;
		if (resourceTypes.contains(BibTex.class)) {
			resourceType = BibTex.class;
		} else {
			resourceType = Bookmark.class;
		}
		//final Set<Class<? extends Resource>> resourceTypes = command.getResourcetype();
	//	for (final Class<? extends Resource> resourceType : this.getListsToInitialize(format, command.getResourcetype())) {
			
	//	final Set<Class<? extends Resource>> resourceTypes = this.getListsToInitialize(format, command.getResourcetype());
		//	final ListCommand<?> listCommand = command.getListCommand(resourceType);
		this.setList(command, resourceType, groupingEntity, requUser, null, longHash, null, filter, null, command.getStartDate(), command.getEndDate(), command.getListCommand(resourceType).getEntriesPerPage());
		this.postProcessAndSortList(command, resourceType);
	//		resourceClass = resourceType;
		//	this.setList(command, resourceClass, groupingEntity,requUser, null, longHash, null, filter, null, command.getStartDate(), command.getEndDate(), entriesPerPage);
	//	}
		
		//int size = resourceTypes.size();
		
		//resourceClass = BibTex.class;
		//final ListCommand<?> listCommand = command.getListCommand(resourceClass);
		//final int entriesPerPage = listCommand.getEntriesPerPage();
		
	//	this.setList(command, resourceClass, groupingEntity,requUser, null, longHash, null, filter, null, command.getStartDate(), command.getEndDate(), entriesPerPage);
		//this.postProcessAndSortList(command, resourceClass);
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
