/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.layout.csl.CSLStyle;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Layout;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.User;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthConsumerInfo;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthUserInfo;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author Steffen Kress
 */
@Getter
@Setter
public class SettingsViewCommand extends TabsCommand<Object> implements Serializable {
	private static final long serialVersionUID = -1211293063812357398L;
	
	/** Indexes of defined tabs */
	public final static int MY_PROFILE_IDX = 0;
	public final static int SETTINGS_IDX = 1;
	public final static int LAYOUT_IDX = 2;
	public final static int GROUP_IDX = 3;
	public final static int SYNC_IDX = 4;
	public final static int CV_IDX = 5;
	public final static int OAUTH_IDX = 6;
	public final static int PERSON_IDX = 7;
	
	private static final String TAB_URL = "/settings";
	
	
	/**
	 * action can be logging, api, layoutTagPost, personSettings
	 * this four types determine the different possible actions which will be handled
	 * by this controller for the settings.settings site
	 */
	private String action;
	
	/** An operation to update a specific group. */
	private GroupUpdateOperation operation;

	/** the AccessToken you want to delete */
	private String accessTokenDelete;

	/** The OAuth informations about the User */
	private List<OAuthUserInfo> oauthUserInfo;
	
	/** List of all valid OAuth consumers */
	private List<OAuthConsumerInfo> consumerInfo;
	
	/** current user */
	private User user;

	/** the person of the current user */
	private Person person;
	
	private boolean hasOwnGroup;
	
	/** number of the new maxCount/minFreq */
	private int changeTo;
	
	/** users which added the current login user in their friend list */
	private List<User> friendsOfUser;
	
	/** list with friends of the current login user */
	private List<User> userFriends;

	private String importType;
	
	private boolean overwrite;

	/** jabref file */
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
	
	/** current password of user */
	private String oldPassword = null;
	
	private String newPassword = null;
	
	private String newPasswordRetype = null;

	private Map<String, String> newBookmarks = null;

	private Map<String, String> updatedBookmarks = null;

	private List<String> nonCreatedBookmarks = null;
	
	/** name of the jabref begin layout file */
	private String beginName = null;
	
	/** hash of the jabref begin layout file */
	private String beginHash = null;
	
	/** name of the jabref item layout file */
	private String itemName = null;
	
	/** hash of the jabref begin layout file */
	private String itemHash = null;
	
	/** name of the jabref end layout file */
	private String endName = null;
	
	/** hash of the jabref end layout file */
	private String endHash = null;
	
	/** name of the csl layout file */
	private String cslName = null;
	
	/** hash of the csl layout file */
	private String cslHash = null;

	/** delete the account yes or no */
	private String delete = null;

	private String importData;
	
	private List<SyncService> syncServer;
	private SyncService newSyncServer;

	/** The available synchronization clients. */
	private List<SyncService> availableSyncClients;
	private List<SyncService> availableSyncServers;
	
	/** this field contains the username of the user, who should be added/removed to/from the group. */
	private String username;
	
	/** login credentials for importing bookmarks from delicious */
	private String importUsername;
	private String importPassword;
	
	/* Stuff for CVWiki settings */
	private boolean isGroup = false;
	private String wikiText;
	private String renderedWikiText;
	
	/** new profile picture file to upload */
	private MultipartFile picturefile;

	/** flag to indicate whether a prior uploaded picture file shall be deleted */
	private boolean deletePicture;

	/** the jabref file to import **/
	private CommonsMultipartFile fileBegin;
	/** the jabref file to import **/
	private CommonsMultipartFile fileItem;
	/** the jabref file to import **/
	private CommonsMultipartFile fileEnd;
	
	/** the csl file to import **/
	private CommonsMultipartFile cslFile;
	
	/** list of all csl layout files of the user */
	private List<CSLStyle> cslFiles = null;

	/** All CSL styles the system offers */
	private List<CSLStyle> personPageCslFiles = null;


	private Map<String, Layout> layoutMap;
	private Map<String, CSLStyle> cslLayoutMap;
	private Map<String, CSLStyle> customCslLayoutMap;
	

	/**
	 * Constructor.
	 */
	public SettingsViewCommand() {
		this.addTab(MY_PROFILE_IDX, "navi.myprofile");
		this.addTab(SETTINGS_IDX, "navi.settings");
		this.addTab(LAYOUT_IDX, "settings.layoutfiles");
		this.addTab(CV_IDX, "navi.cvedit");
		this.addTab(OAUTH_IDX, "navi.oauth.consumers");
		this.addTab(GROUP_IDX, "navi.groups");
		// OAuth tab added in SettingsPageController.java
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
	 * shows the person tab for users with a linked person
	 * @param person
	 */
	public void showPersonTab(Person person) {
		if (present(person)) {
			this.addTab(PERSON_IDX, "navi.person");
		}
	}

	/**
	 * @return the isGroup
	 */
	public boolean getIsGroup() {
		return this.isGroup;
	}

	public void setIsGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}
}