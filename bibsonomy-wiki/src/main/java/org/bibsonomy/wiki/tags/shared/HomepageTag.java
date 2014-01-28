package org.bibsonomy.wiki.tags.shared;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.wiki.tags.UserTag;

/**
 * This is a simple homepage-tag.
 * Usage: <homepage />
 * @author janus
 */
public class HomepageTag extends UserTag {
	private static final String TAG_NAME = "homepage";
	
	/**
	 * set the name of the tag
	 */
	public HomepageTag() {
		super(TAG_NAME);
	}
	
	@Override
	protected String renderUserTag() {
		final String homepage = this.requestedUser.getHomepage() == null ? "" : this.renderString(this.requestedUser.getHomepage().toString());
		// TODO: add attribute to set the displayed link name?
		return present(homepage) ? "<div id='homepage'><a href=\"" + homepage + "\" rel=\"nofollow me\">" + homepage + "</a></div>" : "";
	}

}
