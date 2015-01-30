/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupLevelPermission;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.TagSetParam;
import org.bibsonomy.database.params.WikiParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.GroupRequest;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.TagSet;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.util.ExceptionUtils;
import org.bibsonomy.wiki.TemplateManager;

/**
 * Used to retrieve groups from the database.
 * 
 * @author Christian Schenk
 * @author Thomas Niebler
 */
public class GroupDatabaseManager extends AbstractDatabaseManager {
	private static final Log log = LogFactory.getLog(GroupDatabaseManager.class);

	private final static GroupDatabaseManager singleton = new GroupDatabaseManager();

	/**
	 * @return GroupDatabaseManager
	 */
	public static GroupDatabaseManager getInstance() {
		return singleton;
	}

	private UserDatabaseManager userDb;
	private final DatabasePluginRegistry plugins;

	private GroupDatabaseManager() {
		this.plugins = DatabasePluginRegistry.getInstance();
	}

	/**
	 * Returns a list of all groups without membership information
	 * @param start 
	 * @param end 
	 * @param session 
	 * @return a list of all groups
	 */
	public List<Group> getAllGroups(final int start, final int end, final DBSession session) {
		final GroupParam param = LogicInterfaceHelper.buildParam(GroupParam.class, Order.ALPH, start, end);
		return this.queryForList("getAllGroups", param, Group.class, session);
	}
	
	/**
	 * Returns pending groups.
	 * 
	 * @param start 
	 * @param end 
	 * @param session 
	 * @return list of all pending groups
	 */
	public List<Group> getPendingGroups(final int start, final int end,  final DBSession session) {
		final GroupParam param = new GroupParam();
		param.setOffset(start);
		param.setLimit(end);
		return this.queryForList("getPendingGroups", param, Group.class, session);
    }

	/**
	 * Returns a specific group with memberships
	 * DEPRECATED: Use getGroupMembers instead
	 * 
	 * @param groupname 
	 * @param session 
	 * @return Returns a {@link Group} object if the group exists otherwise null.
	 */
	@Deprecated
	public Group getGroupByName(final String groupname, final DBSession session) {
		if (!present(groupname)) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Groupname isn't present");
		}
		final String normedGroupName = this.getNormedGroupName(groupname);
		if ("public".equals(normedGroupName)) {
			return GroupUtils.getPublicGroup();
		}

		if ("private".equals(normedGroupName)) {
			return GroupUtils.buildPrivateGroup();
		}

		if ("friends".equals(normedGroupName)) {
			return GroupUtils.buildFriendsGroup();
		}
		
		return this.queryForObject("getGroupWithMemberships", normedGroupName, Group.class, session);
	}

	/**
	 * Returns a list of tagsets for a group
	 *
	 * @param groupname - the name of the group
	 * @param session
	 * @return Return a list of {@link TagSet} objects if the group exists and if there are tagsets related to th group
	 */
	public List<TagSet> getGroupTagSets(final String groupname, final DBSession session){
		return this.queryForList("getTagSetsForGroup", groupname, TagSet.class, session);
	}

	/**
	 * Returns a TagSet for a group with a given setname 
	 * 
	 * @param setName - the name of the tagSet
	 * @param groupId - the id of the group
	 * @param session 
	 * @return a TagSet
	 */
	private TagSet getTagSetBySetNameAndGroup(final String setName, final int groupId, final DBSession session){
		final TagSetParam param = new TagSetParam();
		param.setSetName(setName);
		param.setGroupId(groupId);
		return this.queryForObject("getTagSetBySetNameAndGroup", param, TagSet.class, session);
	}

	/**
	 * Returns a group with all its members if the user is allowed to see them.
	 * 
	 * @param authUser 
	 * @param groupname 
	 * @param getPermissions TODO
	 * @param session 
	 * @return group 
	 */
	public Group getGroupMembers(final String authUser, final String groupname, final boolean getPermissions, final DBSession session) {
		log.debug("getGroupMembers " + groupname);
		Group group;
		if ("friends".equals(groupname)) {
			group = GroupUtils.buildFriendsGroup();
			final List<GroupMembership> mss = new LinkedList<>();
			for (final User u : this.userDb.getUserRelation(authUser, UserRelation.OF_FRIEND, null, session)) {
				mss.add(new GroupMembership(u, GroupRole.USER, true));
			}
			group.setMemberships(mss);
			return group;
		}
		if ("public".equals(groupname)) {
			group = GroupUtils.getPublicGroup();
			group.setMemberships(Collections.<GroupMembership> emptyList());
			return group;
		}
		if ("private".equals(groupname)) {
			group = GroupUtils.buildPrivateGroup();
			group.setMemberships(Collections.<GroupMembership> emptyList());
			return group;
		}
		final String statement;
		if (getPermissions) {
			statement = "getGroupWithMembershipsAndPermissions";
		} else {
			statement = "getGroupWithMemberships";
		}
		
		group = this.queryForObject(statement, groupname, Group.class, session);
		// the group has no members. At least the dummy user should exist.
		if (group == null) {
			log.debug("group " + groupname + " does not exist");
			group = GroupUtils.buildInvalidGroup();
			group.setMemberships(Collections.<GroupMembership> emptyList());
			return group;
		}
		
		final int groupId = group.getGroupId();
		final Privlevel privlevel = this.getPrivlevelForGroup(groupId, session);
		// remove members as necessary
		switch (privlevel) {
		case MEMBERS:
			// if the user isn't a member of the group he can't see other
			// members -> and we'll fall through to HIDDEN
			if (this.isUserInGroup(authUser, group)) break;
			//$FALL-THROUGH$
		case HIDDEN:
			// only a group admin may always see the group members
			final GroupMembership groupMembershipForUser = this.getGroupMembershipForUser(authUser, group, session);
			if (!present(groupMembershipForUser) || !GroupRole.ADMINISTRATOR.equals(groupMembershipForUser.getGroupRole())) {
				group.setMemberships(Collections.<GroupMembership>emptyList());
			}
			break;
		case PUBLIC:
			// ignore
			break;
		}
		
		return group;
	}
	
	/**
	 * Returns the privlevel for a group.
	 */
	private Privlevel getPrivlevelForGroup(final int groupId, final DBSession session) {
		return this.queryForObject("getPrivlevelForGroup", groupId, Privlevel.class, session);
	}

	/**
	 * Returns the membership for the given user in a given group.
	 */
	private GroupMembership getGroupMembershipForUser(final String userName, final Group group, final DBSession session) {
		final GroupParam param = new GroupParam();
		param.setUserName(userName);
		param.setGroupId(group.getGroupId());
		
		return this.queryForObject("getGroupMembershipForUserInGroup", param, GroupMembership.class, session);
	}
	
	/**
	 * @param user
	 * @param groupName
	 * @param session
	 * @return
	 */
	public GroupMembership getPendingMembershipForUserAndGroup(User user, String groupName, DBSession session) {
		final GroupParam param = new GroupParam();
		param.setUserName(user.getName());
		param.setRequestedGroupName(groupName);
		return this.queryForObject("getPendingMembershipForUserInGroup", param, GroupMembership.class, session);
	}

	/**
	 * Returns true if there's only one admin for the group.
	 */
	private boolean hasOneAdmin(final Group g, final DBSession session) {
		final GroupParam p = new GroupParam();
		p.setMembership(new GroupMembership(null, GroupRole.ADMINISTRATOR, true));
		p.setGroupId(g.getGroupId());
		final Integer count = this.queryForObject("countPerRole", p, Integer.class, session);
		return count != null && count.intValue() == 1;
	}

	/**
	 * Returns true if the user is in the group otherwise false.
	 */
	private boolean isUserInGroup(final String username, final Group group) {
		for (final GroupMembership ms : group.getMemberships()) {
			if (ms.getUser().getName().equals(username)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a a list of groups for a given user
	 * 
	 * @param username 
	 * @param session 
	 * @return a list of groups
	 */
	public List<Group> getGroupsForUser(final String username, final DBSession session) {
		return this.getGroupsForUser(username, false, session);
	}

	/**
	 * Get all groups a user is a member of, with or without special groups.
	 * 
	 * @param userName
	 * @param removeSpecialGroups
	 * @param session
	 * @return a list of groups the user is member of
	 */
	public List<Group> getGroupsForUser(final String userName, final boolean removeSpecialGroups, final DBSession session) {
		final List<Group> groupsForUser = this.queryForList("getGroupsForUser", userName, Group.class, session);
		if (!removeSpecialGroups) {
			groupsForUser.addAll(this.queryForList("getSpecialGroupsForUser", userName, Group.class, session));
		}
		
		return groupsForUser;
	}

	/**
	 * Helper function to remove special groups from a List of groups
	 * 
	 * @param groups a list of groups 
	 * @return a new list of groups with special groups removed
	 */
	public List<Group> removeSpecialGroups(final List<Group> groups) {
		final List<Group> newGroups = new ArrayList<>();
		for (final Group group : groups) {
			if (!GroupID.isSpecialGroupId(group.getGroupId()))
				newGroups.add(group);
		}
		return newGroups;
	}

	/**
	 * Gets all groups in which both user A and user B are in. 
	 * The userSharedDocuments values of userNameB will be returned.
	 * 
	 * @param userNameA - name of the first user.
	 * @param userNameB - name of the second user.
	 * @param session
	 * @return The list of groups both given users are in.
	 */
	public List<Group> getCommonGroups(final String userNameA, final String userNameB, final DBSession session) {
		final List<Group> groupsOfUserA = this.getGroupsForUser(userNameA, true, session);
		final List<Group> groupsOfUserB = this.getGroupsForUser(userNameB, true, session);

		/*
		 * It is not very efficient, to do this in two cascaded loops, but users
		 * are typically in very few groups and with linked lists there is
		 * probably no much more efficient way to do it.
		 */
		final List<Group> commonGroups = new LinkedList<>();
		for (final Group groupOfUserA : groupsOfUserA) {
			for (final Group groupOfUserB : groupsOfUserB) {
				if (groupOfUserA.getGroupId() == groupOfUserB.getGroupId()) {
					/*
					 * we add the group of user b to the result list
					 * because we need the attribute of user b
					 * if he shares documents with other
					 * group members
					 */
					commonGroups.add(groupOfUserB);
				}
			}
		}
		return commonGroups;
	}

	/**
	 * Returns a a list of groups for a given contentID
	 * 
	 * @param contentId 
	 * @param session 
	 * @return a list of groups
	 */
	public List<Group> getGroupsForContentId(final Integer contentId, final DBSession session) {
		return this.queryForList("getGroupsForContentId", contentId, Group.class, session);
	}

	/**
	 * Gets all the groupIds of the given users groups.
	 * 
	 * @param userName userName to get the groupids for
	 * @param session a db session
	 * @return A list of groupids
	 */
	public List<Integer> getGroupIdsForUser(final String userName, final DBSession session) {
		if (!present(userName)) return new ArrayList<>();
		return this.queryForList("getGroupIdsForUser", userName, Integer.class, session);
	}

	/**
	 * Checks if group exists.
	 * 
	 * @param groupname 
	 * @param session a db session
	 * @return groupid of group, GroupID.GROUP_INVALID otherwise
	 */
	public Integer getGroupIdByGroupName(final String groupname, final DBSession session) {
		return this.getGroupIdByGroupNameAndUserName(groupname, null, session);
	}

	/**
	 * Checks if a given user is in the given group.
	 * 
	 * @param groupname
	 * @param username 
	 * @param session a db session
	 * @return groupid if user is in group, GroupID.GROUP_INVALID otherwise
	 */
	public Integer getGroupIdByGroupNameAndUserName(final String groupname, final String username, final DBSession session) {
		if (!present(groupname)) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "groupname isn't set");
		}
		try {
			final GroupID specialGroup = GroupID.getSpecialGroup(groupname);
			if (specialGroup != null) {
				return Integer.valueOf(specialGroup.getId());
			}
		} catch (final IllegalArgumentException ignore) {
			// do nothing - this simply means that the given group is not a special group
		}

		final GroupParam param = new GroupParam();
		param.setRequestedUserName(username);
		param.setRequestedGroupName(groupname);

		// FIXME: what about dummy, join request and invited users?
		final Integer rVal = this.queryForObject("getGroupIdByGroupNameAndUserName", param, Integer.class, session);
		if (rVal == null) {
			return Integer.valueOf(GroupID.INVALID.getId());
		}
		return rVal;
	}

	/**
	 * Activates a group.
	 * @param groupName 
	 * @param session 
	 */
	public void activateGroup(final String groupName, final DBSession session) {
		// get the group 
		final Group group = this.getPendingGroup(groupName, session);
		
		// TODO: check if group exists
		final GroupRequest groupRequest = group.getGroupRequest();

		try {
			session.beginTransaction();
			// activate the user
			this.userDb.performActivationSteps(new User(groupName), session);
			
			// "move" the pending group row to the normal group table
			this.insert("activateGroup", groupName, session);
			
			// clear the pending group table
			this.deletePendingGroup(groupName, session);
			
			// add the group user to the group
			this.addUserToGroup(groupName, groupName, GroupRole.DUMMY, session);
			
			// add the requesting user to the group with level ADMINISTRATOR
			this.addUserToGroup(groupName, groupRequest.getUserName(), GroupRole.ADMINISTRATOR, session);
			
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}
	
	/**
	 * Returns a specific pending group
	 * 
	 * @param groupname 
	 * @param session 
	 * @return Returns a {@link Group} object if the group exists otherwise null.
	 */
	public Group getPendingGroup(final String groupname, final DBSession session) {
		if (!present(groupname)) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Pending-Groupname isn't present");
		}
		final String normedGroupName = this.getNormedGroupName(groupname);
		return this.queryForObject("getPendingGroup", normedGroupName, Group.class, session);
	}

	/**
	 * Creates a group in the database.
	 * 
	 * @param group 
	 * @param session 
	 */
	public void createGroup(final Group group, final DBSession session) {
		final String groupName = group.getName();
		
		final String normedGroupName = getNormedGroupName(groupName);
		group.setName(normedGroupName);
		
		// make sure the group name differs from the special groups
		if (!GroupUtils.isValidGroupName(normedGroupName)) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "The name \"" + normedGroupName + "\" is reserved.");
		}
		/*
		 * check if a user or a pending user exists with that name
		 * currently every group also has a corresponding user in the system
		 */
		final User existingGroupUser = this.userDb.getUserDetails(normedGroupName, session);
		final List<User> pendingUserList = this.userDb.getPendingUserByUsername(normedGroupName, 0, Integer.MAX_VALUE, session);
		final Group existingPendingGroup = this.getPendingGroup(normedGroupName, session);
		
		if (present(existingGroupUser.getName()) || present(pendingUserList) || present(existingPendingGroup)) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "There is a user with this name - cannot create the group.");
		}
		
		// create the user
		final User groupUser = UserUtils.buildGroupUser(normedGroupName);
		
		try {
			session.beginTransaction();
			this.userDb.createUser(groupUser, session);
			this.insertGroup(group, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * Returns the normed group name. Everything is in lower case.
	 * @param groupName a group name
	 * @return groupName in lower case
	 */
	private String getNormedGroupName(final String groupName) {
		if (!present(groupName)) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "No group name specified.");
		}
		
		return groupName.toLowerCase();
	}

	/**
	 * Inserts a group into the pending groups table.
	 */
	private void insertGroup(final Group group, final DBSession session) {
		final int newGroupId = this.getNewGroupId(session);
		group.setGroupId(newGroupId);
		this.insert("insertPendingGroup", group, session);
		this.insertDefaultWiki(group, session);
	}
	
	/**
	 * Removes the group and the groupuser from the pending tables.
	 * 
	 * @param groupname
	 * @param session
	 */
	public void deletePendingGroup(final String groupname, final DBSession session) {
		// make sure that the group exists
		if (groupname == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Pending Group ('" + groupname + "') doesn't exist");
			throw new RuntimeException();
		}
		this.userDb.deletePendingUser(groupname, session);
		this.delete("deletePendingGroup", groupname, session);
	}

    /**
     * Inserts a default wiki for a newly created group.
     * 
     * @param group 
     * @param session
     */
	private void insertDefaultWiki(final Group group, final DBSession session) {
		WikiParam param = new WikiParam();
		param.setUserName(group.getName());
		param.setDate(new Date());

		param.setWikiText(TemplateManager.getTemplate("group1en"));
		this.update("updateWikiForUser", param, session);
	}

	/**
	 * Insert a TagSet
	 * 
	 * @param tagset the Set to insert
	 * @param group the group which owns the tagset
	 * @param session
	 */
	private void insertTagSet(final TagSet tagset, final String groupname, final DBSession session) {
		final Group group = this.getGroupByName(groupname, session);
		if (!present(group)) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + groupname + "') doesn't exist");
		}
		if(tagset.getSetName().isEmpty() || tagset.getTags().isEmpty()){
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Invalid tagset - a tagset must contain a setname and at least one valid tag");
		}

		final TagSetParam param = new TagSetParam();
		param.setSetName(tagset.getSetName());
		param.setGroupId(group.getGroupId());
		final TagSet set = this.getTagSetBySetNameAndGroup(tagset.getSetName(), group.getGroupId(), session);
		for (final Tag tag: tagset.getTags()){
			if (set.getTags().contains(tag)){
				ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "INSERT FAILED: tag ('"+tag.getName()+"') already contained in the tagset ('"+tagset.getSetName()+"') for group ('"+group.getName()+"')");
			}
			param.setTagName(tag.getName());
			this.insert("insertTagSet", param, session);
		}

	}

	/**
	 * Deletes a TagSet in the DataBase
	 * 
	 * @param setName - the name of the TagSet to delete
	 * @param group - the group of the TagSet
	 * @param session
	 */
	private void deleteTagSet(final String setName, final String groupname, final DBSession session){
		final Group group = this.getGroupByName(groupname, session);
		if (group == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + groupname + "') doesn't exist");
			throw new RuntimeException(); // never happens but calms down eclipse 
		}
		TagSet tagset = this.getTagSetBySetNameAndGroup(setName, group.getGroupId(), session);
		if(tagset == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "TagSet ('" + setName + "') doesn't exist for group ('" + groupname + "')");
		}

		final TagSetParam param = new TagSetParam();
		param.setSetName(setName);
		param.setGroupId(group.getGroupId());
		this.delete("deleteTagSet", param, session);
	}

	/**
	 * TODO: method unused
	 * 
	 * Update an existing TagSet
	 * 
	 * @param tagset
	 * @param groupname
	 * @param session
	 */
	@SuppressWarnings("unused")
	private void updateTagSet(final TagSet tagset, final String groupname, final DBSession session){
		//delete the old TagSet
		this.deleteTagSet(tagset.getSetName(), groupname, session);

		//insert the new TagSet
		this.insertTagSet(tagset, groupname, session);
	}

	/**
	 * Returns a new groupId.
	 */
	private int getNewGroupId(final DBSession session) {
		return this.queryForObject("getNewGroupId", null, Integer.class, session).intValue();
	}

	/**
	 * Delete a group from the database.
	 * 
	 * @param groupname 
	 * @param session 
	 */
	public void deleteGroup(final String groupname, final DBSession session) {
		// make sure that the group exists
		final Group group = this.getGroupByName(groupname, session);
		if (group == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + groupname + "') doesn't exist");
			throw new RuntimeException(); // never happens but calms down eclipse 
		}
		
		final Integer groupId = Integer.valueOf(group.getGroupId());
		this.delete("deleteGroup", groupId, session);
		this.delete("removeAllUserFromGroup", groupId, session);
	}


	/**
	 * Adds a user to a group.
	 * 
	 * @param groupname 
	 * @param username 
	 * @param role 
	 * @param session 
	 */
	public void addUserToGroup(final String groupname, final String username, final GroupRole role, final DBSession session) {
		try {
			session.beginTransaction();
			// check if a user exists with that name
			final User user = this.userDb.getUserDetails(username, session);
			if (user.getName() == null) {
				ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "There's no user with this name ('" + username + "')");
			}
			if (user.isSpammer()) {
				ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "The user '" + username + "' is a spammer");
			}
			
			// make sure that the group exists
			final Group group = this.getGroupByName(groupname, session);
			if (group == null) {
				ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + groupname + "') doesn't exist - can't add user to nonexistent group");
			}
			// make sure that the user isn't a member of the group
			if (this.isUserInGroup(username, group)) {
				ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User ('" + username + "') is already a member of this group ('" + groupname + "')");
			}
			
			// maybe there is a pending membership -> delete it
			this.removePendingMembership(groupname, username, session);
			
			// add user to group
			final GroupParam param = new GroupParam();
			param.setGroupId(group.getGroupId());
			/* 
			 * TODO: shares documents setting must be changed if we allow users
			 * to specify shared documents in the join request
			 */
			param.setMembership(new GroupMembership(user, role, false));
			
			this.insert("addUserToGroup", param, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * Removes a user from a group.
	 * 
	 * @param groupname 
	 * @param username 
	 * @param session 
	 */
	public void removeUserFromGroup(final String groupname, final String username, final DBSession session) {
		// make sure that the group exists
		final Group group = this.getGroupByName(groupname, session);
		if (group == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + groupname + "') doesn't exist - can't remove user from nonexistent group");
		}
		// make sure that the user is a member of the group
		if (!this.isUserInGroup(username, group)) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User ('" + username + "') isn't a member of this group ('" + groupname + "')");
		}
		// check if we have only one group admin
		if (this.hasOneAdmin(group, session)) {
			// check the group role for the given username
			GroupRole activeRole = group.getGroupMembershipForUser(new User(username)).getGroupRole();
			// the user is the last admin, we can't remove him.
			if (GroupRole.ADMINISTRATOR.equals(activeRole)) {
				ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User ('" + username + "') is the last group admin and can't be deleted.");		
			}
		}
		
		
		final GroupParam param = new GroupParam();
		param.setUserName(username);
		param.setGroupId(group.getGroupId());

		this.plugins.onChangeUserMembershipInGroup(param.getUserName(), param.getGroupId(), session);
		this.delete("removeUserFromGroup", param, session);
	}
	
	/**
	 * Updates the users role.
	 * @param loginUser
	 * @param groupname
	 * @param username
	 * @param newGroupRole 
	 * @param session
	 */
	public void updateGroupRole(User loginUser, final String groupname, final String username, final GroupRole newGroupRole, final DBSession session) {
		// make sure that the group exists
		final Group group = this.getGroupByName(groupname, session);
		if (group == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + groupname + "') doesn't exist - can't update the grouprole");
		}
		if (!this.isUserInGroup(username, group)) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User ('" + username + "') isn't a member of this group ('" + groupname + "')");
		}
		
		if (!GroupRole.GROUP_ROLES.contains(newGroupRole)) {
			throw new IllegalArgumentException("group role '" + newGroupRole + "' not supported");
		}
		
		// check the old user role
		final GroupMembership oldMembership = this.getGroupMembershipForUser(username, group, session);
		final GroupRole oldRole = oldMembership.getGroupRole();
		
		// only perform action if they differ XXX: exception for this case?
		if (oldRole.equals(newGroupRole)) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User ('" + username + "') already has this role in this group ('" + groupname + "')");
		}
		
		// make sure that we keep at least one admin
		if (GroupRole.ADMINISTRATOR.equals(oldRole) && this.hasOneAdmin(group, session)) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User ('" + username + "') is the last administrator of this group ('" + groupname + "')");
		}
		
		// check if the current group role of the logged in users allows to change the requested user
		final GroupRole loggedinUserRole = GroupUtils.getGroupMembershipOfUserForGroup(loginUser, groupname).getGroupRole();
		if (!checkGroupRoleChange(loggedinUserRole, newGroupRole, oldRole)) {
			throw new AccessDeniedException("you can not change the group role of user " + username + " to " + newGroupRole);
		}
		
		final GroupParam param = new GroupParam();
		param.setUserName(username);
		param.setGroupId(group.getGroupId());
		oldMembership.setGroupRole(newGroupRole);
		param.setMembership(oldMembership);
		
		this.plugins.onChangeUserMembershipInGroup(param.getUserName(), param.getGroupId(), session);
		this.update("updateGroupRole", param, session);
	}

	/**
	 * @param object
	 * @param groupRole
	 */
	private static boolean checkGroupRoleChange(final GroupRole ownGroupRole, final GroupRole groupRole, final GroupRole oldGroupRole) {
		switch (ownGroupRole) {
		case ADMINISTRATOR:
			// admin can do anything
			return true;
		case MODERATOR:
			// don't add group admins and do not modify admins
			return GroupRole.ADMINISTRATOR != groupRole && GroupRole.ADMINISTRATOR != oldGroupRole;
		default:
			return false;
		}
	}
	
	/**
	 * Removes the join request or invite from the group.
	 * 
	 * @param groupname
	 * @param username
	 * @param session
	 */
	public void removePendingMembership(final String groupname, final String username, final DBSession session) {
		final Group group = this.getGroupByName(groupname, session);
		if (group == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + groupname + "') doesn't exist - can't remove join request/invite from nonexistent group");
			throw new RuntimeException();
		}
		
		final GroupParam param = new GroupParam();
		param.setMembership(new GroupMembership(new User(username), GroupRole.DUMMY, true));
		param.setGroupId(group.getGroupId());
		
		this.delete("removePendingMembership", param, session);
	}
	
	public List<Group> getPendingMembershipsForUser(final String username, final DBSession session) {
		return this.queryForList("getPendingMembershipsForUser", username, Group.class, session);
	}
	
	public Group getGroupWithPendingMemberships(final String groupname, final DBSession session) {
		return this.queryForObject("getPendingMembershipsForGroup", groupname, Group.class, session);
	}
	
	public void addPendingMembership(final String groupname, final User user, final GroupRole pendingGroupRole, final DBSession session) {
		final Group group = this.getGroupByName(groupname, session);
		final String username = user.getName();
		final User groupMembershipUser = this.userDb.getUserDetails(username, session);
		if (!present(groupMembershipUser.getName())) {
			ExceptionUtils.logErrorAndThrowQueryTimeoutException(log, null, "user " + username + " not found.");
		}
		final GroupMembership alreadyExistingMembership = this.getGroupMembershipForUser(username, group, session);
		if (group == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + groupname + "') doesn't exist - can't remove join request/invite from nonexistent group");
		}
		if (present(alreadyExistingMembership)) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User " + username + " is already a member of group " + groupname);
		}
		
		try {
			session.beginTransaction();
			final GroupMembership pendingMembership = this.getPendingMembershipForUserAndGroup(user, groupname, session);
			
			if (!present(pendingMembership)) {
				final GroupMembership membership = new GroupMembership();
				membership.setUser(user);
				membership.setGroupRole(pendingGroupRole);
				
				final GroupParam param = new GroupParam();
				param.setMembership(membership);
				param.setGroupId(group.getGroupId());
				
				this.insert("addPendingMembership", param, session);
			} else {
				switch (pendingMembership.getGroupRole()) {
				case INVITED:
					if (GroupRole.REQUESTED.equals(pendingGroupRole)) {
						this.addUserToGroup(groupname, username, GroupRole.USER, session);
					}
					break;
				case REQUESTED:
					if (GroupRole.INVITED.equals(pendingGroupRole)) {
						this.addUserToGroup(groupname, username, GroupRole.USER, session);
					}
					break;
				default:
					break;
				}
			}
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}
	
	/**
	 * Updates a group's privacy level and documents settings. 
	 * TODO: tests
	 * 
	 * @param groupToUpdate 
	 * @param session
	 */
	public void updateGroupSettings(Group groupToUpdate, final DBSession session) {
		if (!present(groupToUpdate)) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "During updateGroupSettings: The parameter groupToUpdate was null. (required argument)");
		}

		if (!(present(groupToUpdate.getGroupId()) && present(groupToUpdate.getPrivlevel()) && present(groupToUpdate.isSharedDocuments())) )  {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "During updateGroupSettings: Incomplete group information: group ID, privlevel and shared documents are required.");
		}
		/*
		 * store the bean
		 */
		
		//TODO: Logging!
		
		this.update("updateGroupSettings", groupToUpdate, session);
	}

	/**
	 * updates the user shared documents field for the given user.
	 * 
	 * @param group
	 * @param membership 
	 * @param session
	 */
	public void updateUserSharedDocuments(final Group group, GroupMembership membership, final DBSession session) {
		final GroupParam param = new GroupParam();
		param.setMembership(membership);
		param.setRequestedGroupName(group.getName());
		
		this.plugins.onChangeUserMembershipInGroup(param.getMembership().getUser().getName(), group.getGroupId(), session);
		this.update("updateUserSharedDocuments", param, session);
	}
	
	/**
	 * updates the groups publication reporting settings
	 * @param group
	 * @param session
	 */
	public void updateGroupPublicationReportingSettings(final Group group, final DBSession session) {
		this.update("updateGroupPublicationReportingSettings", group, session);
	}
	
	/**
	 * @param userDb the userDb to set
	 */
	public void setUserDb(UserDatabaseManager userDb) {
		this.userDb = userDb;
	}

	/**
	 * @param loginUserName
	 * @param group 
	 * @param session 
	 * @param paramGroup
	 */
	public void updateGroupLevelPermissions(final String loginUserName, final Group group, final DBSession session) {
		try {
			session.beginTransaction();
			final Group existinGroup = getGroupWithGroupLevelPermissions(group, session);
			if (!present(existinGroup)) {
				throw new IllegalArgumentException("Permissions can only be added to existing groups");
			}
			final Collection<GroupLevelPermission> permissionsToDelete = CollectionUtils.subtract(existinGroup.getGroupLevelPermissions(), group.getGroupLevelPermissions());
			final Collection<GroupLevelPermission> permissionsToInsert = CollectionUtils.subtract(group.getGroupLevelPermissions(), existinGroup.getGroupLevelPermissions());
			for (final GroupLevelPermission permissionToInsert : permissionsToInsert) {
				final GroupParam groupParam = new GroupParam();
				groupParam.setGroupId(existinGroup.getGroupId());
				groupParam.setGrantedByUser(loginUserName);
				groupParam.setGroupLevelPermission(permissionToInsert);
				this.insert("insertGroupLevelPermission", groupParam, session);
			}
			for (final GroupLevelPermission permissionToDelete : permissionsToDelete) {
				final GroupParam groupParam = new GroupParam();
				groupParam.setGroupId(existinGroup.getGroupId());
				groupParam.setGroupLevelPermission(permissionToDelete);
				this.delete("deleteGroupLevelPermission", groupParam, session);
			}
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}
	
	private Group getGroupWithGroupLevelPermissions(Group group, final DBSession session) {
		return this.queryForObject("getGroupWithPermissions", group.getName(), Group.class, session);
	}
}
