package org.bibsonomy.wiki.tags.user;


import static org.bibsonomy.util.ValidationUtils.present;
import info.bliki.htmlcleaner.Utils;

import org.bibsonomy.wiki.tags.AbstractTag;

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
	protected String render() {
		final StringBuffer renderedHTML = new StringBuffer();
		final String hobbies = this.requestedUser.getHobbies();

		if (present(hobbies)) {
			renderedHTML.append("<p class='align'>");
			renderedHTML.append(Utils.escapeXmlChars(hobbies));
			renderedHTML.append("</p>");
		}
		return renderedHTML.toString();
	}

}

