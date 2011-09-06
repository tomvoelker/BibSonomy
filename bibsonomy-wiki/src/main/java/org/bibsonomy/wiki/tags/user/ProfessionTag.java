package org.bibsonomy.wiki.tags.user;

import org.bibsonomy.wiki.tags.UserTag;

/**
 * This is a simple profession-tag.
 * Usage: <profession />
 * @author Bernd Terbrack
 *
 */
public class ProfessionTag extends UserTag{
	private static final String TAG_NAME = "profession";
	
	/**
	 * set the name of the tag
	 */
	public ProfessionTag() {
		super(TAG_NAME);
	}
	
	@Override
	protected String renderUserTag() {
		return this.renderString(this.requestedUser.getProfession());
	}

}
