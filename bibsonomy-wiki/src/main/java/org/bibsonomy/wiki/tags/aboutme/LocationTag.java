package org.bibsonomy.wiki.tags.aboutme;

import info.bliki.htmlcleaner.Utils;

import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * This is a simple location-tag.
 * Usage: <location />
 * @author Bernd
 *
 */
public class LocationTag extends AbstractTag{
	private static final String TAG_NAME = "location";
	
	/**
	 * set name of the tag
	 */
	public LocationTag() {
		super(TAG_NAME);
	}
	
	@Override
	protected StringBuilder render() {
		final StringBuilder renderedHTML = new StringBuilder();
		renderedHTML.append(Utils.escapeXmlChars(requestedUser.getPlace()));
		return renderedHTML;
	}

}
