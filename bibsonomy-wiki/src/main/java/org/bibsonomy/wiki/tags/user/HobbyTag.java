package org.bibsonomy.wiki.tags.user;


import org.bibsonomy.wiki.tags.UserTag;

/**
 * @author philipp
 * @version $Id$
 */
public class HobbyTag extends UserTag {
	private static final String TAG_NAME = "hobbies";

	/**
	 * set the tag name
	 */
	public HobbyTag() {
		super(TAG_NAME);
	}

	@Override
	protected String renderUserTag() {
		return this.renderString(this.requestedUser.getHobbies());
	}

}

