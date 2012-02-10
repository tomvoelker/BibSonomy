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
