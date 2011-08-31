package org.bibsonomy.wiki.tags.aboutme;

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
	private static final String TAG_NAME = "name";

	/**
	 * dafault construtor
	 */
	public NameTag() {
		super(TAG_NAME);
	}

	@Override
	protected StringBuilder render() {
		final StringBuilder renderedHTML = new StringBuilder();
		final String name = Utils.escapeXmlChars(this.requestedUser.getRealname());
		if (present(name)) {
			final URL homepage = requestedUser.getHomepage();
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
		return renderedHTML;
	}

}
