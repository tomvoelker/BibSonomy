package org.bibsonomy.wiki.tags;


import static org.bibsonomy.util.ValidationUtils.present;
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
		StringBuffer render() {
			StringBuffer renderedHTML = new StringBuffer();
        	String hobbies = wikiUtil.getUser().getHobbies();
        	
        	if(!present(hobbies))
        		return renderedHTML;
        	
        	renderedHTML.append("<p class='align'>" +Utils.escapeXmlChars(hobbies) +"</p>");
        	
        	return renderedHTML;
		}


}

