package org.bibsonomy.webapp.controller;


import java.util.Collection;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.bibsonomy.webapp.command.ListView;
import org.bibsonomy.webapp.command.ResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * controller for retrieving multiple windowed lists with resources. These are currently the bookmark an the bibtex list
 * 
 * @author Jens Illig
 */
public class MultiResourceListController implements MinimalisticController<ResourceViewCommand> {
	private static final Logger log = Logger.getLogger(MultiResourceListController.class);
	
	private PostLogicInterface postLogic;
	private UserSettings userSettings;
	private Collection<Class<? extends Resource>> listsToInitialise;
	
	public ResourceViewCommand instantiateCommand() {
		return new ResourceViewCommand();
	}

	public View workOn(ResourceViewCommand command) {
		log.debug(this.getClass().getSimpleName());
		final GroupingEntity groupingEntity;
		final String groupingName;
		if (command.getRequestedUser() != null) {
			groupingEntity = GroupingEntity.USER;
			groupingName = command.getRequestedUser();
		} else {
			groupingEntity = GroupingEntity.ALL;
			groupingName = null;
		}
		
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			setList(command, resourceType, groupingEntity, groupingName, userSettings.getItemsPerPage());
		}

		return Views.HOMEPAGE;
	}
	
	private <T extends Resource> void setList(ResourceViewCommand cmd, Class<T> resourceType, GroupingEntity groupingEntity, String groupingName, int itemsPerPage) {
		ListView<Post<T>> listView = cmd.getListView(resourceType); 
		listView.setEntriesPerPage(itemsPerPage);
		listView.setList( this.postLogic.getPosts(resourceType, groupingEntity, groupingName, null, null, null, listView.getStart(), listView.getStart() + userSettings.getItemsPerPage(), null) );
	}

	/**
	 * @param postLogic some postLogicInterface implementation
	 */
	public void setPostLogic(PostLogicInterface postLogic) {
		this.postLogic = postLogic;
	}

	/**
	 * @param userSettings the loginUsers userSettings
	 */
	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}

	/**
	 * @param listsToInitialise which lists shall be initialised by this controller instance
	 */
	public void setListsToInitialise(final Collection<Class<? extends Resource>> listsToInitialise) {
		this.listsToInitialise = listsToInitialise;
	}
}