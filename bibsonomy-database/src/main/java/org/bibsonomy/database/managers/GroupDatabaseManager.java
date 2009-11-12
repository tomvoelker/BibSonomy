package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.TagSetParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.TagSet;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.util.ExceptionUtils;

/**
 * Used to retrieve groups from the database.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class GroupDatabaseManager extends AbstractDatabaseManager {

	private static final Log log = LogFactory.getLog(GroupDatabaseManager.class);

	private final static GroupDatabaseManager singleton = new GroupDatabaseManager();
	private final UserDatabaseManager userDb;
	private final DatabasePluginRegistry plugins;

	private GroupDatabaseManager() {
		this.userDb = UserDatabaseManager.getInstance();
		this.plugins = DatabasePluginRegistry.getInstance();
	}

	/**
	 * @return GroupDatabaseManager
	 */
	public static GroupDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Returns a list of all groups
	 * @param start 
	 * @param end 
	 * @param session 
	 * @return a list of all groups
	 */
	public List<Group> getAllGroups(final int start, final int end, final DBSession session) {
		final GroupParam param = LogicInterfaceHelper.buildParam(GroupParam.class, null, null, null, null, null, null, start, end, null, null, null);
		return this.queryForList("getAllGroups", param, Group.class, session);
	}

	/**
	 * Returns a specific group
	 * 
	 * @param groupname 
	 * @param session 
	 * @return Returns a {@link Group} object if the group exists otherwise null.
	 */
	public Group getGroupByName(final String groupname, final DBSession session) {
		if (present(groupname) == false) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Groupname isn't present");
		}

		if ("public".equals(groupname)) {
			return GroupUtils.getPublicGroup();
		}
		if ("private".equals(groupname)) {
			return GroupUtils.getPrivateGroup();
		}
		if ("friends".equals(groupname)) {
			return GroupUtils.getFriendsGroup();
		}

		return this.queryForObject("getGroupByName", groupname, Group.class, session);
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
	 * @param session 
	 * @return group 
	 */
	public Group getGroupMembers(final String authUser, final String groupname, final DBSession session) {
		log.debug("getGroupMembers " + groupname);
		Group group;
		if ("friends".equals(groupname)) {
			group = GroupUtils.getFriendsGroup();
			group.setUsers(this.userDb.getUserRelation(authUser, UserRelation.OF_FRIEND, session));
			return group;
		}
		if ("public".equals(groupname)) {
			group = GroupUtils.getPublicGroup();
			group.setUsers(Collections.<User> emptyList());
			return group;
		}
		if ("private".equals(groupname)) {
			group = GroupUtils.getPrivateGroup();
			group.setUsers(Collections.<User> emptyList());
			return group;
		}

		group = this.queryForObject("getGroupMembers", groupname, Group.class, session);
		if (group == null) {
			log.debug("group " + groupname + " does not exist");
			group = GroupUtils.getInvalidGroup();
			group.setUsers(Collections.<User> emptyList());
			return group;
		}

		final int groupId = this.getGroupByName(groupname, session).getGroupId();
		final Privlevel privlevel = this.getPrivlevelForGroup(groupId, session);
		// remove members as necessary
		switch (privlevel) {
		case MEMBERS:
			// if the user isn't a member of the group he can't see other
			// members -> and we'll fall through to HIDDEN
			if (this.isUserInGroup(authUser, groupname, session)) break;
		case HIDDEN:
			group.setUsers(Collections.<User> emptyList());
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
	 * Returns true if the user is in the group otherwise false.
	 */
	private boolean isUserInGroup(final String username, final String groupname, final DBSession session) {
		final List<Group> userGroups = this.getGroupsForUser(username, session);
		for (final Group group : userGroups) {
			if (groupname.equals(group.getName())) return true;
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
		return this.queryForList("getGroupsForUser", username, Group.class, session);
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
		if (removeSpecialGroups) {
			return this.removeSpecialGroups(this.getGroupsForUser(userName, session));
		}
		return this.getGroupsForUser(userName, session);
	}

	/**
	 * Helper function to remove special groups from a List of groups
	 * 
	 * @param groups a list of groups 
	 * @return a new list of groups with special groups removed
	 */
	public List<Group> removeSpecialGroups(final List<Group> groups) {
		final ArrayList<Group> newGroups = new ArrayList<Group>();
		for (final Group group : groups) {
			if (!GroupID.isSpecialGroupId(group.getGroupId()))
				newGroups.add(group);
		}
		return newGroups;
	}

	/**
	 * Gets all groups in which both user A and user B are in. 
	 * 
	 * @param userNameA - name of the first user.
	 * @param userNameB - name of the second user.
	 * @param session
	 * @return The list of groups both given users are in.
	 */
	public List<Group> getCommonGroups(final String userNameA, final String userNameB, final DBSession session) {
		final List<Group> userAGroups = this.getGroupsForUser(userNameA, true, session);
		final List<Group> userBGroups = this.getGroupsForUser(userNameB, true, session);

		/*
		 * It is not very efficient, to do this in two cascaded loops, but users
		 * are typically in very few groups and with linked lists there is
		 * probably no much more efficient way to do it.
		 */
		final List<Group> commonGroups = new LinkedList<Group>();
		for (final Group a : userAGroups) {
			for (final Group b : userBGroups) {
				if (a.getGroupId() == b.getGroupId()) {
					commonGroups.add(a);
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
		if (present(userName) == false) return new ArrayList<Integer>();
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
		if (present(groupname) == false) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "groupname isn't set");
		}
		try {
			final GroupID specialGroup = GroupID.getSpecialGroup(groupname);
			if (specialGroup != null) {
				return specialGroup.getId();
			}
		} catch (IllegalArgumentException ignore) {
			// do nothing - this simply means that the given group is not a special group
		}

		final Group group = new Group();
		group.setName(groupname);
		if (present(username)) group.setUsers(Arrays.asList(new User(username)));

		final Integer rVal = this.queryForObject("getGroupIdByGroupNameAndUserName", group, Integer.class, session);
		if (rVal == null) return GroupID.INVALID.getId();
		return rVal;
	}

	/**
	 * Returns group name for a given group id
	 * 
	 * @param groupID
	 * @param session a db session
	 * @return groupName if group exists, null otherwise
	 */
	public String getGroupNameByGroupId(final int groupID, final DBSession session) {
		return this.queryForObject("getGroupNameByGroupId", groupID, String.class, session);
	}

	/**
	 * Stores a group in the database.
	 * 
	 * FIXME: update isn't implemented.
	 * 
	 * @param group 
	 * @param update 
	 * @param session 
	 */
	public void storeGroup(final Group group, final boolean update, final DBSession session) {
		if (update) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Not implemented yet");
		}

		// check if a user exists with that name
		if (this.userDb.getUserDetails(group.getName(), session).getName() == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "There's no user with this name - can't create a group with this name");
		}
		// check if a group exists with that name
		if (this.getGroupByName(group.getName(), session) != null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "There's already a group with this name ('" + group.getName() + "')");
		}

		this.insertGroup(group, session);
	}

	/**
	 * Inserts a group.
	 */
	private void insertGroup(final Group group, final DBSession session) {
		final int newGroupId = this.getNewGroupId(session);
		group.setGroupId(newGroupId);
		this.insert("insertGroup", group, session);
		this.addUserToGroup(group.getName(), group.getName(), session);
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
		if (group == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + groupname + "') doesn't exist");
		}
		if(tagset.getSetName().length() == 0 || tagset.getTags().size() == 0){
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
		Group group = this.getGroupByName(groupname, session);
		if (group == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + groupname + "') doesn't exist");
		}
		TagSet tagset = this.getTagSetBySetNameAndGroup(setName, group.getGroupId(), session);
		if(tagset == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "TagSet ('" + setName + "') doesn't exist for group ('" + groupname + "')");
		}
		TagSetParam param = new TagSetParam();
		param.setSetName(setName);
		param.setGroupId(group.getGroupId());
		this.delete("deleteTagSet", param, session);
	}
	
	/**
	 * Update an existing TagSet
	 * 
	 * @param tagset
	 * @param groupname
	 * @param session
	 */
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
		return this.queryForObject("getNewGroupId", null, Integer.class, session);
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
		}
		this.delete("deleteGroup", group.getGroupId(), session);
		this.delete("removeAllUserFromGroup", group.getGroupId(), session);
	}

	
	/**
	 * Adds a user to a group.
	 * 
	 * @param groupname 
	 * @param username 
	 * @param session 
	 */
	public void addUserToGroup(final String groupname, final String username, final DBSession session) {
		// check if a user exists with that name
		if (this.userDb.getUserDetails(username, session).getName() == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "There's no user with this name ('" + username + "')");
		}
		// make sure that the group exists
		final Group group = this.getGroupByName(groupname, session);
		if (group == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + groupname + "') doesn't exist - can't add user to nonexistent group");
		}
		// make sure that the user isn't a member of the group
		if (this.isUserInGroup(username, groupname, session)) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User ('" + username + "') is already a member of this group ('" + groupname + "')");
		}
		// XXX: the next line is semantically incorrect
		group.setName(username);
		this.insert("addUserToGroup", group, session);
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
		if (this.isUserInGroup(username, groupname, session) == false) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User ('" + username + "') isn't a member of this group ('" + groupname + "')");
		}
		// XXX: the next line is semantically incorrect
		group.setName(username);
		
		this.plugins.onRemoveUserFromGroup(username, group.getGroupId(), session);
		this.delete("removeUserFromGroup", group, session);
	}
}