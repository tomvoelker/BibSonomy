package org.bibsonomy.wiki.tags.user;


import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.wiki.tags.AbstractTag;

import info.bliki.htmlcleaner.Utils;

/**
 * @author philipp
 * @version $Id$
 */
public class HobbieTag extends AbstractTag {
	private static final String TAG_NAME = "hobbies";

	/**
	 * set the tag name
	 */
	public HobbieTag() {
		super(TAG_NAME);
	}

	@Override
	protected StringBuilder render() {
		final StringBuilder renderedHTML = new StringBuilder();
		final String hobbies = this.requestedUser.getHobbies();

		if (present(hobbies)) {
			renderedHTML.append("<p class='align'>");
			renderedHTML.append(Utils.escapeXmlChars(hobbies));
			renderedHTML.append("</p>");
		}
		return renderedHTML;
	}

}

