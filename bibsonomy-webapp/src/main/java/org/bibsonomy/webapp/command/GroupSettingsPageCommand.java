package org.bibsonomy.webapp.command;

import java.net.URL;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 *
 * @author niebler
 */
public class GroupSettingsPageCommand extends TabsCommand<Object> {

	// tabs
	public final static int MY_PROFILE_IDX = 0;
	public final static int USERS_IDX = 1;
	public final static int CV_IDX = 5;

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
	private int privlevel;
	private int sharedDocuments;

	// specific settings for the group user
	private CommonsMultipartFile file;

	// cv settings
	private String wikiText;
	private String renderedWikiText;

	// bla
	private GroupUpdateOperation operation;

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
}
