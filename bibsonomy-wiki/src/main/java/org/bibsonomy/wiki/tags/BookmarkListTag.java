package org.bibsonomy.wiki.tags;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

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
    protected StringBuilder render() {
    	final StringBuilder renderedHTML = new StringBuilder();
        final String tagName = this.getAttributes().get(NAME);
        
        if (!present(tagName)) {
        	return renderedHTML;
        }
        
        final List<Post<Bookmark>> posts = this.logic.getPosts(Bookmark.class, GroupingEntity.USER, this.requestedUser.getName(), Collections.singletonList(tagName), null, null, null, 0, Integer.MAX_VALUE, null);
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

}
