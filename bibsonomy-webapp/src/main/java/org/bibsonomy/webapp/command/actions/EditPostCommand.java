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
	
	private String tags;
	
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
	
	/** should the edit view be shown before the post is stored into the db? */
	private boolean editBeforeSaving;

	/**
	 * @return the post
	 */
	public Post<RESOURCE> getPost() {
		return this.post;
	}

	/**
	 * @param post the post to set
	 */
	public void setPost(Post<RESOURCE> post) {
		this.post = post;
	}

	/**
	 * @return the tags
	 */
	public String getTags() {
		return this.tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

	/**
	 * @return the groups
	 */
	public List<String> getGroups() {
		return this.groups;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	/**
	 * @return the relevantGroups
	 */
	public List<String> getRelevantGroups() {
		return this.relevantGroups;
	}

	/**
	 * @param relevantGroups the relevantGroups to set
	 */
	public void setRelevantGroups(List<String> relevantGroups) {
		this.relevantGroups = relevantGroups;
	}

	/**
	 * @return the recommendedTags
	 */
	public SortedSet<RecommendedTag> getRecommendedTags() {
		return this.recommendedTags;
	}

	/**
	 * @param recommendedTags the recommendedTags to set
	 */
	public void setRecommendedTags(SortedSet<RecommendedTag> recommendedTags) {
		this.recommendedTags = recommendedTags;
	}
		
	/**
	 * @return the relevantTagSets
	 */
	public Map<String, Map<String, List<String>>> getRelevantTagSets() {
		return this.relevantTagSets;
	}

	/**
	 * @param relevantTagSets the relevantTagSets to set
	 */
	public void setRelevantTagSets(Map<String, Map<String, List<String>>> relevantTagSets) {
		this.relevantTagSets = relevantTagSets;
	}

	/**
	 * Sets the tags from the copied post.
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
	
	/**
	 * @return the copytags
	 */
	public List<Tag> getCopytags() {
		return this.copytags;
	}

	/**
	 * @param copytags the copytags to set
	 */
	public void setCopytags(List<Tag> copytags) {
		this.copytags = copytags;
	}

	/**
	 * @return the abstractGrouping
	 */
	public String getAbstractGrouping() {
		return this.abstractGrouping;
	}

	/**
	 * @param abstractGrouping the abstractGrouping to set
	 */
	public void setAbstractGrouping(String abstractGrouping) {
		this.abstractGrouping = abstractGrouping;
	}

	/**
	 * @return the diffPost
	 */
	public Post<RESOURCE> getDiffPost() {
		return this.diffPost;
	}

	/**
	 * @param diffPost the diffPost to set
	 */
	public void setDiffPost(Post<RESOURCE> diffPost) {
		this.diffPost = diffPost;
	}

	/**
	 * @return the intraHashToUpdate
	 */
	public String getIntraHashToUpdate() {
		return this.intraHashToUpdate;
	}

	/**
	 * @param intraHashToUpdate the intraHashToUpdate to set
	 */
	public void setIntraHashToUpdate(String intraHashToUpdate) {
		this.intraHashToUpdate = intraHashToUpdate;
	}

	/**
	 * @return the acceptComma
	 */
	public boolean isAcceptComma() {
		return this.acceptComma;
	}

	/**
	 * @param acceptComma the acceptComma to set
	 */
	public void setAcceptComma(boolean acceptComma) {
		this.acceptComma = acceptComma;
	}
	
	/**
	 * @return the containsComma
	 */
	public boolean getContainsComma() {
		return this.containsComma;
	}

	/**
	 * @param containsComma the containsComma to set
	 */
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
	 * @return the postID used by the recommenders
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

	/**
	 * @return the recaptcha_challenge_field
	 */
	public String getRecaptcha_challenge_field() {
		return this.recaptcha_challenge_field;
	}

	/**
	 * @param recaptchaChallengeField the recaptcha_challenge_field to set
	 */
	public void setRecaptcha_challenge_field(String recaptchaChallengeField) {
		this.recaptcha_challenge_field = recaptchaChallengeField;
	}

	/**
	 * @return the recaptcha_response_field
	 */
	public String getRecaptcha_response_field() {
		return this.recaptcha_response_field;
	}

	/**
	 * @param recaptchaResponseField the recaptcha_response_field to set
	 */
	public void setRecaptcha_response_field(String recaptchaResponseField) {
		this.recaptcha_response_field = recaptchaResponseField;
	}

	/**
	 * @return the captchaHTML
	 */
	public String getCaptchaHTML() {
		return this.captchaHTML;
	}

	/**
	 * @param captchaHTML the captchaHTML to set
	 */
	public void setCaptchaHTML(String captchaHTML) {
		this.captchaHTML = captchaHTML;
	}

	/**
	 * @return the editBeforeSaving
	 */
	public boolean isEditBeforeSaving() {
		return this.editBeforeSaving;
	}

	/**
	 * @param editBeforeSaving the editBeforeSaving to set
	 */
	public void setEditBeforeSaving(boolean editBeforeSaving) {
		this.editBeforeSaving = editBeforeSaving;
	}
}
