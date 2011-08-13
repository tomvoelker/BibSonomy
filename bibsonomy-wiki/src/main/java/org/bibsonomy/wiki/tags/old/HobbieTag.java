package org.bibsonomy.wiki.tags.old;


import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.wiki.tags.AbstractTag;

import info.bliki.htmlcleaner.Utils;

/**
 * @author philipp
 * @version $Id$
 */
public class HobbieTag extends AbstractTag {

		public static final String TAG_NAME = "hobbies";
	
        public HobbieTag() {
                super(TAG_NAME);
        }

		@Override
		protected StringBuilder render() {
			final StringBuilder renderedHTML = new StringBuilder();
        	final String hobbies = this.requestedUser.getHobbies();
        	
        	if (!present(hobbies))
        		return renderedHTML;
        	
        	renderedHTML.append("<p class='align'>");
        	renderedHTML.append(Utils.escapeXmlChars(hobbies));
        	renderedHTML.append("</p>");
        	
        	return renderedHTML;
		}


}

