package org.bibsonomy.wiki.tags.general;

import static org.bibsonomy.util.ValidationUtils.present;
import info.bliki.htmlcleaner.Utils;

import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * TODO: remove? only the user and admins are allowed to access the email address!
 * 
 * This is a simple email-tag.
 * Usage: <email />
 * @author Bernd
 * @version $Id$
 */
public class EmailTag extends AbstractTag {
	private static final String TAG_NAME = "email";

	/**
	 * set tag name
	 */
	public EmailTag() {
		super(TAG_NAME);
	}

	@Override
	protected StringBuilder render() {
		final StringBuilder renderedHTML = new StringBuilder();
		final String email = Utils.escapeXmlChars(this.requestedUser.getEmail());
		if (present(email)) {
			renderedHTML.append("<a href='mailto:").append(email).append("'>").append(email + "</a>");
		}
		return renderedHTML;
	}
}