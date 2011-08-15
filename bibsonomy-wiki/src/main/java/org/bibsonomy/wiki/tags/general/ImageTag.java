package org.bibsonomy.wiki.tags.general;

import info.bliki.htmlcleaner.Utils;

import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * This is a simple image-tag
 * Usage: <image />
 * @author Bernd
 *
 */

public class ImageTag extends AbstractTag{
	public static final String TAG_NAME = "image";
	public ImageTag() {
		super(TAG_NAME);
	}
	@Override
	protected StringBuilder render() {
		StringBuilder renderedHTML = new StringBuilder();
		final String name = Utils.escapeXmlChars(this.requestedUser.getName());
		renderedHTML.append("<img src='/picture/user/"+name+"'>");
		return renderedHTML;
	}

}
