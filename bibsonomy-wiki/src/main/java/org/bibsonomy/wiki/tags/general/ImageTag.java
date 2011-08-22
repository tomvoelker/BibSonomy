package org.bibsonomy.wiki.tags.general;

import info.bliki.htmlcleaner.Utils;

import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * This is a simple image-tag
 * Usage: <image />
 * @author Bernd
 * @version $Id$
 */
public class ImageTag extends AbstractTag{
	private static final String TAG_NAME = "image";
	
	/**
	 * set name of tag
	 */
	public ImageTag() {
		super(TAG_NAME);
	}
	
	@Override
	protected StringBuilder render() {
		final StringBuilder renderedHTML = new StringBuilder();
		final String name = Utils.escapeXmlChars(this.requestedUser.getName());
		renderedHTML.append("<img src='/picture/user/").append(name).append("'>");
		return renderedHTML;
	}

}
