package org.bibsonomy.wiki.tags.post;

import info.bliki.htmlcleaner.TagNode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Layout;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.SortUtils;
import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * TODO: abstract resource tag
 * 
 * @author philipp
 * @author Bernd Terbrack
 * @version $Id: PublicationListTag.java,v 1.9 2011-08-22 13:16:07 nosebrain Exp
 *          $
 */
public class PublicationListTag extends AbstractTag {

	private static final String DEFAULT_LAYOUT = "plain";

	private static final Log log = LogFactory.getLog(PublicationListTag.class);

	private static final String TAGS = "tags";
	private static final String LAYOUT = "layout";
	private static final String KEYS = "keys";
	private static final String ORDER = "order";
	private static final Set<String> ALLOWED_SORTPAGE_JABREF_LAYOUTS = new HashSet<String>(Arrays.asList("year", "author", "title"));
	private static final Set<String> ALLOWED_SORTPAGEORDER_JABREF_LAYOUTS = new HashSet<String>(Arrays.asList("asc", "desc"));

	private static final String TAG_NAME = "publications";

	private final static Set<String> ALLOWED_ATTRIBUTES_SET = new HashSet<String>(Arrays.asList(TAGS, LAYOUT, KEYS, ORDER));

	/**
	 * sets the tag name
	 */
	public PublicationListTag() {
		super(TAG_NAME);
	}

	@Override
	protected String render() {
		final TagNode node = this;
		final StringBuffer renderedHTML = new StringBuffer();
		final Map<String, String> tagAtttributes = node.getAttributes();
		final Set<String> keysSet = tagAtttributes.keySet();

		final String tags;
		if (!keysSet.contains(TAGS)) {
			tags = "myown"; // TODO: should be MyOwnSystemTag.NAME but adding
							// dependency to database module only for accessing
							// the constant?!
		} else {
			tags = tagAtttributes.get(TAGS);
		}

		final String requestedUserName = this.requestedUser.getName();
		
		renderedHTML.append("<div><span id='citation_formats'><form name='citation_format_form' action='' style='font-size:80%;'>Citation format (<a href='/export/user/" + requestedUserName + "/" +tags + "' title='show all export formats (including RSS, CVS, ...)''>all formats</a>): <select size='1' name='layout' class='layout' onchange='return formatPublications(this)'>");
		renderedHTML.append("<option value='plain'>plain</option><option value='harvardhtml'>harvard</option><option value='din1505'>DIN1505</option><option value='simplehtml'>simpleHTML</option>");
		renderedHTML.append("</select><input type='hidden' value='"+tags+"' /></form></span></div>");

		final List<Post<BibTex>> posts = this.logic.getPosts(BibTex.class, GroupingEntity.USER, requestedUserName, Collections.singletonList(tags), null, null, null, 0, Integer.MAX_VALUE, null);
		if (this.checkSort(tagAtttributes)) {
			BibTexUtils.sortBibTexList(posts, SortUtils.parseSortKeys(tagAtttributes.get(KEYS)), SortUtils.parseSortOrders(tagAtttributes.get(ORDER)));
		}

		try {
			final Layout layout;
			if (null != tagAtttributes.get(LAYOUT)) {
				layout = this.layoutRenderer.getLayout(tagAtttributes.get(LAYOUT), requestedUserName);
			} else {
				layout = this.layoutRenderer.getLayout(DEFAULT_LAYOUT, requestedUserName);
			}
			renderedHTML.append("<div>" +this.layoutRenderer.renderLayout(layout, posts, false) +"</div>");
		} catch (final LayoutRenderingException e) {
			log.error(e.getMessage());
		} catch (final IOException e) {
			log.error(e.getMessage());
		}

		return renderedHTML.toString();
	}

	private boolean checkSort(final Map<String, String> tagAtttributes) {
		return ALLOWED_SORTPAGE_JABREF_LAYOUTS.contains(tagAtttributes.get(KEYS)) && ALLOWED_SORTPAGEORDER_JABREF_LAYOUTS.contains(tagAtttributes.get(ORDER)) ? true : false;
	}

	@Override
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES_SET.contains(attName);
	}
}
