package org.bibsonomy.webapp.command;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 *
 * @author niebler
 */
public class GroupSettingsPageCommand extends TabsCommand<Object>  {
	
	// tabs
	public final static int USERS_IDX = 0;
	public final static int MY_PROFILE_IDX = 1;
	public final static int CV_IDX = 5;
	
	// general attributes
	private Group group;
	// this is the hidden group user!
	private User user;
	private User loggedinUser;
	private String requestedGroup;
	private GroupMembership groupMembership;
	
	// group specific settings. maybe move them to another page?
	private String username;
	private int privlevel;
	private int sharedDocuments;
	
	// specific settings for the group user
	private CommonsMultipartFile file;
	
	// cv settings
	private String wikiText;
	private String renderedWikiText;
	
	public GroupSettingsPageCommand() {
		this.addTab(MY_PROFILE_IDX, "navi.groupsettings");
		this.addTab(USERS_IDX, "navi.myprofile");
		this.addTab(CV_IDX, "navi.cvedit");
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
	
	public GroupMembership getGroupMembership() {
		return groupMembership;
	}

	public void setGroupMembership(GroupMembership groupMembership) {
		this.groupMembership = groupMembership;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
}
