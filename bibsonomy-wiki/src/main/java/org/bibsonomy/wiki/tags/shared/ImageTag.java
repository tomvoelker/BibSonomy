package org.bibsonomy.wiki.tags.shared;

import info.bliki.htmlcleaner.TagNode;
import info.bliki.htmlcleaner.Utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.wiki.tags.SharedTag;

/**
 * This is a simple image-tag Usage: <image />
 * 
 * @author Bernd
 * @version $Id$
 */
public class ImageTag extends SharedTag {
	private static final String TAG_NAME = "image";
	private static final String FLOAT = "style";

	private final static Set<String> ALLOWED_ATTRIBUTES_SET = new HashSet<String>(Arrays.asList(FLOAT));

	/**
	 * set name of tag
	 */
	public ImageTag() {
		super(TAG_NAME);
	}

	@Override
	protected String renderUserTag() {
		final TagNode node = this;
		final Map<String, String> tagAtttributes = node.getAttributes();
		final StringBuilder renderedHTML = new StringBuilder();
		final String name = Utils.escapeXmlChars(this.requestedUser.getName());
		/*
		 * TODO: Kriegen wir die URLs aus dem JavaCode raus 
		 * FIXME: tolowercase impl.
		 */
		renderedHTML.append("<img src='/picture/user/").append(name).append("' style='").append(tagAtttributes.get(FLOAT)).append("'>");
		return renderedHTML.toString();
	}

	@Override
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES_SET.contains(attName);
	}

	@Override
	protected String renderGroupTag() {
		// TODO Auto-generated method stub
		return "Not implemented yet.";
	}

}
