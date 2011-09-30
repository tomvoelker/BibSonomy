package org.bibsonomy.wiki.tags.user;

import org.bibsonomy.wiki.tags.UserTag;

/**
 * @author philipp
 * @author Bernd Terbrack
 * @version $Id$
 */
public class InterestsTag extends UserTag {
	
	private static final String TAG_NAME = "interests";
	
	/**
	 * 
	 */
	public InterestsTag() {
        super(TAG_NAME);
	}

	@Override
	protected String renderUserTag() {
		return this.renderString(this.requestedUser.getInterests());
	}
}
