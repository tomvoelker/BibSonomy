package org.bibsonomy.wiki.tags.general;

import info.bliki.htmlcleaner.Utils;

import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * This is a simple institution-tag.
 * Usage: <institution />
 * @author Bernd
 * @version $Id$
 */
public class InstitutionTag extends AbstractTag{
	private static final String TAG_NAME = "institution";
	
	/**
	 * set the name of the tag
	 */
	public InstitutionTag() {
		super(TAG_NAME);
	}
	
	@Override
	protected StringBuilder render() {
		final StringBuilder renderedHTML = new StringBuilder();
		renderedHTML.append(Utils.escapeXmlChars(requestedUser.getInstitution()));
		return renderedHTML;
	}

}
