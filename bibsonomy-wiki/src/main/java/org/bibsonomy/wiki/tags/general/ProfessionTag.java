package org.bibsonomy.wiki.tags.general;

import info.bliki.htmlcleaner.Utils;

import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * This is a simple profession-tag.
 * Usage: <profession />
 * @author Bernd
 *
 */
public class ProfessionTag extends AbstractTag{
	public static final String TAG_NAME = "profession";
	public ProfessionTag() {
		super(TAG_NAME);
	}
	
	@Override
	protected StringBuilder render() {
		StringBuilder renderedHTML = new StringBuilder();
		renderedHTML.append(Utils.escapeXmlChars(requestedUser.getProfession()));
		return renderedHTML;
	}

}
