package org.bibsonomy.wiki.tags.aboutme;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.wiki.tags.AbstractTag;

import info.bliki.htmlcleaner.Utils;

/**
 * @author philipp
 * @version $Id$
 */
public class InterestsTag extends AbstractTag {
	
	private static final String TAG_NAME = "interests";
	
	/**
	 * 
	 */
	public InterestsTag() {
        super(TAG_NAME);
	}

	@Override
	protected StringBuilder render() {
		final StringBuilder renderedHTML = new StringBuilder();
     	final String interests = this.requestedUser.getInterests();
     	
     	if (!present(interests)) {
     		return renderedHTML;
     	}
     	
     	renderedHTML.append("<p class='align'>");
     	renderedHTML.append(Utils.escapeXmlChars(interests));
     	renderedHTML.append("</p>");
     	
     	return renderedHTML;
	}
}
