package org.bibsonomy.webapp.command;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.SettingPageMsg;
import org.bibsonomy.model.User;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author Steffen Kress
 * @version $Id$
 */
//TODO
public class SettingsViewCommand extends TabsCommand<Object> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1211293063812357398L;
	
	private static final Log log = LogFactory.getLog(SettingsViewCommand.class);
	
	/** Indexes of definded tabs */
	
	public final static int MY_PROFILE_IDX = 0;
	public final static int SETTINGS_IDX = 1;
	public final static int IMPORTS_IDX = 2;
	
	/**
	 * action can be logging, api or layoutTagPost
	 * this three types determine the different possible actions which will be handled 
	 * by this controller for the settings.settings site
	 */
	String action;
	
	private User user;
	
	private boolean hasOwnGroup;
	
	private int statusID = SettingPageMsg.IDLE.getId();
	
	private List<User> friendsOfUser;
	
	private List<User> userFriends;
	
	private String group;
	
	private String importType;
	
	private boolean overwrite;
	
	private CommonsMultipartFile file;
	
	private int privlevel;
	
	private int sharedDocuments;
	
	private boolean confirmDelete;
	
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
	 * name of the begin layout file
	 */
	private String beginName = null;
	
	/**
	 * hash of the begin layout file
	 */
	private String beginHash = null;
	
	/**
	 * name of the item layout file
	 */
	private String itemName = null;
	
	/**
	 * hash of the begin layout file
	 */
	private String itemHash = null;
	
	/**
	 * name of the end layout file
	 */
	private String endName = null;
	
	/**
	 * hash of the end layout file
	 */
	private String endHash = null;
	
	/**
	 * Constructor.
	 */
	public SettingsViewCommand() {
		addTab(MY_PROFILE_IDX, "navi.myprofile");
		addTab(SETTINGS_IDX, "navi.settings");
		addTab(IMPORTS_IDX, "navi.imports");
		setSelTab(MY_PROFILE_IDX);
	}

	public String getGroup() {
		return this.group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getImportType() {
		return this.importType;
	}

	public void setImportType(String importType) {
		this.importType = importType;
	}
	
	public boolean getOverwrite() {
		return this.overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public CommonsMultipartFile getFile() {
		return this.file;
	}

	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}

	public Map<String, String> getNewBookmarks() {
		return this.newBookmarks;
	}

	public Map<String, String> getUpdatedBookmark() {
		return this.updatedBookmarks;
	}

	public List<String> getNonCreatedBookmark() {
		return this.nonCreatedBookmarks;
	}

	public void setNewBookmarks(Map<String, String> newBookmarks) {
		this.newBookmarks = newBookmarks;
	}

	public void setUpdatedBookmarks(Map<String, String> updatedBookmarks) {
		this.updatedBookmarks = updatedBookmarks;
	}

	public void setNonCreatedBookmarks(List<String> nonCreatedBookmarks) {
		this.nonCreatedBookmarks = nonCreatedBookmarks;
	}

	public void setPrivlevel(int privlevel) {
		this.privlevel = privlevel;
	}

	public int getPrivlevel() {
		return privlevel;
	}

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

	public void setBeginName(String beginName) {
		this.beginName = beginName;
	}

	public void setBeginHash(String beginHash) {
		this.beginHash = beginHash;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public void setItemHash(String itemHash) {
		this.itemHash = itemHash;
	}

	public void setEndName(String endName) {
		this.endName = endName;
	}

	public void setEndHash(String endHash) {
		this.endHash = endHash;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}


	
	public void setHasOwnGroup(boolean hasOwnGroup) {
		this.hasOwnGroup = hasOwnGroup;
	}

	public boolean getHasOwnGroup() {
		return hasOwnGroup;
	}

	public void setFriendsOfUser(List<User> friendsOfUser) {
		this.friendsOfUser = friendsOfUser;
	}

	public List<User> getFriendsOfUser() {
		return friendsOfUser;
	}

	public void setUserFriends(List<User> userFriends) {
		this.userFriends = userFriends;
	}

	public List<User> getUserFriends() {
		return userFriends;
	}

	public int getStatusID() {
		return this.statusID;
	}

	public void setStatusID(int statusID) {
		this.statusID = statusID;
	}

	public boolean isConfirmDelete() {
		return this.confirmDelete;
	}

	public void setConfirmDelete(boolean confirmDelete) {
		this.confirmDelete = confirmDelete;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getOldPassword() {
		return this.oldPassword;
	}

	public String getNewPassword() {
		return this.newPassword;
	}

	public String getNewPasswordRetype() {
		return this.newPasswordRetype;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public void setNewPasswordRetype(String newPasswordRetype) {
		this.newPasswordRetype = newPasswordRetype;
	}
	
	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}