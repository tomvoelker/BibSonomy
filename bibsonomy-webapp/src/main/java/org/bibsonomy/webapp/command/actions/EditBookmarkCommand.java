package org.bibsonomy.webapp.command.actions;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.recommender.RecommendedTag;
import org.bibsonomy.webapp.command.PostCommand;

/**
 * @author fba
 * @version $Id$
 */
public class EditBookmarkCommand extends PostCommand {
	/**
	 * The tags of the copied post.
	 */
	private List<Tag> copytags;
	
	private Post<Bookmark> post;
	private String tags ;
	private boolean jump = false;
	
	private Post<Bookmark> diffPost;
	
	private String intraHashToUpdate;
	
	
	/**
	 * The abstract (or general) group of the post:
	 * public, private, or other 
	 */
	private String abstractGrouping;
	
	private List<String> groups;
	private List<Tag> relevantGroups;
	private SortedSet<RecommendedTag> recommendedTags;

	private Map<String,Map<String,List<String>>> relevantTagSets;
	
	// string holding ajax requests -- e.g. getRecommendedTags
	private String ajax;
	// string holding an ajax response -- e.g. recommended tags
	private String responseString;
	
	public Post<Bookmark> getPost() {
		return this.post;
	}

	public void setPost(Post<Bookmark> post) {
		this.post = post;
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
	
	public SortedSet<RecommendedTag> getRecommendedTags() {
		return this.recommendedTags;
	}

	public void setRecommendedTags(SortedSet<RecommendedTag> recommendedTags) {
		this.recommendedTags = recommendedTags;
	}

	public Map<String, Map<String, List<String>>> getRelevantTagSets() {
		return this.relevantTagSets;
	}

	public void setRelevantTagSets(Map<String, Map<String, List<String>>> relevantTagSets) {
		this.relevantTagSets = relevantTagSets;
	}

	public void setJump(boolean jump) {
		this.jump = jump;
	}

	public boolean isJump() {
		return jump;
	}
		
	/**
	 * Sets the URL of the post. 
	 * Needed for the (old) postBookmark button and "copy" links. 
	 *  
	 * @param url 
	 */
	public void setUrl(final String url){
		this.post.getResource().setUrl(url);
	}
	
	/**
	 * Sets the title of a post.
	 * Needed for the (old) postBookmark button and "copy" links.
	 * 
	 * @param title
	 */
	public void setDescription(final String title){
		this.post.getResource().setTitle(title);
	}
	
	/**
	 * Sets the description of a post.
	 * Needed for the (old) postBookmark button and "copy" links.
	 * 
	 * @param description
	 */
	public void setExtended(final String description){
		this.post.setDescription(description);
	}
	
	/** Sets the tags from the copied post.
	 * Needed for the (old) "copy" links.
	 * 
	 * @param tags
	 */
	public void setCopytag(final String tags){
		final String[] splittedTags = tags.split("\\s");
		for (final String tagname: splittedTags){
			this.copytags.add(new Tag(tagname));
		}
	}
	
	public List<Tag> getCopytags(){
		return this.copytags;
	}
	
	public void setCopytags(final List<Tag> tags){
		this.copytags = tags;
	}

	public String getAbstractGrouping() {
		return this.abstractGrouping;
	}

	public void setAbstractGrouping(String abstractGrouping) {
		this.abstractGrouping = abstractGrouping;
	}

	public Post<Bookmark> getDiffPost() {
		return this.diffPost;
	}

	public void setDiffPost(Post<Bookmark> diffPost) {
		this.diffPost = diffPost;
	}

	public String getIntraHashToUpdate() {
		return this.intraHashToUpdate;
	}

	public void setIntraHashToUpdate(String intraHashToUpdate) {
		this.intraHashToUpdate = intraHashToUpdate;
	}

	public void setAjax(String ajax) {
		this.ajax = ajax;
	}

	public String getAjax() {
		return ajax;
	}

	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}

	public String getResponseString() {
		return responseString;
	}
}
