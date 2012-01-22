package org.bibsonomy.webapp.command.resource;

import java.util.List;

import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.TagResourceViewCommand;

/**
 * @author dzo
 * @version $Id$
 * @param <R> the resource
 */
public class ResourcePageCommand<R extends Resource> extends TagResourceViewCommand {
	private String requestedHash;
	
	
	/**
	 * A variable to state whether or not the posts are public or not.
	 * In case of private, or otherwise visibility restricted posts we ask for confirmation before we create a goldstandard. For public posts we dont.
	 */
	private boolean publicPost = true;
	
	/**
	 * @return whether the posts displayed are public or not
	 */
	public boolean isPublicPost() {
		return this.publicPost;
	}
	
	/**
	 * @param publicPost
	 */
	public void setPublicPost(final boolean publicPost) {
		this.publicPost = publicPost;
	}

	private List<DiscussionItem> discussionItems;
	private Post<R> goldStandard;

	/**
	 * @return the requestedHash
	 */
	public String getRequestedHash() {
		return this.requestedHash;
	}

	/**
	 * @param requestedHash the requestedHash to set
	 */
	public void setRequestedHash(final String requestedHash) {
		this.requestedHash = requestedHash;
	}

	/**
	 * @return the goldStandard
	 */
	public Post<R> getGoldStandard() {
		return this.goldStandard;
	}

	/**
	 * @param goldStandard the goldStandard to set
	 */
	public void setGoldStandard(final Post<R> goldStandard) {
		this.goldStandard = goldStandard;
	}

	/**
	 * @return the discussionItems
	 */
	public List<DiscussionItem> getDiscussionItems() {
		return this.discussionItems;
	}

	/**
	 * @param discussionItems the discussionItems to set
	 */
	public void setDiscussionItems(final List<DiscussionItem> discussionItems) {
		this.discussionItems = discussionItems;
	}
}
