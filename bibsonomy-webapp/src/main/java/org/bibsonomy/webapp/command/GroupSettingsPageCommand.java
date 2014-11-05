package org.bibsonomy.webapp.command;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;

/**
 *
 * @author niebler
 */
public class GroupSettingsPageCommand extends BaseCommand {
	
	private Group group;
	private User user;

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
	
}
