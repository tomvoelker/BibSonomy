package org.bibsonomy.wiki.tags.shared;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.services.URLGenerator;
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
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES_SET.contains(attName);
	}

	/**
	 * TODO: Kriegen wir die URLs aus dem JavaCode raus? - Ja mittels des {@link URLGenerator} 
	 * 
	 * @see org.bibsonomy.wiki.tags.SharedTag#renderSharedTag(org.bibsonomy.wiki.tags.SharedTag.RequestType)
	 */
	@Override
	protected String renderSharedTag(final RequestType requestType) {
		final Map<String, String> tagAtttributes = this.getAttributes();
		final StringBuilder renderedHTML = new StringBuilder();
		final String name = this.getRequestedName(requestType);
		renderedHTML.append("<img src='/picture/user/").append(this.renderString(name)).append("' style='").append(tagAtttributes.get(FLOAT)).append("'>");
		return renderedHTML.toString();
	}

}
