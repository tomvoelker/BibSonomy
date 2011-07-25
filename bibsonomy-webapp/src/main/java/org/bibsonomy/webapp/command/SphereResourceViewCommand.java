package org.bibsonomy.webapp.command;

import java.util.Map;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;

/**
 * Command class for encapsulating aspect related models
 */
public class SphereResourceViewCommand extends FriendsResourceViewCommand {
	private Map<String, Set<User>> aspects;
	private Map<String, ListCommand<Post<Bookmark>>> aspectsBMPosts;
	private Map<String, ListCommand<Post<BibTex>>> aspectsPBPosts;
	private Map<String, TagCloudCommand> aspectsTagClouds;
	
	public void setAspects(Map<String, Set<User>> aspects) {
		this.aspects = aspects;
	}

	public Map<String, Set<User>> getAspects() {
		return aspects;
	}

	public void setAspectsBMPosts(Map<String, ListCommand<Post<Bookmark>>> aspectsBMPosts) {
		this.aspectsBMPosts = aspectsBMPosts;
	}

	public Map<String, ListCommand<Post<Bookmark>>> getAspectsBMPosts() {
		return aspectsBMPosts;
	}

	public void setAspectsPBPosts(Map<String, ListCommand<Post<BibTex>>> aspectsPBPosts) {
		this.aspectsPBPosts = aspectsPBPosts;
	}

	public Map<String, ListCommand<Post<BibTex>>> getAspectsPBPosts() {
		return aspectsPBPosts;
	}

	public void setAspectsTagClouds(Map<String, TagCloudCommand> aspectsTagClouds) {
		this.aspectsTagClouds = aspectsTagClouds;
	}

	public Map<String, TagCloudCommand> getAspectsTagClouds() {
		return aspectsTagClouds;
	}

}
