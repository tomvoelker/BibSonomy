/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;

/**
 * @author Christian Schenk
 */
public class GroupUtils {
	
	/**
	 * the public group name
	 */
	public static final String PUBLIC_GROUP_NAME = "public";
	
	/**
	 * the private group name
	 */
	public static final String PRIVATE_GROUP_NAME = "private";

	/**
	 * the friends group name
	 */
	public static final String FRIENDS_GROUP_NAME = "friends";


	/**
	 * Public group
	 * 
	 * @return public group
	 */
	public static Group getPublicGroup() {
		return getGroup(PUBLIC_GROUP_NAME,  "public group",  GroupID.PUBLIC,  Privlevel.PUBLIC);
	}

	/**
	 * Private group
	 * 
	 * @return private group
	 */
	public static Group getPrivateGroup() {
		return getGroup(PRIVATE_GROUP_NAME, "private group", GroupID.PRIVATE, Privlevel.HIDDEN);
	}

	/**
	 * Friends group
	 * 
	 * @return friends group
	 */
	public static Group getFriendsGroup() {
		return getGroup(FRIENDS_GROUP_NAME, "friends group", GroupID.FRIENDS, Privlevel.HIDDEN);
	}


	/**
	 * Public spam group
	 * 
	 * @return public group
	 */
	public static Group getPublicSpamGroup() {
		return getGroup(PUBLIC_GROUP_NAME,  "public group",  GroupID.PUBLIC_SPAM,  Privlevel.PUBLIC);
	}

	/**
	 * Private spam group
	 * 
	 * @return private group
	 */
	public static Group getPrivateSpamGroup() {
		return getGroup(PRIVATE_GROUP_NAME, "private group", GroupID.PRIVATE_SPAM, Privlevel.HIDDEN);
	}

	/**
	 * Friends spam group
	 * 
	 * @return friends group
	 */
	public static Group getFriendsSpamGroup() {
		return getGroup(FRIENDS_GROUP_NAME, "friends group", GroupID.FRIENDS_SPAM, Privlevel.HIDDEN);
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
	 * Checks if the given group is an "exclusive" group, i.e., a group which can't 
	 * be chosen as "viewable for" together with another group - basically the 
	 * groups "private" and "public". Use this method because it also checks spam
	 * groups! 
	 *  
	 * @param group
	 * @return <code>true</code> if the group is exclusively be "viewable for". 
	 */
	public static boolean isExclusiveGroup(final Group group) {
		return (
				getPrivateGroup().equals(group) || 
				getPublicGroup().equals(group)
		);
	}

	/**
	 * Checks if the given group ID is an "exclusive" group ID, i.e., a group which can't 
	 * be chosen as "viewable for" together with another group - basically the 
	 * groups "private" and "public". Use this method because it also checks spam
	 * groups! 
	 *  
	 * @param groupId
	 * @return <code>true</code> if the group is exclusively "viewable for" 
	 */
	public static boolean isExclusiveGroup(final int groupId) {
		return (
				GroupID.equalsIgnoreSpam(getPrivateGroup().getGroupId(), groupId) || 
				GroupID.equalsIgnoreSpam(getPublicGroup().getGroupId(), groupId)
		);
	}
	
	/**
	 * 
	 * @param groups
	 * @param isSpammer
	 */
	public static void prepareGroups(final Set<Group> groups, final boolean isSpammer) {
		for (final Group group : groups) {
			/*
			 * update the group id of the post
			 */
			group.setGroupId(GroupID.getGroupId(group.getGroupId(), isSpammer));
		}
	}

	/**
	 * Checks if the given groups contain an "exclusive" group, i.e., a group which 
	 * can't be chosen as "viewable for" together with another group - basically the 
	 * groups "private" and "public". Use this method because it also checks spam
	 * groups! 
	 *  
	 * @param groups
	 * @return <code>true</code> if one of the groups is exclusively be "viewable for". 
	 */
	public static boolean containsExclusiveGroup(final Set<Group> groups) {
		/*
		 * at least one of the groups is public or private
		 */
		return groups.contains(getPublicGroup()) || groups.contains(getPrivateGroup());
	}
	
	/**
	 * Checks if the set of groups contains only the public group.
	 * @param groups
	 * @return <code>true</code> if the set of groups contains only the public group.
	 */
	public static boolean isPublicGroup(final Set<Group> groups) {
		return groups.size() == 1 && groups.contains(getPublicGroup());
	}
	
	/**
	 * Checks if the set of groups contains only the private group.
	 * @param groups
	 * @return <code>true</code> if the set of groups contains only the private group.
	 */
	public static boolean isPrivateGroup(final Set<Group> groups) {
		return groups.size() == 1 && groups.contains(getPrivateGroup());
	}
	
	/**
	 * Checks if the given name can be used for a new group. The reserved names are
	 * public, friends and private.
	 * 
	 * @param name
	 * @return <code>true</code> if the name is valid.
	 */
	public static boolean isValidGroupName(String name) {
		String normedName = name.toLowerCase();
		return !GroupUtils.FRIENDS_GROUP_NAME.equals(normedName) &&
			!GroupUtils.PUBLIC_GROUP_NAME.equals(normedName) &&
			!GroupUtils.PRIVATE_GROUP_NAME.equals(normedName);
		
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
	
	public static User getDummyUser(Group group) {
		for (GroupMembership ms : group.getMemberships()) {
			if (ms.getGroupRole().equals(GroupRole.DUMMY)) {
				return ms.getUser();
			}
		}
		throw new IllegalArgumentException("The group " + group.getName()+ " has no dummy user!");
	}
}