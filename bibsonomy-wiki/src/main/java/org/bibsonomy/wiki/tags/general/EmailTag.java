package org.bibsonomy.wiki.tags.general;

import info.bliki.htmlcleaner.Utils;
import static org.bibsonomy.util.ValidationUtils.present;
import org.bibsonomy.wiki.tags.AbstractTag;

public class EmailTag extends AbstractTag {
	public static final String TAG_NAME = "email";

	public EmailTag() {
		super(TAG_NAME);
	}

	@Override
	protected StringBuilder render() {
		StringBuilder renderedHTML = new StringBuilder();
		final String email = Utils.escapeXmlChars(this.requestedUser.getEmail());
		if (present(email)) {
			renderedHTML.append("<a href='mailto:" + email + "'>" + email + "</a>");
		}
		return renderedHTML;
	}
}