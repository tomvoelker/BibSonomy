package org.bibsonomy.wiki.tags.shared;

import static org.bibsonomy.util.ValidationUtils.present;
import info.bliki.htmlcleaner.Utils;

import java.net.URL;

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
	protected String renderUserTag() {
		final StringBuilder renderedHTML = new StringBuilder();
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

	@Override
	protected String renderGroupTag() {
		final StringBuilder renderedHTML = new StringBuilder();
		final String name = Utils.escapeXmlChars(this.requestedGroup.getRealname());
		if (present(name)) {
			final URL homepage = this.requestedGroup.getHomepage();
			if (present(homepage)) {
				renderedHTML.append("<a href=\"");
				renderedHTML.append(Utils.escapeXmlChars(this.requestedGroup.getHomepage().toExternalForm()));
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
