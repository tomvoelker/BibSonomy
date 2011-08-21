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
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.SortUtils;

/**
 * 
 * @author philipp
 * @author Bernd
 * @version $Id$
 */
public class PublicationListTag extends AbstractTag {

	private static final String NAME = "tags";
	private static final String LAYOUT = "layout";
	private static final String KEYS = "keys";
	private static final String ORDER = "order";
	private static final Set<String> ALLOWED_SORTPAGE_JABREF_LAYOUTS = new HashSet<String>(Arrays.asList("year","author","title")) ;
	private static final Set<String> ALLOWED_SORTPAGEORDER_JABREF_LAYOUTS = new HashSet<String>(Arrays.asList("asc","desc")) ;

	public static final String TAG_NAME = "publications";

	final static public HashSet<String> ALLOWED_ATTRIBUTES_SET = new HashSet<String>(
			Arrays.asList(NAME, LAYOUT, KEYS, ORDER));

	public PublicationListTag() {
		super(TAG_NAME);

	}

	// TODO Var names

	@SuppressWarnings("unchecked")
	@Override
	protected StringBuilder render() {
		final TagNode node = this;
		final StringBuilder renderedHTML = new StringBuilder();
		final Map<String, String> tagAtttributes = node.getAttributes();
		final Set<String> keysSet = tagAtttributes.keySet();

		final String tags;
		if (!keysSet.contains("tags")) {
			tags = "myown"; // TODO: should be MyOwnSystemTag.NAME but adding
							// dependency to database module only for accessing
							// the constant?!
		} else {
			tags = tagAtttributes.get("tags");
		}

		final String requestedUserName = this.requestedUser.getName();
		// <!-- onchange='changeCitationFormat()' -->
		renderedHTML
				.append("<div><span id='citation_formats'><form name='citation_format_form' action='' style='font-size:80%;'>Citation format (<a href='/export/user/"
						+ requestedUserName
						+ "/myown' title='show all export formats (including RSS, CVS, ...)'>all formats</a>): <select size='1' name='layout' id='layout'><option value='plain'>plain</option><option value='harvardhtml'>harvard</option><option value='din1505'>DIN1505</option><option value='simplehtml'>simpleHTML</option></select></form></span></div>");

		final List<? extends Post<? extends Resource>> posts = this.logic
				.getPosts(BibTex.class, GroupingEntity.USER, requestedUserName,
						Collections.singletonList(tags), null, null, null, 0,
						Integer.MAX_VALUE, null);
		if (checkSort(tagAtttributes)) {
			BibTexUtils.sortBibTexList((List<Post<BibTex>>) posts,
					SortUtils.parseSortKeys(tagAtttributes.get("keys")),
					SortUtils.parseSortOrders(tagAtttributes.get("order")));
		}

		Layout layout;

		try {
			if (null != tagAtttributes.get(LAYOUT)) {
				layout = this.layoutRenderer.getLayout(
						tagAtttributes.get(LAYOUT), requestedUserName);
			} else {
				layout = this.layoutRenderer.getLayout("plain",
						requestedUserName);
			}
			renderedHTML.append(this.layoutRenderer.renderLayout(layout, posts,
					false));
		} catch (final LayoutRenderingException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return renderedHTML;

	}

	private boolean checkSort(Map<String, String> tagAtttributes) {
		return ALLOWED_SORTPAGE_JABREF_LAYOUTS.contains(tagAtttributes.get("keys")) && ALLOWED_SORTPAGEORDER_JABREF_LAYOUTS.contains(tagAtttributes.get("order")) ? true: false;
	}

	@Override
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES_SET.contains(attName);
	}
}
