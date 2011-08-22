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
 * TODO: abstract resource tag
 * 
 * @author philipp
 * @version $Id$
 */
public class BookmarkListTag extends AbstractTag {
	private static final String NAME = "tags";
	private static final String TAG_NAME = "bookmarks";

	private static final Set<String> ALLOWED_ATTRIBUTES = new HashSet<String>(Arrays.asList(NAME));

	/**
	 * sets the tag
	 */
	public BookmarkListTag() {
		super(TAG_NAME);
	}
	
    @Override
    protected StringBuilder render() {
 	   final TagNode node = this;
       
 		final StringBuilder renderedHTML = new StringBuilder();
 		
         final Map<String, String> tagAtttributes = node.getAttributes();
         final Set<String> keysSet = tagAtttributes.keySet();
         
 		final String tags;
 		if (!keysSet.contains(NAME)) {
 			tags = "myown"; // TODO: should be MyOwnSystemTag.NAME but adding
 							// dependency to database module only for accessing
 							// the constant?!
 		} else {
 			tags = tagAtttributes.get(NAME);
 		}
         
        final List<Post<Bookmark>> posts = this.logic.getPosts(Bookmark.class, GroupingEntity.USER, this.requestedUser.getName(), Collections.singletonList(tags), null, null, null, 0, Integer.MAX_VALUE, null);
        renderedHTML.append("<div class='align'>");
        renderedHTML.append("<ul id='liste' class='bookmarkList'>");
        
        for (final Post<Bookmark> post : posts) {
        	renderedHTML.append("<div style='margin:1.2em;' class='entry'><li><span class='entry_title'>");
        	renderedHTML.append("<a href='" +post.getResource().getUrl() +"' rel='nofollow'>" +post.getResource().getTitle() +"</a>");
        	renderedHTML.append("</span>");
        	
        	final String description = post.getDescription();
			if (present(description)) {
        		// TODO: i18n [show details]
        		renderedHTML.append("<a class='hand'> [show details] </a>");
        		renderedHTML.append("<p class='details'>" +description +"</p>");
        	}
        	
        	renderedHTML.append("</li></div>");
        }
        
        renderedHTML.append("</ul>");
        renderedHTML.append("</div >");
        
    	return renderedHTML;
    }

	@Override
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES.contains(attName);
	}
}
