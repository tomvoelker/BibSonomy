package org.bibsonomy.wiki.tags.user;

import static org.bibsonomy.util.ValidationUtils.present;
import info.bliki.htmlcleaner.Utils;

import java.net.URL;

import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * This is a simple name-tag.
 * Usage: <name />
 * 
 * @author Bernd
 * @version $Id$
 */
public class NameTag extends AbstractTag {
	
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
	protected String render() {
		final StringBuffer renderedHTML = new StringBuffer();
		final String name = Utils.escapeXmlChars(this.requestedUser.getRealname());
		if (present(name)) {
			final URL homepage = this.requestedUser.getHomepage();
			if (present(homepage)) {
				renderedHTML.append("<a href=\"");
				renderedHTML.append(Utils.escapeXmlChars(this.requestedUser.getHomepage().toExternalForm()));
				renderedHTML.append("\">");
				renderedHTML.append(name);
				renderedHTML.append("</a>");
			} else {
				renderedHTML.append(name);
			}
		}
		return renderedHTML.toString();
	}

}
