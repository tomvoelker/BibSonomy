package org.bibsonomy.wiki.tags.shared;

import java.net.URL;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.wiki.tags.UserTag;

/**
 * This is a simple homepage-tag.
 * Usage: <homepage />
 * @author janus
 * @version $Id$
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
		final String homepage = this.requestedUser.getHomepage() == null ? ""
				: this.renderString(this.requestedUser.getHomepage().toString());
		return ValidationUtils.present(homepage) ? "<div id='homepage'><a href=\"" + homepage + "\">" + homepage + "</a></div>" : "";
	}

}
