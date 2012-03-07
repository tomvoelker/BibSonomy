package org.bibsonomy.webapp.command.resource;

import java.util.List;

import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.TagResourceViewCommand;

/**
 * @author dzo
 * @version $Id$
 * @param <R> the resource
 */
public class ResourcePageCommand<R extends Resource> extends TagResourceViewCommand {
	private String requestedHash;
	
	private List<DiscussionItem> discussionItems;
	
	private String postOwner;
	
	private String intraHash;

	/**
	 * @return the intraHash of a post
	 */
	public String getIntraHash() {
		return this.intraHash;
	}

	/**
	 * set the intraHash of a post
	 * @param intraHash
	 */
	public void setIntraHash(String intraHash) {
		this.intraHash = intraHash;
	}

	/**
	 * @return the owner of the post
	 */
	public String getPostOwner() {
		return this.postOwner;
	}

	/**
	 * set the owner of a post
	 * @param postOwner
	 */
	public void setPostOwner(String postOwner) {
		this.postOwner = postOwner;
	}

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
