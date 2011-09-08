package org.bibsonomy.wiki.tags.shared;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.wiki.tags.SharedTag;

/**
 * This is a simple name-tag.
 * Usage: <name />
 * 
 * @author Bernd Terbrack
 * @version $Id$
 */
public class NameTag extends SharedTag {
	
	/*
	 * TODO: DISCUSS: should we use the homepage link for the real name?
	 * would it not be better to have a homepage tag and have the name link to the bibsonomy-page of the user?
	 */
	private static final String TAG_NAME = "name";

	/**
	 * dafault construtor
	 */
	public NameTag() {
		super(TAG_NAME);
	}

	@Override
	protected String renderSharedTag(final RequestType requestType) {
		final StringBuilder renderedHTML = new StringBuilder();
		final String name = this.getRequestedName(requestType);
		if (present(name)) {
			/*if (present(homepage)) {
				renderedHTML.append("<a href=\"");
				renderedHTML.append(Utils.escapeXmlChars(this.requestedUser.getHomepage().toExternalForm()));
				renderedHTML.append("\">");
				renderedHTML.append(name);
				renderedHTML.append("</a>");
			} else {*/
				renderedHTML.append(name);
			//}
		}
		return renderedHTML.toString();
	}

}
