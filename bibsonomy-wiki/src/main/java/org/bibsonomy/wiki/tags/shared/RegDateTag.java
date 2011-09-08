package org.bibsonomy.wiki.tags.shared;

import static org.bibsonomy.util.ValidationUtils.present;
import info.bliki.htmlcleaner.Utils;

import org.bibsonomy.wiki.tags.SharedTag;

/**
 * This is a simple registered-date-tag
 * Usage: <regdate />
 * @author Bernd Terbrack
 *
 */
public class RegDateTag extends SharedTag {
	
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
	protected String renderUserTag() {
		final StringBuilder renderedHTML = new StringBuilder();
		final String regDate = Utils.escapeXmlChars(this.requestedUser.getRegistrationDate().toString());
		if (present(regDate)) {
			renderedHTML.append(regDate);
		}
		return renderedHTML.toString();
	}

	@Override
	protected String renderGroupTag() {
		// TODO Auto-generated method stub
		return "Not implemented yet.";
	}
}