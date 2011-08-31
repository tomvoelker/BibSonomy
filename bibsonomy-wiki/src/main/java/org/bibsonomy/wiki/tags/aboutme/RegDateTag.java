package org.bibsonomy.wiki.tags.aboutme;

import static org.bibsonomy.util.ValidationUtils.present;
import info.bliki.htmlcleaner.Utils;

import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * This is a simple registered-date-tag
 * Usage: <regdate />
 * @author Bernd
 *
 */
public class RegDateTag extends AbstractTag {
	private static final String TAG_NAME = "regdate";

	/**
	 * set the tag name
	 */
	public RegDateTag() {
		super(TAG_NAME);
	}

	@Override
	protected StringBuilder render() {
		final StringBuilder renderedHTML = new StringBuilder();
		final String regDate = Utils.escapeXmlChars(this.requestedUser.getRegistrationDate().toString());
		if (present(regDate)) {
			renderedHTML.append(regDate);
		}
		return renderedHTML;
	}
}