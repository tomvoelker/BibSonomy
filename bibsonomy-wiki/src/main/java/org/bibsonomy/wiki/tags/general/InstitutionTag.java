package org.bibsonomy.wiki.tags.general;

import info.bliki.htmlcleaner.Utils;

import org.bibsonomy.wiki.tags.AbstractTag;

public class InstitutionTag extends AbstractTag{
	public static final String TAG_NAME = "institution";
	public InstitutionTag() {
		super(TAG_NAME);
	}
	@Override
	protected StringBuilder render() {
		StringBuilder renderedHTML = new StringBuilder();
		renderedHTML.append(Utils.escapeXmlChars(requestedUser.getInstitution()));
		return renderedHTML;
	}

}
