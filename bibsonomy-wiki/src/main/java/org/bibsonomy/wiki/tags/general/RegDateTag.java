package org.bibsonomy.wiki.tags.general;

import info.bliki.htmlcleaner.Utils;
import static org.bibsonomy.util.ValidationUtils.present;
import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * This is a simple registered-date-tag
 * Usage: <regdate />
 * @author Bernd
 *
 */
public class RegDateTag extends AbstractTag {
	public static final String TAG_NAME = "regdate";

	public RegDateTag() {
		super(TAG_NAME);
	}

	@Override
	protected StringBuilder render() {
		StringBuilder renderedHTML = new StringBuilder();
		final String regDate = Utils.escapeXmlChars(this.requestedUser.getRegistrationDate().toString());
		if (present(regDate)) {
			renderedHTML.append(regDate);
		}
		return renderedHTML;
	}
}