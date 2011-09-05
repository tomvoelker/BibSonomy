package org.bibsonomy.wiki.tags.user;

import info.bliki.htmlcleaner.TagNode;
import info.bliki.htmlcleaner.Utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * This is a simple image-tag
 * Usage: <image />
 * @author Bernd
 * @version $Id$
 */
public class ImageTag extends AbstractTag{
	private static final String TAG_NAME = "image";
	private static final String FLOAT = "float";
	
	/*
	 * FIXME: imho it would make sense to have classes for these structures (enums?)
	 * thus we could implement guaranteed testing with toLowerCase etc.
	 * e.g. AbstractTag has a field of type Set<AllowedAttribute>
	 * where AllowedAttribute is an object representing a parameter containing a name and an the possible values for the param
	 * This set would be filled on creation of the tag.
	 */
	private static final Set<String> ALLOWED_FLOAT_ATTRIBUTES = new HashSet<String>(Arrays.asList("left","right","none"));
	private final static Set<String> ALLOWED_ATTRIBUTES_SET = new HashSet<String>(Arrays.asList(FLOAT));
	
	/**
	 * set name of tag
	 */
	public ImageTag() {
		super(TAG_NAME);
	}
	
	@Override
	protected StringBuilder render() {
		final TagNode node = this;
		final Map<String, String> tagAtttributes = node.getAttributes();
		final StringBuilder renderedHTML = new StringBuilder();
		final String name = Utils.escapeXmlChars(this.requestedUser.getName());
		/*
		 * TODO: Kriegen wir die URLs aus dem JavaCode raus
		 * FIXME: tolowercase impl.
		 */
		if(ALLOWED_FLOAT_ATTRIBUTES.contains(tagAtttributes.get(FLOAT))){
			renderedHTML.append("<img src='/picture/user/").append(name).append("' style='float:").append(tagAtttributes.get(FLOAT)).append(";'>");
		} else {
			renderedHTML.append("<img src='/picture/user/").append(name).append("' style='float:right;'>");
		}
		return renderedHTML;
	}
	
	@Override
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES_SET.contains(attName);
	}

}
