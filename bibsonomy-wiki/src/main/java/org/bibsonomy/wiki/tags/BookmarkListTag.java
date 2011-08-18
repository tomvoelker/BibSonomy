package org.bibsonomy.wiki.tags;

import static org.bibsonomy.util.ValidationUtils.present;

import info.bliki.htmlcleaner.TagNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * @author philipp
 * @version $Id$
 */
public class BookmarkListTag extends AbstractTag {
	private static final String NAME = "tags";
	public static final String TAG_NAME = "bookmarklist";

	final static public HashSet<String> ALLOWED_ATTRIBUTES_SET = new HashSet<String>();
	final static public List<String> ALLOWED_ATTRIBUTES = Arrays.asList(NAME);

	static {
		for (String attr : ALLOWED_ATTRIBUTES) {
			ALLOWED_ATTRIBUTES_SET.add(attr);
		}
	}

	
	public BookmarkListTag() {
		super(TAG_NAME);

	}
	
    @Override
    protected StringBuilder render() {
 	   TagNode node = this;
       
 		final StringBuilder renderedHTML = new StringBuilder();
 		
         Map<String, String> tagAtttributes = node.getAttributes();
         Set<String> keysSet = tagAtttributes.keySet();
         
         if(!keysSet.contains("tags")){
         	return renderedHTML;
         }
        
         String tags = tagAtttributes.get("tags");
         
        final List<Post<Bookmark>> posts = this.logic.getPosts(Bookmark.class, GroupingEntity.USER, this.requestedUser.getName(), Collections.singletonList(tags), null, null, null, 0, Integer.MAX_VALUE, null);
        renderedHTML.append("<div class='align'>");
        renderedHTML.append("<ul id='liste' class='bookmarkList'>");
        
        for (final Post<Bookmark> post : posts) {
        	
        	renderedHTML.append("<div style='margin:1.2em;' class='entry'><li><span class='entry_title'>");
        	renderedHTML.append("<a href='" +post.getResource().getUrl() +"' rel='nofollow'>" +post.getResource().getTitle() +"</a>");
        	renderedHTML.append("</span>");
        	
        	final String description = post.getDescription();
			if (present(description)) {
        		//TODO i18n [show details]
        		renderedHTML.append("<a class='hand'> [show details] </a>");
        		renderedHTML.append("<p class='details'>" +description +"</p>");
        	}
        	
        	renderedHTML.append("</li></div>");
        }
        
        renderedHTML.append("</ul>");
        renderedHTML.append("</div >");
        
    	return renderedHTML;
    }

	public boolean isAllowedAttribute(String attName) {
		return ALLOWED_ATTRIBUTES_SET.contains(attName);
	}
}
