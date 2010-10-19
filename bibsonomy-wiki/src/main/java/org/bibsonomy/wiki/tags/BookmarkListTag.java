package org.bibsonomy.wiki.tags;

import static org.bibsonomy.util.ValidationUtils.present;

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

/**
 * @author philipp
 * @version $Id$
 */
public class BookmarkListTag extends AbstractTag {
	private static final String NAME = "name";
	
	public static final String TAG_NAME = "bookmarklist";

	final static public Set<String> ALLOWED_ATTRIBUTES_SET = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(NAME)));
	
	public BookmarkListTag() {
		super(TAG_NAME);

	}
	
    @Override
    StringBuffer render() {
    	final LogicInterface logic = wikiUtil.getLogic();
    	StringBuffer renderedHTML = new StringBuffer();
    	
        Map<String, String> tagAtttributes = this.getAttributes();
        String bookName = tagAtttributes.get(NAME);
        
        if(!present(bookName))
        	return renderedHTML;
        
        List<Post<Bookmark>> bookmarks = logic.getPosts(Bookmark.class, GroupingEntity.USER, wikiUtil.getUser().getName(), Collections.singletonList(bookName), null, null, null, 0, Integer.MAX_VALUE, null);
        
        renderedHTML.append("<div class='align'>");
        renderedHTML.append("<ul id='liste' class='bookmarkList'>");
        
        for(Post<Bookmark> b : bookmarks) {
        	
        	renderedHTML.append("<div style='margin:1.2em;' class='entry'><li><span class='entry_title'>");
        	renderedHTML.append("<a href='" +b.getResource().getUrl() +"' rel='nofollow'>" +b.getResource().getTitle() +"</a>");
        	renderedHTML.append("</span>");
        	
        	if(!b.getDescription().isEmpty()) {
        		//TODO i18n [show details]
        		renderedHTML.append("<a class='hand'> [show details] </a>");
        		renderedHTML.append("<p class='details'>" +b.getDescription() +"</p>");
        	}
        	
        	renderedHTML.append("</li></div>");
        }
        
        renderedHTML.append("</ul>");
        renderedHTML.append("</div >");
        
    	return renderedHTML;
    }

}
