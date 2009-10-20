package org.bibsonomy.webapp.command.actions;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.PostCommand;

/**
 * @author fba
 * @version $Id$
 * @param <RESOURCE> The type of resource this command handles.
 */
public class EditPostCommand<RESOURCE extends Resource> extends PostCommand {
	/**
	 * The tags of the copied post.
	 */
	private List<Tag> copytags;
	
	private Post<RESOURCE> post;
	private String tags ;
	private boolean jump = false;
	
	private Post<RESOURCE> diffPost;
	
	/**
	 * If the user edits his own post, this field is used to identify the post. 
	 */
	private String intraHashToUpdate;
	/**
	 * If the user wants to copy a post from another user, this field is used.
	 * NOTE: the name must be "hash" since this was the case in the old system.
	 * There might exist web pages which use parameter name!
	 */
	private String hash;
	/**
	 * This is the user who owns the post which should be copied.
	 */
	private String user;
	
	
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
	
	private String recaptcha_challenge_field;
	private String recaptcha_response_field;
	private String captchaHTML;

	
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

	/**
	 * The post id is used to uniquely identify a post until it is stored in the
	 * database. The recommender service needs this to assign recommenders to 
	 * posting processes.
	 *  
	 * @param postID
	 */
	public void setPostID(int postID) {
		this.postID = postID;
	}

	/**
	 * @see #setPostID(int)
	 * @return
	 */
	public int getPostID() {
		return postID;
	}

	/**
	 * @return The intra hash of the post which should be copied. Must be used 
	 * together with the name of the user.
	 */
	public String getHash() {
		return this.hash;
	}

	/**
	 * Sets the intra hash of the post which should be copied. Must be used 
	 * together with the name of the user.
	 * 
	 * @param hash
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * @return The name of the user whose post should be copied.
	 */
	public String getUser() {
		return this.user;
	}

	/** 
	 * @param user The name of the user whose post should be copied.
	 */
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getCaptchaHTML() {
		return this.captchaHTML;
	}

	public void setCaptchaHTML(String captchaHTML) {
		this.captchaHTML = captchaHTML;
	}

	public String getRecaptcha_challenge_field() {
		return this.recaptcha_challenge_field;
	}

	public void setRecaptcha_challenge_field(String recaptcha_challenge_field) {
		this.recaptcha_challenge_field = recaptcha_challenge_field;
	}

	public String getRecaptcha_response_field() {
		return this.recaptcha_response_field;
	}

	public void setRecaptcha_response_field(String recaptcha_response_field) {
		this.recaptcha_response_field = recaptcha_response_field;
	}
}
