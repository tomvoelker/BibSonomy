package org.bibsonomy.community.webapp.command;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Tag;

/**
 * @author fba
 * @version $Id$
 * @param <RESOURCE> The type of resource this command handles.
 */
public class EditPostCommand<RESOURCE extends Resource> {
	/**
	 * The tags of the copied post.
	 */
	private List<Tag> copytags;
	
	private Post<RESOURCE> post;
	private String tags ;
	private boolean jump = false;
	
	private Post<RESOURCE> diffPost;
	
	private String intraHashToUpdate;
	
	/**
	 * When the tag field contains commas, it is only accepted, if this boolean is set to <code>true</code> 
	 */
	private boolean acceptComma = false;
	private boolean containsComma = false;
	
	
	/**
	 * The abstract (or general) group of the post:
	 * public, private, or other 
	 */
	private String abstractGrouping;
	
	private List<String> groups;
	
	private List<String> relevantGroups;
	private SortedSet<RecommendedTag> recommendedTags;

	private Map<String,Map<String,List<String>>> relevantTagSets;
	
	/**
	 * stores an id, e.g. for mapping recommendations to posts
	 */
	private int postID;
	
	public Post<RESOURCE> getPost() {
		return this.post;
	}

	public void setPost(Post<RESOURCE> post) {
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

	public List<String> getRelevantGroups() {
		return this.relevantGroups;
	}

	public void setRelevantGroups(List<String> relevantGroups) {
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

	public Post<RESOURCE> getDiffPost() {
		return this.diffPost;
	}

	public void setDiffPost(Post<RESOURCE> diffPost) {
		this.diffPost = diffPost;
	}

	public String getIntraHashToUpdate() {
		return this.intraHashToUpdate;
	}

	public void setIntraHashToUpdate(String intraHashToUpdate) {
		this.intraHashToUpdate = intraHashToUpdate;
	}

	public boolean isAcceptComma() {
		return this.acceptComma;
	}

	public void setAcceptComma(boolean acceptComma) {
		this.acceptComma = acceptComma;
	}

	public boolean getContainsComma() {
		return this.containsComma;
	}

	public void setContainsComma(boolean containsComma) {
		this.containsComma = containsComma;
	}

	public void setPostID(int postID) {
		this.postID = postID;
	}

	public int getPostID() {
		return postID;
	}
}
