package org.bibsonomy.webapp.command.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.PostCommand;

/**
 * @author fba
 * @version $Id$
 */
public class EditBookmarkCommand extends PostCommand {
	private boolean userLoggedIn;
	private Post<Bookmark> postBookmark;
	private String tags ;
	
	private List<String> groups;
	private List<Tag> relevantGroups;
	private List<String> recommendedTags;
	private List<Group> groupDetails;
	//private TagCloudCommand tagcloud = new TagCloudCommand();
	
	public EditBookmarkCommand() {
		postBookmark = new Post<Bookmark>();
		postBookmark.setResource(new Bookmark());

		groups = new ArrayList<String>();
		relevantGroups = new ArrayList<Tag>();
		groupDetails = new ArrayList<Group>();
	}

	public Post<Bookmark> getPostBookmark() {
		return this.postBookmark;
	}

	public void setUserLoggedIn(boolean userLoggedIn) {
		this.userLoggedIn = userLoggedIn;
	}

	public boolean isUserLoggedIn() {
		return userLoggedIn;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTags() {
		return tags;
	}


	public List<String> getGroups() {
		return this.groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public List<Tag> getRelevantGroups() {
		return this.relevantGroups;
	}

	public void setRelevantGroups(List<Tag> relevantGroups) {
		this.relevantGroups = relevantGroups;
	}
	
	public List<String> getRecommendedTags() {
		return this.recommendedTags;
	}

	public void setRecommendedTags(List<String> recommendedTags) {
		this.recommendedTags = recommendedTags;
	}

	public List<Group> getGroupDetails() {
		return this.groupDetails;
	}

	public void setGroupDetails(List<Group> groupDetails) {
		this.groupDetails = groupDetails;
	}

	
	
}
