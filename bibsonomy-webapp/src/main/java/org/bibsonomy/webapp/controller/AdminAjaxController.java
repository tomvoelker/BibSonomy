package org.bibsonomy.webapp.controller;

import java.util.List;

import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.AdminAjaxCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for ajax requests on admin pages
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class AdminAjaxController extends AjaxController implements MinimalisticController<AdminAjaxCommand> {

	public View workOn(AdminAjaxCommand command) {
		final String action = command.getAction();
		
		if ("flag_spammer".equals(action)) {
			this.flagSpammer(command, true);
			this.setResponse(command, command.getUserName() + " flagged as spammer");
		} else if ("unflag_spammer".equals(action)) {
			this.flagSpammer(command, false);
			this.setResponse(command, command.getUserName() + " flagged as nonspammer");
		} else if ("update_settings".equals(action)) {
			this.updateSettings(command);
			this.setResponse(command, command.getKey() + " updated");
		} else if ("latest_posts".equals(action)) {
			this.setLatestPosts(command);
			return Views.AJAX_POSTS;
		} else if ("prediction_history".equals(action)) {
			this.setPredictionHistory(command);
			return Views.AJAX_PREDICTIONS;
		}
		return Views.AJAX;
	}	

	/**
	 * flags a user as spammer
	 * @param cmd
	 */
	private void flagSpammer(AdminAjaxCommand cmd, boolean spammer) {		
		if (cmd.getUserName() != null) {
			User user = new User(cmd.getUserName());
			user.setToClassify(0);
			user.setAlgorithm("admin");
			user.setSpammer(spammer ? 1 : 0);
			
			this.logic.updateUser(user);
		}
	}

	private void updateSettings(AdminAjaxCommand command) {
		if (command.getKey() != null && command.getValue() != null) {
			ClassifierSettings setting = ClassifierSettings.getClassifierSettings(command.getKey());
			this.logic.updateClassifierSettings(setting, command.getValue());
		}
	}
	
	private void setLatestPosts(AdminAjaxCommand command) {
		if (command.getUserName() != null && command.getUserName() != "") {
			List<Post<Bookmark>> bookmarks = this.logic.getPosts(Bookmark.class, GroupingEntity.USER, command.getUserName(), null, null, Order.ADDED, 0, 5, null);
			command.setBookmarks(bookmarks);
		}
	}	

	private void setPredictionHistory(AdminAjaxCommand command) {
		if (command.getUserName() != null && command.getUserName() != "") {
			List<User> predictions = this.logic.getClassifierHistory(command.getUserName());
			command.setPredictionHistory(predictions);
		}		
	}
	
	public AdminAjaxCommand instantiateCommand() {
		return new AdminAjaxCommand();
	}
}