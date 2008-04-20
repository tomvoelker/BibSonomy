package org.bibsonomy.model.util;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.model.Group;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class GroupUtils {

	/**
	 * Public group
	 * 
	 * @return public group
	 */
	public static Group getPublicGroup() {
		return getGroup("public", "public group", GroupID.PUBLIC, Privlevel.PUBLIC);
	}

	/**
	 * Private group
	 * 
	 * @return private group
	 */
	public static Group getPrivateGroup() {
		return getGroup("private", "private group", GroupID.PRIVATE, Privlevel.HIDDEN);
	}

	/**
	 * Friends group
	 * 
	 * @return friends group
	 */
	public static Group getFriendsGroup() {
		return getGroup("friends", "group of all your BibSonomy friends", GroupID.FRIENDS, Privlevel.HIDDEN);
	}

	/**
	 * Invalid group
	 * 
	 * @return invalid group
	 */
	public static Group getInvalidGroup() {
		return getGroup("invalid", "invalid group", GroupID.INVALID, Privlevel.HIDDEN);
	}

	/**
	 * Helper method that returns a new {@link Group} object.
	 */
	private static Group getGroup(final String name, final String description, final GroupID groupId, final Privlevel privlevel) {
		final Group group = new Group();
		group.setName(name);
		group.setDescription(description);
		group.setGroupId(groupId.getId());
		group.setPrivlevel(privlevel);
		return group;
	}
}