package org.bibsonomy.wiki.tags;

import static org.bibsonomy.util.ValidationUtils.present;
import info.bliki.htmlcleaner.Utils;
/**
 * @author philipp
 * @version $Id$
 */
public class InterestsTag extends AbstractTag {
	
	public static final String TAG_NAME = "interests";
	
	/**
	 * 
	 */
	public InterestsTag() {
        super(TAG_NAME);
	}

	@Override
	StringBuffer render() {
		StringBuffer renderedHTML = new StringBuffer();
     	String interests = wikiUtil.getUser().getInterests();
     	
     	if(!present(interests))
     		return renderedHTML;

     	renderedHTML.append("<p class='align'>" +Utils.escapeXmlChars(interests) +"</p>");
     	
     	return renderedHTML;
	}
}
