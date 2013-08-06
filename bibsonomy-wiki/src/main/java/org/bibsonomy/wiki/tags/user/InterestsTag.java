package org.bibsonomy.wiki.tags.user;

import org.bibsonomy.util.ValidationUtils;
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
		final String interests = this.renderString(this.requestedUser.getInterests());
		return ValidationUtils.present(interests) ? "<div id='interests'>" + interests + "</div>" : "";
	}
}
