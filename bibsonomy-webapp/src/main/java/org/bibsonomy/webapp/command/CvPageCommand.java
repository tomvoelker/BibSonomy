package org.bibsonomy.webapp.command;

import org.bibsonomy.model.User;



/**
 * @author Philipp Beau
 *
 */
public class CvPageCommand extends ResourceViewCommand {
	
	private User user;
	

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
	
}
