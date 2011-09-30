package org.bibsonomy.wiki.tags.user;

import static org.bibsonomy.util.ValidationUtils.present;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bibsonomy.wiki.tags.UserTag;

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
	 */
	private static final SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * set tag name
	 */
	public BirthdayTag() {
		super(TAG_NAME);
	}
	
	@Override
	protected String renderUserTag() {
		final Date birthday = this.requestedUser.getBirthday();
		if (present(birthday)) {
			return this.renderString(simpleDate.format(birthday));
		}
		return "";
	}

}
