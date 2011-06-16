package org.bibsonomy.webapp.command.ajax;

import java.util.List;

import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.webapp.command.GroupingCommand;

/**
 * @author dzo
 * @version $Id$
 * @param <D> 
 */
public class DiscussionItemAjaxCommand<D extends DiscussionItem> extends AjaxCommand implements GroupingCommand {
	
	/**
	 * the discussionItem
	 */
	private D discussionItem;
	
	/**
	 * the hash of the resource
	 */
	private String hash;
	
	/**
	 * The abstract (or general) group of the post:
	 * public, private, or other 
	 */
	private String abstractGrouping;
	
	/**
	 * the groups
	 */
	private List<String> groups;
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.ajax.GroupingCommand#getAbstractGrouping()
	 */
	@Override
	public String getAbstractGrouping() {
		return this.abstractGrouping;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.ajax.GroupingCommand#setAbstractGrouping(java.lang.String)
	 */
	@Override
	public void setAbstractGrouping(String abstractGrouping) {
		this.abstractGrouping = abstractGrouping;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.ajax.GroupingCommand#getGroups()
	 */
	@Override
	public List<String> getGroups() {
		return this.groups;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.ajax.GroupingCommand#setGroups(java.util.List)
	 */
	@Override
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	/**
	 * @return the comment
	 */
	public D getDiscussionItem() {
		return this.discussionItem;
	}
	
	/**
	 * @param discussionItem the comment to set
	 */
	public void setDiscussionItem(final D discussionItem) {
		this.discussionItem = discussionItem;
	}
	
	/**
	 * @return the hash
	 */
	public String getHash() {
		return this.hash;
	}
	
	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}	
}
