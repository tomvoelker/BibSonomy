package org.bibsonomy.webapp.command;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;

/**
 * Command class for encapsulating sphere related models
 * 
 * TODO: this is a merge of two parameter classes and thus needs some cleanup 
 */
public class SphereResourceViewCommand extends UserResourceViewCommand {
	
	private String 			sphereName 	= "";
	private List<User> 		relatedUsers;
	
	private Map<String, Set<User>> spheres;
	private Map<String, ListCommand<Post<Bookmark>>> spheresBMPosts;
	private Map<String, ListCommand<Post<BibTex>>> spheresPBPosts;
	private Map<String, TagCloudCommand> spheresTagClouds;

	
	/**
	 * @return the relatedUsers
	 */
	public List<User> getRelatedUsers() {
		return this.relatedUsers;
	}
	/**
	 * @param relatedUsers the relatedUsers to set
	 */
	public void setRelatedUsers(List<User> relatedUsers) {
		this.relatedUsers = relatedUsers;
	}
	/**
	 * @param spheres
	 */
	public void setSpheres(Map<String, Set<User>> spheres) {
		this.spheres = spheres;
	}

	/**
	 * @return spheres
	 */
	public Map<String, Set<User>> getSpheres() {
		return spheres;
	}

	/**
	 * @param spheresBMPosts
	 */
	public void setSpheresBMPosts(Map<String, ListCommand<Post<Bookmark>>> spheresBMPosts) {
		this.spheresBMPosts = spheresBMPosts;
	}

	/**
	 * @return spheresBMPosts
	 */
	public Map<String, ListCommand<Post<Bookmark>>> getSpheresBMPosts() {
		return spheresBMPosts;
	}

	/**
	 * @param spheresPBPosts
	 */
	public void setSpheresPBPosts(Map<String, ListCommand<Post<BibTex>>> spheresPBPosts) {
		this.spheresPBPosts = spheresPBPosts;
	}

	/**
	 * @return spheresPBPosts
	 */
	public Map<String, ListCommand<Post<BibTex>>> getSpheresPBPosts() {
		return spheresPBPosts;
	}

	/**
	 * @param spheresTagClouds
	 */
	public void setSpheresTagClouds(Map<String, TagCloudCommand> spheresTagClouds) {
		this.spheresTagClouds = spheresTagClouds;
	}

	/**
	 * @return spheresTagClouds
	 */
	public Map<String, TagCloudCommand> getSpheresTagClouds() {
		return spheresTagClouds;
	}
	/**
	 * @return the sphereName
	 */
	public String getSphereName() {
		return this.sphereName;
	}
	/**
	 * @param sphereName the sphereName to set
	 */
	public void setSphereName(String sphereName) {
		this.sphereName = sphereName;
	}

}
