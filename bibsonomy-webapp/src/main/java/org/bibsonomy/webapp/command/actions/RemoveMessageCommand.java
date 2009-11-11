package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * This Commands information: which Message is to be removed from the inbox 
 * @author sdo
 * @version $Id$
 */
public class RemoveMessageCommand extends BaseCommand implements Serializable {
	private static final long serialVersionUID = -6623936347565283765L;
	private int contentId; //the post to which the inbox-message is to be removed

	/**
	 * @return contentId
	 */
	public int getContentId() {
		return this.contentId;
	}

	/**
	 * @param contentId
	 */
	public void setContentId(final int contentId) {
		this.contentId = contentId;
	}

	
	
}
