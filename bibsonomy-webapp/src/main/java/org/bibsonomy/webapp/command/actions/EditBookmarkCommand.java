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
	private List<Group> groups;
	private List<String> relevantTags;
	private List<Tag> relevantGroups;
	private List<String> recommendedTags;
	private GroupID groupIds;
	private Map<String,Map<String,List<String>>> relevantTagSets;
	//private TagCloudCommand tagcloud = new TagCloudCommand();
	
	public EditBookmarkCommand() {
		postBookmark = new Post<Bookmark>();
		postBookmark.setResource(new Bookmark());
		groups = new ArrayList<Group>();
		relevantTags = new ArrayList<String>();
		relevantGroups = new ArrayList<Tag>();
		relevantTagSets = new HashMap<String, Map<String,List<String>>>();
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

	public List<Group> getGroups() {
		return this.groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<String> getRelevantTags() {
		return this.relevantTags;
	}

	public void setRelevantTags(List<String> relevantTags) {
		this.relevantTags = relevantTags;
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

	public Map<String, Map<String, List<String>>> getRelevantTagSets() {
		return this.relevantTagSets;
	}

	public void setRelevantTagSets(Map<String, Map<String, List<String>>> relevantTagSets) {
		this.relevantTagSets = relevantTagSets;
	}
	
	
}
