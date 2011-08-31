package org.bibsonomy.wiki.tags.aboutme;

import static org.bibsonomy.util.ValidationUtils.present;
import info.bliki.htmlcleaner.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * This is a simple birthday-tag.
 * Usage: <birthday />
 * @author Bernd
 *
 */
public class BirthdayTag extends AbstractTag{
	private static final String TAG_NAME = "birthday";
	private static final String DATE_FORMAT = "dd-MM-yyyy";
	
	
	private static final SimpleDateFormat simpleDate = new SimpleDateFormat(DATE_FORMAT);
	
	/**
	 * set tag name
	 */
	public BirthdayTag() {
		super(TAG_NAME);
	}
	
	@Override
	protected StringBuilder render() {
		final StringBuilder renderedHTML = new StringBuilder();		
		//FIXME: requestedUser Birthday?!

		final Date birthday = requestedUser.getBirthday();
		if (present(birthday)) {
			renderedHTML.append(Utils.escapeXmlChars(simpleDate.format(birthday)));
		}

		return renderedHTML;
	}

}
