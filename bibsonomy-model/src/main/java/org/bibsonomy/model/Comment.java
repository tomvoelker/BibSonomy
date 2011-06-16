package org.bibsonomy.model;


/**
 * @author dzo
 * @version $Id$
 */
public class Comment extends DiscussionItem {
	
	private String text;
	
	/**
	 * @return the text
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(final String text) {
		this.text = text;
	}
}
