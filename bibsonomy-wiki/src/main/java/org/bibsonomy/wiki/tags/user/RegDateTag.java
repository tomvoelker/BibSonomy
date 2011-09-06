package org.bibsonomy.wiki.tags.user;

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
	
	/*
	 * TODO Unify date handling for this tag and for the BirthdayTag (same options)
	 * probably useful would be an AbstractDate Tag
	 */
	private static final String TAG_NAME = "regdate";

	/**
	 * set the tag name
	 */
	public RegDateTag() {
		super(TAG_NAME);
	}

	@Override
	protected String render() {
		final StringBuffer renderedHTML = new StringBuffer();
		final String regDate = Utils.escapeXmlChars(this.requestedUser.getRegistrationDate().toString());
		if (present(regDate)) {
			renderedHTML.append(regDate);
		}
		return renderedHTML.toString();
	}
}