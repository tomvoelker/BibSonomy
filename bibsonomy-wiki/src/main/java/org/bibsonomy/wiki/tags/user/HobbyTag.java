package org.bibsonomy.wiki.tags.user;


import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.wiki.tags.UserTag;

/**
 * @author philipp
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
		final String hobby = this.renderString(this.requestedUser.getHobbies());
		return ValidationUtils.present(hobby) ? "<div id='hobbies'>" + hobby + "</div>" : "";
	}

}

