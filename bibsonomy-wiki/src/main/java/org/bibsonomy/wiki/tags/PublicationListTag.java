package org.bibsonomy.wiki.tags;

import info.bliki.htmlcleaner.TagNode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Layout;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author philipp
 * @version $Id$
 */
public class PublicationListTag extends AbstractTag {

	private static final String NAME = "tags";
	private static final String LAYOUT = "layout";
	
	
	public static final String TAG_NAME = "publicationlist";

	final static public HashSet<String> ALLOWED_ATTRIBUTES_SET = new HashSet<String>(Arrays.asList(NAME, LAYOUT));

	public PublicationListTag() {
		super(TAG_NAME);

	}

	//TODO Var names
	
	@Override
	protected StringBuilder render() {
	   final TagNode node = this;
	       
		final StringBuilder renderedHTML = new StringBuilder();
		
        final Map<String, String> tagAtttributes = node.getAttributes();
        final Set<String> keysSet = tagAtttributes.keySet();
        
        final String tags;
        if(!keysSet.contains("tags")){
        	tags = "myown"; // TODO: should be MyOwnSystemTag.NAME but adding dependency to database module only for accessing the constant?!
        } else {
        	tags = tagAtttributes.get("tags");
        }
        
        final String requestedUserName = this.requestedUser.getName();
        
        final List<? extends Post<? extends Resource>> posts = this.logic.getPosts(BibTex.class, GroupingEntity.USER, requestedUserName, Collections.singletonList(tags), null, null, null, 0, Integer.MAX_VALUE, null);
        
        Layout layout22;
		try {
			layout22 = this.layoutRenderer.getLayout(tagAtttributes.get(LAYOUT), requestedUserName);
			renderedHTML.append(this.layoutRenderer.renderLayout(layout22, posts, false));
		} catch (final LayoutRenderingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        
        
        /*
        
        renderedHTML.append("<div class='align'>");
        renderedHTML.append("<ul id='liste' class='bookmarkList'>");
        
        for (final Post<BibTex> post : posts) {
        	final BibTex publication = post.getResource();
        	
        	renderedHTML.append("<div style='margin:1.2em;' class='entry'><li><span class='entry_title'>");
        	
        	// TODO: use an URLGenerator for generating the publication link
        	renderedHTML.append("<a href='/bibtex/2");
			renderedHTML.append(publication.getIntraHash());
        	renderedHTML.append("/");
			renderedHTML.append(requestedUserName);
        	renderedHTML.append("' rel='nofollow'>");
        	renderedHTML.append(publication.getTitle());
        	renderedHTML.append("</a>");
        	renderedHTML.append("</span>");
        	
        	final String description = post.getDescription();
			if (present(description)) {
        		renderedHTML.append("<a class='hand'> [show details] </a>"); // TODO i18n [show details]
        		renderedHTML.append("<p class='details'>");
        		renderedHTML.append(description);
        		renderedHTML.append("</p>");
        	}
        	
        	renderedHTML.append("</li></div>");
        }
        
        renderedHTML.append("</ul>");
        renderedHTML.append("</div >");
        */
        return renderedHTML;
	}

	@Override
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES_SET.contains(attName);
	}
}
