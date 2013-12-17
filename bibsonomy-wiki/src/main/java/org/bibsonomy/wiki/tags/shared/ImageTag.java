package org.bibsonomy.wiki.tags.shared;

// Only used for style attributes.
//import java.util.HashSet;
//import java.util.Set;

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
  */
public class ImageTag extends SharedTag {
	private static final String TAG_NAME = "image";
	// We don't want any attributes. For now. ;)
	//private static final String STYLE = "style";
	//private final static Set<String> ALLOWED_ATTRIBUTES_SET = new HashSet<String>();

	/**
	 * set name of tag
	 */
	public ImageTag() {
		super(TAG_NAME);
	}

	@Override
	/**
	 * Checks if attName is an allowed attribute.
	 */
	public boolean isAllowedAttribute(final String attName) {
		return false; //ALLOWED_ATTRIBUTES_SET.contains(attName);
	}

	/**
	 * @see org.bibsonomy.wiki.tags.SharedTag#renderSharedTag(org.bibsonomy.wiki.tags.SharedTag.RequestType)
	 */
	@Override
	protected String renderSharedTag() {
//		final Map<String, String> tagAttributes = this.getAttributes();
		final StringBuilder renderedHTML = new StringBuilder();
		final String name = this.getRequestedName();
		renderedHTML.append("<div id='userImg'><img height='100px' src='/picture/user/").append(this.renderString(name)).append("' /></div>"); //.append("' style='").append(tagAtttributes.get(STYLE)).append("'>");
		return renderedHTML.toString();
	}

}
