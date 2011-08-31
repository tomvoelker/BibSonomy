package org.bibsonomy.wiki.tags.aboutme;

import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * @author philipp
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

	@Override
	protected StringBuilder render() {
		return this.renderParagraph(this.requestedUser.getInterests());
	}
}
