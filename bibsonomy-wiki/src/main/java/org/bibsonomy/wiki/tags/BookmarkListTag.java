package org.bibsonomy.wiki.tags;

import static org.bibsonomy.util.ValidationUtils.present;
import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.tags.HTMLTag;
import info.bliki.wiki.tags.util.INoBodyParsingTag;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.wiki.WikiUtil;

/**
 * @author philipp
 * @version $Id$
 */
public class BookmarkListTag extends HTMLTag implements INoBodyParsingTag {
	private static final String NAME = "name";
	
	public static final String TAG_NAME = "bookmarklist";

	final static public Set<String> ALLOWED_ATTRIBUTES_SET = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(NAME)));
	
	public BookmarkListTag() {
		super(TAG_NAME);

	}
	


    @Override
    public void renderHTML(ITextConverter converter, Appendable buf, IWikiModel model) throws IOException {
    	WikiUtil w = (WikiUtil) model;
    	final LogicInterface command = w.getLogic();
    	
        Map<String, String> tagAtttributes = this.getAttributes();
        String bookName = tagAtttributes.get(NAME);
        
        if(!present(bookName))
        	return;
        
        List<Post<Bookmark>> bookmarks = command.getPosts(Bookmark.class, GroupingEntity.USER, w.getUser().getName(), Collections.singletonList(bookName), null, null, null, 0, Integer.MAX_VALUE, null);
        
        buf.append("<div class='align'>");
        buf.append("<ul id='liste' class='bookmarkList'>");
        
        for(Post<Bookmark> b : bookmarks) {
        	
        	buf.append("<div style='margin:1.2em;' class='entry'><li><span class='entry_title'>");
        	buf.append("<a href='" +b.getResource().getUrl() +"' rel='nofollow'>" +b.getResource().getTitle() +"</a>");
        	buf.append("</span>");
        	
        	if(!b.getDescription().isEmpty()) {
        		buf.append("<a class='hand'> [show details] </a>");
        		buf.append("<p class='details'>" +b.getDescription() +"</p>");
        	}
        	
        	buf.append("</li></div>");
        }
        
        buf.append("</ul>");
        buf.append("</div >");
        
    	
    }
    

    @Override
    public boolean isAllowedAttribute(String attName) {
            return ALLOWED_ATTRIBUTES_SET.contains(attName);
    }

}
