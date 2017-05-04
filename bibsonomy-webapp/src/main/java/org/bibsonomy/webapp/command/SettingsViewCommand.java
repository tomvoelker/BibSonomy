/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.layout.csl.CSLStyle;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthConsumerInfo;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthUserInfo;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author Steffen Kress
 */
public class SettingsViewCommand extends TabsCommand<Object> implements Serializable {
	private static final long serialVersionUID = -1211293063812357398L;
	
	/** Indexes of defined tabs */
	public final static int MY_PROFILE_IDX = 0;
	public final static int SETTINGS_IDX = 1;
	public final static int JABREF_IDX = 2;
	public final static int GROUP_IDX = 3;
	public final static int SYNC_IDX = 4;
	public final static int CV_IDX = 5;
	public final static int OAUTH_IDX = 6;
	public final static int CSL_IDX = 7;
	
	private static final String TAB_URL = "/settings";
	
	
	/**
	 * action can be logging, api or layoutTagPost
	 * this three types determine the different possible actions which will be handled 
	 * by this controller for the settings.settings site
	 */
	private String action;
	
	/** An operation to update a specific group. */
	private GroupUpdateOperation operation;

	/**
	 * the AccessToken you want to delete
	 */
	
	private String accessTokenDelete;
	/**
	 * The OAuth informations about the User
	 */
	private List<OAuthUserInfo> oauthUserInfo;
	
	/**
	 * List of all valid OAuth consumers
	 */
	private List<OAuthConsumerInfo> consumerInfo;
	
	/**
	 * current user
	 */
	private User user;
	
	private boolean hasOwnGroup;
	
	/**
	 * number of the new maxCount/minFreq
	 */
	private int changeTo;
	
	/**
	 * users which added the current login user in their friend list 
	 */
	private List<User> friendsOfUser;
	
	/**
	 * list with friends of the current login user
	 */
	private List<User> userFriends;
		
	private String importType;
	
	private boolean overwrite;
	
	private CommonsMultipartFile file;
	
	/*
	 * settings for groups
	 */
	private int privlevel;
	// TODO: why not boolean?
	private int sharedDocuments;
	private Group group;
	
	private List<Group> groups = new ArrayList<>();
	private List<Group> pendingRequestedgroups = new ArrayList<>();
	// the group to update
	private String groupName;
	
	/**
	 * current password of user
	 */
	private String oldPassword = null;
	
	private String newPassword = null;
	
	private String newPasswordRetype = null;

	private Map<String, String> newBookmarks = null;

	private Map<String, String> updatedBookmarks = null;

	private List<String> nonCreatedBookmarks = null;
	
	/**
	 * name of the jabref begin layout file
	 */
	private String beginName = null;
	
	/**
	 * hash of the jabref begin layout file
	 */
	private String beginHash = null;
	
	/**
	 * name of the jabref item layout file
	 */
	private String itemName = null;
	
	/**
	 * hash of the jabref begin layout file
	 */
	private String itemHash = null;
	
	/**
	 * name of the jabref end layout file
	 */
	private String endName = null;
	
	/**
	 * hash of the jabref end layout file
	 */
	private String endHash = null;
	
	/**
	 * name of the csl layout file
	 */
	private String cslName = null;
	
	/**
	 * hash of the csl layout file
	 */
	private String cslHash = null;
	
	
	/**
	 * delete the account yes or no
	 */
	private String delete = null;

	private String importData;
	
	private List<SyncService> syncServer;
	private SyncService newSyncServer;
	
	private List<SyncService> availableSyncClients;
	private List<SyncService> availableSyncServers;
	
	/** 
	 * this field contains the username of the user, who should be added/removed to/from the group.
	 */
	private String username;
	
	/**
	 * login credentials for importing bookmarks
	 * from delicious
	 */
	private String importUsername;
	private String importPassword;
	
	/* Stuff for CVWiki settings */
	private boolean isGroup = false;
	private String wikiText;
	private String renderedWikiText;
	
	/**
	 * new profile picture file to upload
	 */
	private MultipartFile picturefile;

	/**
	 * flag to indicate whether a prior uploaded picture file shall be deleted
	 */
	private boolean deletePicture;

	/** the jabref file to import **/
	private CommonsMultipartFile fileBegin;
	/** the jabref file to import **/
	private CommonsMultipartFile fileItem;
	/** the jabref file to import **/
	private CommonsMultipartFile fileEnd;
	
	/** the csl file to import **/
	private CommonsMultipartFile cslFile;
	
	/**
	 * list of all csl layout files of the user
	 */
	private List<CSLStyle> cslFiles = null;
	

	/**
	 * Constructor.
	 */
	public SettingsViewCommand() {
		this.addTab(MY_PROFILE_IDX, "navi.myprofile");
		this.addTab(SETTINGS_IDX, "navi.settings");
		this.addTab(JABREF_IDX, "settings.jabRef.layoutfile");
		this.addTab(CSL_IDX, "settings.csl.layoutfile");
		this.addTab(CV_IDX, "navi.cvedit");
		this.addTab(OAUTH_IDX, "navi.oauth.consumers");
		this.addTab(GROUP_IDX, "navi.groups");
		//OAuth tab added in SettingsPageController.java
		this.setSelTab(MY_PROFILE_IDX);
		this.setTabURL(TAB_URL);
	}
	

	/**
	 * shows the sync tab for admins
	 * @param show
	 */
	public void showSyncTab(boolean show) {
		if(show) {
			this.addTab(SYNC_IDX, "navi.sync");
		}
	}
	
	/**
	 * @return importType
	 */
	public String getImportType() {
		return this.importType;
	}

	/**
	 * @param importType
	 */
	public void setImportType(String importType) {
		this.importType = importType;
	}
	
	/**
	 * @return overwrite
	 */
	public boolean isOverwrite() {
		return this.overwrite;
	}

	/**
	 * @param overwrite the overwrite to set
	 */
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	/**
	 * @return the jabref file
	 */
	public CommonsMultipartFile getFile() {
		return this.file;
	}

	/**
	 * @param file the jabref file to set
	 */
	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}

	/**
	 * @return newBookmarks
	 */
	public Map<String, String> getNewBookmarks() {
		return this.newBookmarks;
	}

	/**
	 * @return updatedBookmarks
	 */
	public Map<String, String> getUpdatedBookmark() {
		return this.updatedBookmarks;
	}

	/**
	 * @return nonCreatedBookmarks
	 */
	public List<String> getNonCreatedBookmark() {
		return this.nonCreatedBookmarks;
	}

	/**
	 * @param newBookmarks
	 */
	public void setNewBookmarks(Map<String, String> newBookmarks) {
		this.newBookmarks = newBookmarks;
	}

	/**
	 * @param updatedBookmarks
	 */
	public void setUpdatedBookmarks(Map<String, String> updatedBookmarks) {
		this.updatedBookmarks = updatedBookmarks;
	}

	/**
	 * @param nonCreatedBookmarks
	 */
	public void setNonCreatedBookmarks(List<String> nonCreatedBookmarks) {
		this.nonCreatedBookmarks = nonCreatedBookmarks;
	}

	/**
	 * @param privlevel
	 */
	public void setPrivlevel(int privlevel) {
		this.privlevel = privlevel;
	}

	public int getPrivlevel() {
		return privlevel;
	}

	/**
	 * @param sharedDocuments
	 */
	public void setSharedDocuments(int sharedDocuments) {
		this.sharedDocuments = sharedDocuments;
	}

	public int getSharedDocuments() {
		return sharedDocuments;
	}

	public String getBeginName() {
		return this.beginName;
	}

	public String getBeginHash() {
		return this.beginHash;
	}

	public String getItemName() {
		return this.itemName;
	}

	public String getItemHash() {
		return this.itemHash;
	}

	public String getEndName() {
		return this.endName;
	}

	public String getEndHash() {
		return this.endHash;
	}

	/**
	 * @param beginName for jabref
	 */
	public void setBeginName(String beginName) {
		this.beginName = beginName;
	}

	/**
	 * @param beginHash for jabref 
	 */
	public void setBeginHash(String beginHash) {
		this.beginHash = beginHash;
	}

	/**
	 * @param itemName for jabref 
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * @param itemHash for jabref
	 */
	public void setItemHash(String itemHash) {
		this.itemHash = itemHash;
	}

	/**
	 * @param endName for jabref
	 */
	public void setEndName(String endName) {
		this.endName = endName;
	}

	/**
	 * @param endHash for jabref
	 */
	public void setEndHash(String endHash) {
		this.endHash = endHash;
	}
	
	/**
	 * @param hasOwnGroup
	 */
	public void setHasOwnGroup(boolean hasOwnGroup) {
		this.hasOwnGroup = hasOwnGroup;
	}

	public boolean getHasOwnGroup() {
		return hasOwnGroup;
	}
	
	/**
	 * @return the friendsOfUser
	 */
	public List<User> getFriendsOfUser() {
		return this.friendsOfUser;
	}

	/**
	 * @param friendsOfUser the friendsOfUser to set
	 */
	public void setFriendsOfUser(List<User> friendsOfUser) {
		this.friendsOfUser = friendsOfUser;
	}

	/**
	 * @return the userFriends
	 */
	public List<User> getUserFriends() {
		return this.userFriends;
	}

	/**
	 * @param userFriends the userFriends to set
	 */
	public void setUserFriends(List<User> userFriends) {
		this.userFriends = userFriends;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the oldPassword
	 */
	public String getOldPassword() {
		return this.oldPassword;
	}

	/**
	 * @param oldPassword the oldPassword to set
	 */
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	/**
	 * @return the newPassword
	 */
	public String getNewPassword() {
		return this.newPassword;
	}

	/**
	 * @param newPassword the newPassword to set
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	/**
	 * @return the newPasswordRetype
	 */
	public String getNewPasswordRetype() {
		return this.newPasswordRetype;
	}

	/**
	 * @param newPasswordRetype the newPasswordRetype to set
	 */
	public void setNewPasswordRetype(String newPasswordRetype) {
		this.newPasswordRetype = newPasswordRetype;
	}
	
	/**
	 * @return the action
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the delete
	 */
	public String getDelete() {
		return this.delete;
	}

	/**
	 * @param delete the delete to set
	 */
	public void setDelete(String delete) {
		this.delete = delete;
	}
	
	/**
	 * @return the changeTo
	 */
	public int getChangeTo() {
		return this.changeTo;
	}

	/**
	 * @param changeTo the changeTo to set
	 */
	public void setChangeTo(int changeTo) {
		this.changeTo = changeTo;
	}

	/**
	 * @return the importData
	 */
	public String getImportData() {
		return this.importData;
	}

	/**
	 * @param importData the importData to set
	 */
	public void setImportData(String importData) {
		this.importData = importData;
	}

	/**
	 * @return the group
	 */
	public Group getGroup() {
		return this.group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

	/**
	 * @param syncServer the syncServer to set
	 */
	public void setSyncServer(List<SyncService> syncServer) {
		this.syncServer = syncServer;
	}

	/**
	 * @return the syncServer
	 */
	public List<SyncService> getSyncServer() {
		return syncServer;
	}

	/**
	 * @param availableSyncServers the avlSyncServer to set
	 */
	public void setAvailableSyncServers(List<SyncService> availableSyncServers) {
		this.availableSyncServers = availableSyncServers;
	}

	/**
	 * @return the avlSyncServer
	 */
	public List<SyncService> getAvailableSyncServers() {
		return availableSyncServers;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * @param username
	 */
	public void setUsername(final String username) {
		this.username = username;
	}

	/**
	 * @return The available synchronization clients.
	 */
	public List<SyncService> getAvailableSyncClients() {
		return this.availableSyncClients;
	}

	/**
	 * @param availableSyncClients
	 */
	public void setAvailableSyncClients(final List<SyncService> availableSyncClients) {
		this.availableSyncClients = availableSyncClients;
	}
	
	/**
	 * @return the newSyncServer
	 */
	public SyncService getNewSyncServer() {
		return this.newSyncServer;
	}

	/**
	 * @param newSyncServer the newSyncServer to set
	 */
	public void setNewSyncServer(final SyncService newSyncServer) {
		this.newSyncServer = newSyncServer;
	}
	

	/**
	 * @return the importUsername
	 */
	public String getImportUsername() {
		return this.importUsername;
	}

	/**
	 * @param importUsername the importUsername to set
	 */
	public void setImportUsername(final String importUsername) {
		this.importUsername = importUsername;
	}

	/**
	 * @return the importPassword
	 */
	public String getImportPassword() {
		return this.importPassword;
	}

	/**
	 * @param importPassword the importPassword to set
	 */
	public void setImportPassword(final String importPassword) {
		this.importPassword = importPassword;
	}

	/**
	 * @return the wikiText
	 */
	public String getWikiText() {
		return this.wikiText;
	}

	/**
	 * @param wikiText the wikiText to set
	 */
	public void setWikiText(String wikiText) {
		this.wikiText = wikiText;
	}

	/**
	 * @return the renderedWikiText
	 */
	public String getRenderedWikiText() {
		return renderedWikiText;
	}

	/**
	 * @param renderedWikiText the renderedWikiText to set
	 */
	public void setRenderedWikiText(final String renderedWikiText) {
		this.renderedWikiText = renderedWikiText;
	}

	/**
	 * @return the isGroup
	 */
	public boolean getIsGroup() {
		return this.isGroup;
	}

	/**
	 * @param isGroup the isGroup to set
	 */
	public void setIsGroup(final boolean isGroup) {
		this.isGroup = isGroup;
	}

	/**
	 * @return the oauthUserInfo
	 */
	public List<OAuthUserInfo> getOauthUserInfo() {
		return this.oauthUserInfo;
	}

	/**
	 * @param oauthUserInfo the oauthUserInfo to set
	 */
	public void setOauthUserInfo(final List<OAuthUserInfo> oauthUserInfo) {
		this.oauthUserInfo = oauthUserInfo;
	}

	/**
	 * @return the consumerInfo
	 */
	public List<OAuthConsumerInfo> getConsumerInfo() {
		return this.consumerInfo;
	}

	/**
	 * @param consumerInfo the consumerInfo to set
	 */
	public void setConsumerInfo(final List<OAuthConsumerInfo> consumerInfo) {
		this.consumerInfo = consumerInfo;
	}
	
	/**
	 * Sets picture file to upload.
	 * @param picturefile : picture file as MultipartFile
	 */
	public void setPicturefile(final MultipartFile picturefile ) {
		this.picturefile = picturefile;
	}
	
	/**
	 * Returns picture file to upload.
	 * @return picture file as MultipartFile
	 */
	public MultipartFile getPicturefile() {
		return this.picturefile;
	}

	/**
	 * Sets whether a prior uploaded picture file shall be deleted.
	 * @param deletePicture flag as boolean
	 */
	public void setDeletePicture(final boolean deletePicture) {
		this.deletePicture = deletePicture;
	}
	
	/**
	 * Checks whether a prior uploaded picture file shall be deleted.
	 * @return flag as boolean
	 */
	public boolean getDeletePicture() {
		return this.deletePicture;
	}

	/**
	 * @return the accessTokenDelete
	 */
	public String getAccessTokenDelete() {
		return this.accessTokenDelete;
	}

	/**
	 * @param accessTokenDelete the accessTokenDelete to set
	 */
	public void setAccessTokenDelete(final String accessTokenDelete) {
		this.accessTokenDelete = accessTokenDelete;
	}

	/**
	 * @return the groups
	 */
	public List<Group> getGroups() {
		return this.groups;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
	/**
	 * @return the pendingRequestedgroups
	 */
	public List<Group> getPendingRequestedgroups() {
		return this.pendingRequestedgroups;
	}

	/**
	 * @param pendingRequestedgroups the pendingRequestedgroups to set
	 */
	public void setPendingRequestedgroups(List<Group> pendingRequestedgroups) {
		this.pendingRequestedgroups = pendingRequestedgroups;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return this.groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return the groupUpdateOperation
	 */
	public GroupUpdateOperation getOperation() {
		return operation;
	}

	/**
	 * Sets a group update operation.
	 * @param operation a group update operation
	 */
	public void setOperation(GroupUpdateOperation operation) {
		this.operation = operation;
	}
	/**
	 * @return the fileBegin for jabref
	 */
	public CommonsMultipartFile getFileBegin() {
		return this.fileBegin;
	}

	/**
	 * @param fileBegin the jabref fileBegin to set
	 */
	public void setFileBegin(CommonsMultipartFile fileBegin) {
		this.fileBegin = fileBegin;
	}

	/**
	 * @return the fileItem for jabref
	 */
	public CommonsMultipartFile getFileItem() {
		return this.fileItem;
	}

	/**
	 * @param fileItem the jabref fileItem to set
	 */
	public void setFileItem(CommonsMultipartFile fileItem) {
		this.fileItem = fileItem;
	}

	/**
	 * @return the fileEnd for jabref
	 */
	public CommonsMultipartFile getFileEnd() {
		return this.fileEnd;
	}

	/**
	 * @param fileEnd the jabref fileEnd to set
	 */
	public void setFileEnd(CommonsMultipartFile fileEnd) {
		this.fileEnd = fileEnd;
	}


	/**
	 * @return the cslName
	 */
	public String getCslName() {
		return this.cslName;
	}


	/**
	 * @param cslName the cslName to set
	 */
	public void setCslName(String cslName) {
		this.cslName = cslName;
	}


	/**
	 * @return the cslHash
	 */
	public String getCslHash() {
		return this.cslHash;
	}


	/**
	 * @param cslHash the cslHash to set
	 */
	public void setCslHash(String cslHash) {
		this.cslHash = cslHash;
	}


	/**
	 * @return the cslFile
	 */
	public CommonsMultipartFile getCslFile() {
		return this.cslFile;
	}


	/**
	 * @param cslFile the cslFile to set
	 */
	public void setCslFile(CommonsMultipartFile cslFile) {
		this.cslFile = cslFile;
	}


	/**
	 * @return the cslFiles
	 */
	public List<CSLStyle> getCslFiles() {
		return this.cslFiles;
	}


	/**
	 * @param cslFiles the cslFiles to set
	 */
	public void setCslFiles(List<CSLStyle> cslFiles) {
		this.cslFiles = cslFiles;
	}
}