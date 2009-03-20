package org.bibsonomy.model.util;

import java.util.Set;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.util.ValidationUtils;

/**
 * Static methods to handle Posts.
 * 
 * @author rja
 * @version $Id$
 */
public class PostUtils {

	/**
	 * Modifes the group IDs in the post to be spam group IDs or non-spam group IDs,
	 * depending on the spamemr status of the given user.
	 *  
	 * @see #setGroupIds(Post, boolean)
	 * @param post
	 * @param user
	 * @throws ValidationException - if the user name of the post does not match the given user name.
	 */
	public static void setGroupIds(final Post<? extends Resource> post, final User user) throws ValidationException {
		if (!ValidationUtils.present(user.getName()) || !user.getName().equals(post.getUser().getName())) {
			throw new ValidationException("user name of post does not match user name of posting user");
		}
		setGroupIds(post, user.isSpammer());
	}
	
	/**
	 * Modifies the group IDs in the post to be spam group IDs or non-spam group IDs,
	 * depending on the given <code>spammer</code> flag.
	 * <br/>
	 * Note: the post must already contain the integer IDs of the groups, otherwise 
	 * flagging does not work! 
	 * 
	 * @param post - the post whose groups should be modified.
	 * @param isSpammer - <code>true</code> if the user of the post is a spammer.
	 */
	public static void setGroupIds(final Post<? extends Resource> post, final boolean isSpammer) {
		final Set<Group> groups = post.getGroups();
		for (final Group group : groups) {
			/*
			 * update the group id of the post
			 */
			group.setGroupId(UserUtils.getGroupId(group.getGroupId(), isSpammer));
		}
	}
	
}
