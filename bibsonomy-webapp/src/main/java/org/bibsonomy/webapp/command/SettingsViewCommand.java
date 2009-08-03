package org.bibsonomy.webapp.command;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	
	private boolean hasOwnGroup;
	
	private List<User> friendsOfUser;
	
	private List<User> userFriends;
	
	private String grouping;
	
	private String importType;
	
	private boolean overwrite;
	
	private CommonsMultipartFile file;
	
	private int tagboxStyle;
	
	private int tagSort;
	
	private int tagboxTooltip;
	
	private String defaultLanguage;
	
	private int tagboxMinfreq;
	
	private int itemcount;
	
	private int privlevel;
	
	private int sharedDocuments;
	
	private int logLevel;
	
	private String oldPassword;
	
	private String newPassword1;
	
	private String newPassword2;

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

	public String getGrouping() {
		return this.grouping;
	}

	public void setGrouping(String grouping) {
		this.grouping = grouping;
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

	public void setTagboxStyle(int tagboxStyle) {
		this.tagboxStyle = tagboxStyle;
	}

	public int getTagboxStyle() {
		return tagboxStyle;
	}

	public CommonsMultipartFile getFile() {
		return this.file;
	}

	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}

	public void setTagSort(int tagSort) {
		this.tagSort = tagSort;
	}

	public int getTagSort() {
		return tagSort;
	}

	public void setTagboxTooltip(int toolTip) {
		this.tagboxTooltip = toolTip;
	}

	public int getTagboxTooltip() {
		return tagboxTooltip;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setTagboxMinfreq(int tagboxMinfreq) {
		this.tagboxMinfreq = tagboxMinfreq;
	}

	public int getTagboxMinfreq() {
		return tagboxMinfreq;
	}

	public void setItemcount(int itemcount) {
		this.itemcount = itemcount;
	}

	public int getItemcount() {
		return itemcount;
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

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	public int getLogLevel() {
		return logLevel;
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

	/**
	 * Returns current password of user as typed into form.
	 * 
	 * @return
	 */
	public String getOldPassword() {
		return oldPassword;
	}

	public void setNewPassword1(String newPassword1) {
		this.newPassword1 = newPassword1;
	}

	/**
	 * Returns the new password of user as typed into form.
	 * 
	 * @return
	 */
	public String getNewPassword1() {
		return newPassword1;
	}

	public void setNewPassword2(String newPassword2) {
		this.newPassword2 = newPassword2;
	}

	/**
	 * Returns the new password of user as typed into form to confirm <code>newPassword1</code>.
	 * 
	 * @return
	 */
	public String getNewPassword2() {
		return newPassword2;
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

}