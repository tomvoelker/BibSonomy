package org.bibsonomy.wiki.tags;

import static org.bibsonomy.util.ValidationUtils.present;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.LogicInterface;

/**
 * @author philipp
 * @version $Id$
 */
public class PublicationListTag extends AbstractTag {
	
	static final String NAME = "name";

	public static final String TAG_NAME = "publicationlist";

	final static public Set<String> ALLOWED_ATTRIBUTES_SET = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(NAME)));

	public PublicationListTag() {
		super(TAG_NAME);

	}

	@Override
	StringBuffer render() {
		StringBuffer renderedHTML = new StringBuffer();
    	final LogicInterface command = wikiUtil.getLogic();
    	
        Map<String, String> tagAtttributes = this.getAttributes();
        String bibName = tagAtttributes.get(NAME);
       
        if(!present(bibName))
        	return renderedHTML;
        
        List<Post<BibTex>> bibtex = command.getPosts(BibTex.class, GroupingEntity.USER, wikiUtil.getUser().getName(), Collections.singletonList(bibName), null, null, null, 0, Integer.MAX_VALUE, null);
        
        renderedHTML.append("<div class='align'>");
        renderedHTML.append("<ul id='liste' class='bookmarkList'>");
        
        for(Post<BibTex> b : bibtex) {
        	
        	renderedHTML.append("<div style='margin:1.2em;' class='entry'><li><span class='entry_title'>");
        	renderedHTML.append("<a href='/bibtex/2"  +b.getResource().getIntraHash() +"/" +wikiUtil.getUser().getName() +"' rel='nofollow'>" +b.getResource().getTitle() +"</a>");
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
