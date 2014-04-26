package org.bibsonomy.wiki.tags.user;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;

import org.bibsonomy.wiki.tags.UserTag;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * This is a simple birthday-tag.
 * Usage: <birthday />
 * @author Bernd
 *
 */
public class BirthdayTag extends UserTag {
	
	private static final String TAG_NAME = "birthday";
	/*
	 * TODO: allow other formats as tag parameter (e.g. display without year or the english order mm/dd/yyyy..."
	 * TODO: maybe think about standardizing it with the rest of BibSonomy?
	 */
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
	
	/**
	 * set tag name
	 */
	public BirthdayTag() {
		super(TAG_NAME);
	}
	
	@Override
	protected String renderUserTag() {
		final Date birthday = this.requestedUser.getBirthday();
		// TODO: Insert something small indicating the user's birthday
		// if it coincides with the present day :)
		if (present(birthday)) {
			return "<div id='birthday'>" + this.renderString(fmt.print(birthday.getTime())) + "</div>";
		}
		return "";
	}
	
	public static void main(String[] args) {
		
	}

}
