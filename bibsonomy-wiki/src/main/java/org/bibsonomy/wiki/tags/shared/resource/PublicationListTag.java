package org.bibsonomy.wiki.tags.shared.resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Layout;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.SortUtils;
import org.bibsonomy.wiki.tags.SharedResourceTag;

/**
 * TODO: abstract resource tag
 * 
 * @author philipp
 * @author Bernd Terbrack
 * @version $Id$
 */
public class PublicationListTag extends SharedResourceTag {

	private static final String DEFAULT_LAYOUT = "plain";

	private static final Log log = LogFactory.getLog(PublicationListTag.class);

	private static final String TAGS = "tags";
	private static final String LAYOUT = "layout";
	private static final String KEYS = "keys";
	private static final String ORDER = "order";
	private static final String QUANTITY = "qty";
	private static final Set<String> ALLOWED_SORTPAGE_JABREF_LAYOUTS = new HashSet<String>(Arrays.asList("year", "author", "title"));
	private static final Set<String> ALLOWED_SORTPAGEORDER_JABREF_LAYOUTS = new HashSet<String>(Arrays.asList("asc", "desc"));

	private static final String TAG_NAME = "publications";

	private final static Set<String> ALLOWED_ATTRIBUTES_SET = new HashSet<String>(Arrays.asList(TAGS, LAYOUT, KEYS, ORDER, QUANTITY));

	/**
	 * sets the tag name
	 */
	public PublicationListTag() {
		super(TAG_NAME);
	}

	private boolean checkSort(final Map<String, String> tagAtttributes) {
		return ALLOWED_SORTPAGE_JABREF_LAYOUTS.contains(tagAtttributes.get(KEYS)) && ALLOWED_SORTPAGEORDER_JABREF_LAYOUTS.contains(tagAtttributes.get(ORDER)) ? true : false;
	}

	@Override
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES_SET.contains(attName);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected String renderSharedTag(final RequestType requestType) {
		final StringBuilder renderedHTML = new StringBuilder();
		final Map<String, String> tagAtttributes = this.getAttributes();
		final Set<String> keysSet = tagAtttributes.keySet();
		final String tags;
		if (!keysSet.contains(TAGS)) {
			tags = "myown"; // TODO: should be MyOwnSystemTag.NAME but adding
							// dependency to database module only for accessing
							// the constant?!
		} else {
			tags = tagAtttributes.get(TAGS);
		}

		final String requestedName = this.getRequestedName(requestType);

		renderedHTML.append("<div><span id='citation_formats'><form name='citation_format_form' action='' style='font-size:80%;'>Citation format (<a href='/export/").append(requestType.getType()).append("/").append(requestedName).append("/").append(tags).append("' title='show all export formats (including RSS, CVS, ...)''>all formats</a>): <select size='1' name='layout' class='layout' onchange='return formatPublications(this,\"").append(requestType.getType()).append("\")'>");
		renderedHTML.append("<option value='plain'>plain</option><option value='harvardhtml'>harvard</option><option value='din1505'>DIN1505</option><option value='simplehtml'>simpleHTML</option>");
		renderedHTML.append("</select><input type='hidden' value='").append(tags).append("' /></form></span></div>");

		/*
		 * get the publications
		 */
		List<Post<BibTex>> posts = this.logic.getPosts(BibTex.class, requestType.getGroupingEntity(), requestedName, Collections.singletonList(tags), null, null, null, 0, Integer.MAX_VALUE, null);
		BibTexUtils.removeDuplicates(posts);

		/*
		 * if the user wants to sort them, do so
		 */
		if (this.checkSort(tagAtttributes)) {
			BibTexUtils.sortBibTexList(posts, SortUtils.parseSortKeys(tagAtttributes.get(KEYS)), SortUtils.parseSortOrders(tagAtttributes.get(ORDER)));
		}
		
		/*
		 * after the publications being sorted, cut the quantity if the user wants to
		 */
		if (tagAtttributes.get(QUANTITY) != null) {
			try {
				posts = posts.subList(0, Integer.parseInt(tagAtttributes.get(QUANTITY)));
			} catch (final IndexOutOfBoundsException e) {
				log.debug(e);
			} catch (final Exception e) {
				log.error(e);
			}
		}

		/*
		 * and finally use the chosen layout (plain by def.)
		 */
		try {
			final Layout layout;
			if (null != tagAtttributes.get(LAYOUT)) {
				layout = this.layoutRenderer.getLayout(tagAtttributes.get(LAYOUT), requestedName);
			} else {
				layout = this.layoutRenderer.getLayout(DEFAULT_LAYOUT, requestedName);
			}
			renderedHTML.append("<div class='entry bibtex'>" + this.layoutRenderer.renderLayout(layout, posts, false) + "</div>");
		} catch (final LayoutRenderingException e) {
			log.error(e.getMessage());
		} catch (final IOException e) {
			log.error(e.getMessage());
		}

		return renderedHTML.toString();
	}
}
