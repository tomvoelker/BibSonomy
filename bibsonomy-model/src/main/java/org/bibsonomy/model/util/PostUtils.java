/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.util.Sets;

/**
 * Static methods to handle Posts.
 *
 * @author rja
 */
public class PostUtils {

	/**
	 * populates the post
	 * {@link #populatePostWithDate(Post, User)}
	 * {@link #populatePostWithUser(Post, User)}
	 *
	 * @param post
	 * @param user
	 */
	public static void populatePost(final Post<? extends Resource> post, final User user) {
		populatePostWithUser(post, user);
		populatePostWithDate(post, user);
		populatePostWithChangeDate(post, user);
	}

	/**
	 * sets the owner of the post to the user if current owner is null or name of current owner isn't present
	 * @param post
	 * @param user
	 */
	public static void populatePostWithUser(final Post<? extends Resource> post, final User user) {
		final User postUser = post.getUser();
		if (!present(postUser) || !present(postUser.getName())) {
			post.setUser(user);
		}
	}

	/**
	 * Overwrites the date of the post if the user is not allowed to set it.
	 * If the post does not contain a date, the current date is set.
	 *
	 * @param post
	 * @param loginUser
	 */
	public static void populatePostWithDate(final Post<? extends Resource> post, final User loginUser) {
		if (!Role.SYNC.equals(loginUser.getRole()) || !present(post.getDate())) {
			post.setDate(new Date());
		}
	}

	/**
	 * overwrites the change date of the post if the user is not allowed to set it
	 *
	 * @param post
	 * @param loginUser
	 */
	public static void populatePostWithChangeDate(final Post<? extends Resource> post, final User loginUser) {
		if (!Role.SYNC.equals(loginUser.getRole()) || !present(post.getChangeDate())) {
			post.setChangeDate(new Date());
		}
	}

	/**
	 * Modifies the group IDs in the post to be spam group IDs or non-spam group IDs,
	 * depending on the spammer status of the given user.
	 *
	 * @see #setGroupIds(Post, boolean)
	 * @param post
	 * @param postOwner
	 * @throws ValidationException - if the user name of the post does not match the given user name.
	 */
	public static void setGroupIds(final Post<? extends Resource> post, final User postOwner) throws ValidationException {
		if (!present(postOwner.getName())) {
			throw new ValidationException("user name of post does not match user name of posting user");
		}
		setGroupIds(post, postOwner.isSpammer());
	}

	/**
	 * Change all groups to private in case of a limited login user
	 *
	 * @param post
	 * @param user
	 */
	public static void limitedUserModification(final Post<? extends Resource> post, final User user) {
		if (Role.LIMITED.equals(user.getRole())) {
			if (!GroupUtils.isPrivateGroup(post.getGroups())) {
				// if post it not private set it to private
				post.setGroups(Sets.asSet(GroupUtils.buildPrivateGroup()));
			}
		}
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
			group.setGroupId(GroupID.getGroupId(group.getGroupId(), isSpammer));
		}
	}

}
