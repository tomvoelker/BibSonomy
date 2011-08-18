package org.bibsonomy.wiki.tags.general;

import info.bliki.htmlcleaner.Utils;

import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * This is a simple birthday-tag.
 * Usage: <birthday />
 * @author Bernd
 *
 */
public class BirthdayTag extends AbstractTag{
	public static final String TAG_NAME = "birthday";
	public static final String DATE_FORMAT = "dd-MM-yyyy";
	public BirthdayTag() {
		super(TAG_NAME);
	}
	
	@Override
	protected StringBuilder render() {
		StringBuilder renderedHTML = new StringBuilder();
		/*SimpleDateFormat simpleDate = new SimpleDateFormat(DATE_FORMAT);*/
		renderedHTML.append(Utils.escapeXmlChars(/*simpleDate.format(*/requestedUser.getBirthday().toString()));
		return renderedHTML;
	}

}
