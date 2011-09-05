package org.bibsonomy.wiki.tags.user;

import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * @author philipp
 * @author Bernd Terbrack
 * @version $Id$
 */
public class InterestsTag extends AbstractTag {
	
	private static final String TAG_NAME = "interests";
	
	/**
	 * 
	 */
	public InterestsTag() {
        super(TAG_NAME);
	}

	/*
	 * TODO: add some fancy attributes! ie intersection of interests of users etc etc
	 * (non-Javadoc)
	 * @see org.bibsonomy.wiki.tags.AbstractTag#render()
	 */
	@Override
	protected StringBuilder render() {
		return this.requestedUser != null ? this.renderParagraph(this.requestedUser.getInterests()) : this.renderParagraph("");
	}
}
