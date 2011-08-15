package org.bibsonomy.wiki.tags.general;

import info.bliki.htmlcleaner.Utils;
import static org.bibsonomy.util.ValidationUtils.present;
import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * This is a simple name-tag.
 * Usage: <name />
 * 
 * @author Bernd
 *
 */
public class NameTag extends AbstractTag {
	public static final String TAG_NAME = "name";

	public NameTag() {
		super(TAG_NAME);
	}

	@Override
	protected StringBuilder render() {
		StringBuilder renderedHTML = new StringBuilder();
		final String name = Utils.escapeXmlChars(this.requestedUser.getRealname());
		if (present(name)) {
			if (present(requestedUser.getHomepage())) {
				final String homepage = Utils.escapeXmlChars(this.requestedUser.getHomepage().toExternalForm());
				renderedHTML.append("<a href='"+homepage+"'>"+name+"</a>");
			} else {
				renderedHTML.append(name);
			}
		}
		return renderedHTML;
	}

}
