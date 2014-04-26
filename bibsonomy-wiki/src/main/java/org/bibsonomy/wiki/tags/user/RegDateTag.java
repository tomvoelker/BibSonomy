package org.bibsonomy.wiki.tags.user;

import static org.bibsonomy.util.ValidationUtils.present;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bibsonomy.wiki.tags.UserTag;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * This is a simple registered-date-tag Usage: <regdate />
 * 
 * @author Bernd Terbrack
 */
public class RegDateTag extends UserTag {
	private static final String TAG_NAME = "regdate";

	/*
	 * TODO Unify date handling for this tag and for the BirthdayTag (same options) probably useful would be an AbstractDate Tag
	 */
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

	/**
	 * set the tag name
	 */
	public RegDateTag() {
		super(TAG_NAME);
	}

	@Override
	protected String renderUserTag() {
		final Date regDate = this.requestedUser.getRegistrationDate();
		if (present(regDate)) {
			return "<div id='regDate'>" + this.renderString(fmt.print(regDate.getTime())) + "</div>";
		}
		return "";
	}
}