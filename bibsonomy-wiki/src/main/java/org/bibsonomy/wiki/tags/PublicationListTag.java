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
 * 
 * @author philipp
 * @author Bernd
 * @version $Id$
 */
public class PublicationListTag extends AbstractTag {

	private static final String NAME = "tags";
	private static final String LAYOUT = "layout";
	
	
	public static final String TAG_NAME = "publications";

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
        Layout layout;
        
		try {
			if(null != tagAtttributes.get(LAYOUT)) {
				layout = this.layoutRenderer.getLayout(tagAtttributes.get(LAYOUT), requestedUserName);
			} else {
				layout = this.layoutRenderer.getLayout("simplehtml", requestedUserName);
			}
			renderedHTML.append(this.layoutRenderer.renderLayout(layout, posts, false));
		} catch (final LayoutRenderingException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
		return renderedHTML;
		
	}

	@Override
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES_SET.contains(attName);
	}
}
