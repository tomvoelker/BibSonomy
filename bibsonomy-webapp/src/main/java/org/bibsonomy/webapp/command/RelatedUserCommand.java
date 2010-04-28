package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.User;

/**
 * @author daill
 * @version $Id$
 */
public class RelatedUserCommand extends BaseCommand {
	
	/**
	 *  list of user to show
	 */
	private List<User> relatedUsers = new ArrayList<User>();

	/**
	 * @return list of user
	 */
	public List<User> getRelatedUsers() {
		return this.relatedUsers;
	}

	/**
	 * @param relatedUsers the relatedUsers to set
	 */
	public void setRelatedUsers(final List<User> relatedUsers) {
		this.relatedUsers = relatedUsers;
	}

}
