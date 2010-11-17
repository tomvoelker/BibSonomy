package org.bibsonomy.wiki.tags;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author philipp
 * @version $Id$
 */
public class PublicationListTag extends AbstractTag {
	
	private static final String NAME = "name";
	public static final String TAG_NAME = "publicationlist";

	public final static Set<String> ALLOWED_ATTRIBUTES_SET = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(NAME)));

	public PublicationListTag() {
		super(TAG_NAME);

	}

	@Override
	protected StringBuilder render() {
		final StringBuilder renderedHTML = new StringBuilder();
        final String tagName = this.getAttributes().get(NAME);
       
        if (!present(tagName)) {
        	return renderedHTML;
        }
        
        final String requestedUserName = this.requestedUser.getName();
        
        final List<Post<BibTex>> posts = this.logic.getPosts(BibTex.class, GroupingEntity.USER, requestedUserName, Collections.singletonList(tagName), null, null, null, 0, Integer.MAX_VALUE, null);
        
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
        
        return renderedHTML;
	}
}
