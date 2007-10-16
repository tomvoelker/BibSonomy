package org.bibsonomy.webapp.controller;


import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.bibsonomy.webapp.command.ListView;
import org.bibsonomy.webapp.command.RessourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

public class HomepageController implements MinimalisticController<RessourceViewCommand> {
	private static final Logger log = Logger.getLogger(HomepageController.class);
	
	private PostLogicInterface postLogic;
	private UserSettings userSettings;
	
	/*public HomepageController(final PostLogicInterface postLogic, final UserSettings userSettings) {
		this.postLogic = postLogic;
		this.userSettings = userSettings;
	}*/
	
	public RessourceViewCommand instantiateCommand() {
		return new RessourceViewCommand();
	}

	public View workOn(RessourceViewCommand command) {
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
		
		command.getBookmark().setEntriesPerPage(userSettings.getItemsPerPage());
		command.getBookmark().setList( postLogic.getPosts(Bookmark.class, groupingEntity, groupingName, null, null, null, command.getBookmark().getStart(), command.getBookmark().getStart() + userSettings.getItemsPerPage(), null) );
		
		command.getBibtex().setEntriesPerPage(userSettings.getItemsPerPage());
		command.getBibtex().setList( postLogic.getPosts(BibTex.class, groupingEntity, groupingName, null, null, null, command.getBibtex().getStart(), command.getBibtex().getStart() + userSettings.getItemsPerPage(), null) );

		return Views.HOMEPAGE;
	}

	public void setPostLogic(PostLogicInterface postLogic) {
		this.postLogic = postLogic;
	}

	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}

}