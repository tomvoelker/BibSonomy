package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.User;

/**
 * @author daill
 * @version $Id$
 */
public class RelatedUserCommand extends BaseCommand{
	
	// list of user to show
	List<User> relatedUser = new ArrayList<User>();
	
	public RelatedUserCommand() {}
	
	RelatedUserCommand (List<User> relatedUser){
		this.relatedUser = relatedUser;
	}

	/**
	 * @return list of user
	 */
	public List<User> getRelatedUser() {
		return this.relatedUser;
	}

	/**
	 * @param relatedUser
	 */
	public void setRelatedUser(final List<User> relatedUser) {
		this.relatedUser = relatedUser;
	}

}
