/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import java.net.URL;

import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 *
 * @author niebler
 */
public class GroupSettingsPageCommand extends TabsCommand<Object> {

	// tabs
	public final static int GROUP_SETTINGS = 0;
	public final static int MEMBER_LIST_IDX = 1;
	public final static int DELETE_GROUP = 3;
	public final static int CV_IDX = 2;

	// general attributes
	private Group group;
	private User user;
	private User loggedinUser;
	private String requestedGroup;
	private boolean userSharedDocuments;

	private GroupRole groupRole;
	private GroupMembership groupMembership;

	private String username;
	// TODO: WHat's this for?
	private String groupname;

	// group specific settings. maybe move them to another page?
	private String realname;
	private URL homepage;
	private String description;
	
	// TODO should be Privlevel type
	private int privlevel;
	// TODO: boolean type
	private int sharedDocuments;
	private int allowJoin;
	
	// specific settings for the group user
	private CommonsMultipartFile file;

	// cv settings
	private String wikiText;
	private String renderedWikiText;

	// bla
	private GroupUpdateOperation operation;
	
	// tmp error message from URL
	private String errorMessage;

	/**
	 * delete the group yes or no
	 */
	private String delete = null;
	
	/**
	 * new profile picture file to upload
	 */
	private MultipartFile pictureFile;

	/**
	 * flag to indicate whether a prior uploaded picture file shall be deleted
	 */
	private boolean deletePicture;
	
	
	/**
	 * TODO: remove after setting the default value of selTab to null
	 */
	public GroupSettingsPageCommand() {
		this.setSelTab(null);
	}

	public boolean isUserSharedDocuments() {
		return userSharedDocuments;
	}

	public void setUserSharedDocuments(boolean userSharedDocuments) {
		this.userSharedDocuments = userSharedDocuments;
	}

	public GroupRole getGroupRole() {
		return groupRole;
	}

	public void setGroupRole(GroupRole groupRole) {
		this.groupRole = groupRole;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public User getLoggedinUser() {
		return loggedinUser;
	}

	public void setLoggedinUser(User loggedinUser) {
		this.loggedinUser = loggedinUser;
	}

	public String getRequestedGroup() {
		return requestedGroup;
	}

	public void setRequestedGroup(String requestedGroup) {
		this.requestedGroup = requestedGroup;
	}

	public int getPrivlevel() {
		return privlevel;
	}

	public void setPrivlevel(int privlevel) {
		this.privlevel = privlevel;
	}

	public int getSharedDocuments() {
		return sharedDocuments;
	}

	public void setSharedDocuments(int sharedDocuments) {
		this.sharedDocuments = sharedDocuments;
	}
	
	public int getAllowJoin() {
		return this.allowJoin;
	}

	public void setAllowJoin(int allowJoin) {
		this.allowJoin = allowJoin;
	}

	public GroupMembership getGroupMembership() {
		return groupMembership;
	}

	public void setGroupMembership(GroupMembership groupMembership) {
		this.groupMembership = groupMembership;
	}

	public String getWikiText() {
		return wikiText;
	}

	public void setWikiText(String wikiText) {
		this.wikiText = wikiText;
	}

	public String getRenderedWikiText() {
		return renderedWikiText;
	}

	public void setRenderedWikiText(String renderedWikiText) {
		this.renderedWikiText = renderedWikiText;
	}

	public CommonsMultipartFile getFile() {
		return file;
	}

	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}

	public GroupUpdateOperation getOperation() {
		return operation;
	}

	public void setOperation(GroupUpdateOperation operation) {
		this.operation = operation;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public URL getHomepage() {
		return homepage;
	}

	public void setHomepage(URL homepage) {
		this.homepage = homepage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	
	public String getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorMessage(String errormessage) {
		this.errorMessage = errormessage;
	}
	
	public String getDelete() {
		return this.delete;
	}

	public void setDelete(String delete) {
		this.delete = delete;
	}

	/**
	 * Returns picture file to upload.
	 * @return picture file as MultipartFile
	 */
	public MultipartFile getPictureFile() {
		return this.pictureFile;
	}

	/**
	 * Sets picture file to upload.
	 * @param pictureFile : picture file as MultipartFile
	 */
	public void setPictureFile(MultipartFile pictureFile) {
		this.pictureFile = pictureFile;
	}

	/**
	 * Checks whether a prior uploaded picture file shall be deleted.
	 * @return flag as boolean
	 */
	public boolean isDeletePicture() {
		return this.deletePicture;
	}
	
	/**
	 * Checks whether a prior uploaded picture file shall be deleted.
	 * @return flag as boolean
	 */
	public boolean getDeletePicture() {
		return this.deletePicture;
	}

	/**
	 * Sets whether a prior uploaded picture file shall be deleted.
	 * @param deletePicture flag as boolean
	 */
	public void setDeletePicture(boolean deletePicture) {
		this.deletePicture = deletePicture;
	}
}
