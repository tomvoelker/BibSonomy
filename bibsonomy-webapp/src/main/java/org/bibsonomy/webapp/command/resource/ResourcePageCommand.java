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
	
	// TODO: remove!?
	private String title;
	
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
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
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
	@Override
	public List<DiscussionItem> getDiscussionItems() {
		return this.discussionItems;
	}

	/**
	 * @param discussionItems the discussionItems to set
	 */
	@Override
	public void setDiscussionItems(final List<DiscussionItem> discussionItems) {
		this.discussionItems = discussionItems;
	}
}
