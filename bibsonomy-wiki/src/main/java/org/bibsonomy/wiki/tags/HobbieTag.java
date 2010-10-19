package org.bibsonomy.wiki.tags;


import static org.bibsonomy.util.ValidationUtils.present;
import info.bliki.htmlcleaner.Utils;
import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.tags.HTMLTag;
import info.bliki.wiki.tags.util.INoBodyParsingTag;

import java.io.IOException;

import org.bibsonomy.wiki.WikiUtil;

/**
 * @author philipp
 * @version $Id$
 */
public class HobbieTag extends HTMLTag implements INoBodyParsingTag {

		public static final String TAG_NAME = "hobbies";
	
        public HobbieTag() {
                super(TAG_NAME);
        }

        @Override
        public void renderHTML(ITextConverter converter, Appendable buf, IWikiModel model) throws IOException {
        	WikiUtil w = (WikiUtil) model;
        	String hobbies = w.getUser().getHobbies();
        	
        	if(!present(hobbies))
        		return;
        	
        	//FIXME here there has to be something like cv.hobbies!
        	//FIXME hobbies should be stored in escaped XHTML otherwise tags could be possible
        	buf.append("<p class='align'>" +Utils.escapeXmlChars(hobbies) +"</p>");
        	
        	
        }


}

