package org.bibsonomy.wiki.tags.shared;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.wiki.tags.SharedTag;

/**
 * Renders an image of a user / group.
 * 
 * Usage: 
 *   <image/>
 * 
 * Attributes:
 *   style=STYLE: css attributes for the produced <img> tag
 *   
 * Output:
 *   <img src="/picture/user/(USERNAME|GROUPNAME)/" style=STYLE/>
 * 
 * @author Bernd
 * @version $Id$
 */
public class ImageTag extends SharedTag {
	private static final String TAG_NAME = "image";
	private static final String STYLE = "style";

	private final static Set<String> ALLOWED_ATTRIBUTES_SET = new HashSet<String>(Arrays.asList(STYLE));

	/**
	 * set name of tag
	 */
	public ImageTag() {
		super(TAG_NAME);
	}

	@Override
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES_SET.contains(attName);
	}

	/**
	 * @see org.bibsonomy.wiki.tags.SharedTag#renderSharedTag(org.bibsonomy.wiki.tags.SharedTag.RequestType)
	 */
	@Override
	protected String renderSharedTag() {
		final Map<String, String> tagAtttributes = this.getAttributes();
		final StringBuilder renderedHTML = new StringBuilder();
		final String name = this.getRequestedName();
		renderedHTML.append("<img src='/picture/user/").append(this.renderString(name)).append("' style='").append(tagAtttributes.get(STYLE)).append("'>");
		return renderedHTML.toString();
	}

}
