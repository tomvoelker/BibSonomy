/**
 * BibSonomy CV Wiki - Wiki for user and group CVs
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.wiki.tags.shared.resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Layout;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.Sets;
import org.bibsonomy.util.SortUtils;
import org.bibsonomy.wiki.tags.SharedTag;

/**
 * TODO: abstract resource tag
 * 
 */
/*
 * TODO: add order by content type as a valid sort order
 */
/*
 * FIXME: escape ALL data coming from the database
 */
/*
 * @author philipp
 * 
 * @author Bernd Terbrack
 */
public class PublicationListTag extends SharedTag {
	/**
	 * 
	 */
	private static final String ORDER_TITLE = "title";

	/**
	 * 
	 */
	private static final String ORDER_AUTHOR = "author";

	/**
	 * 
	 */
	private static final String ORDER_YEAR = "year";

	/**
	 * 
	 */
	private static final String ORDER_DESC = "desc";

	/**
	 * 
	 */
	private static final String ORDER_ASC = "asc";

	private static final Log log = LogFactory.getLog(PublicationListTag.class);

	private static final String DEFAULT_LAYOUT = "plain";

	private static final String TAGS = "tags";
	private static final String LAYOUT = "layout";
	private static final String KEYS = "keys"; // this is used to sort the
												// publication list is sorted
												// Please rename it! (Take care
												// since users might already use
												// it):
	private static final String SORT = "sort";
	private static final String ORDER = "order";
	private static final String LIMIT = "limit";
	private static final String GROUP_BY = "groupby";
	private static final String TRUE = "true";

	private static final Set<String> ALLOWED_SORTPAGE_JABREF_LAYOUTS = Sets.asSet(ORDER_YEAR, ORDER_AUTHOR, ORDER_TITLE);
	private static final Set<String> ALLOWED_SORTPAGEORDER_JABREF_LAYOUTS = Sets.asSet(ORDER_ASC, ORDER_DESC);

	private static final String TAG_NAME = "publications";

	private static final Map<String, String> defaultOrder = new HashMap<String, String>();

	private final static Set<String> ALLOWED_ATTRIBUTES_SET = Sets.asSet(TAGS, LAYOUT, KEYS, ORDER, LIMIT, GROUP_BY, SORT);

	static {
		defaultOrder.put(ORDER_YEAR, ORDER_DESC);
		defaultOrder.put(ORDER_AUTHOR, ORDER_ASC);
		defaultOrder.put(ORDER_TITLE, ORDER_ASC);
	}

	/**
	 * sets the tag name
	 */
	public PublicationListTag() {
		super(TAG_NAME);
	}

	@Override
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES_SET.contains(attName);
	}

	// FIXME: a lot of code copy, please remove it
	@Override
	protected String renderSharedTag() {
		final StringBuilder renderedHTML = new StringBuilder();
		final Map<String, String> tagAttributes = this.getAttributes();
		String tags;
		if (!tagAttributes.containsKey(TAGS)) {
			tags = "myown"; // TODO: should be MyOwnSystemTag.NAME but adding
							// dependency to database module only for accessing
							// the constant?!
		} else {
			tags = tagAttributes.get(TAGS);
			// FIXME: Check if the attribute value is valid (i.e. a
			// space separated list of tags
		}
		/*
		 * We earlier used the tag KEYS for sorting. To still support old CVs we
		 * map it to the new SORT tag. If both occur, KEYS is ignored
		 */
		if (tagAttributes.containsKey(KEYS) && !tagAttributes.containsKey(SORT)) {
			tagAttributes.put(SORT, tagAttributes.get(KEYS));
		}

		// Check if there is no order by year and no tag to filter years.

		final String requestedName = this.getRequestedName();

		// Either I set a layout by hand, then I won't see the dropdown menu.
		// Otherwise I do not set a layout, then I can choose from a dropdown
		// menu.
		final boolean dropdownMenuEnabled = tagAttributes.get(LAYOUT) == null;

		if (dropdownMenuEnabled) {
			// Standard selected layout is plain.
			this.addDropDownMenu(renderedHTML, tags, requestedName);
		}

		/*
		 * get the publications, maybe restricted to a certain interval of
		 * years.
		 * 
		 * FIXME: We want these working in a different way. We want the
		 * publication's year, not the BibSonomy year of the posting.
		 */
		List<Post<BibTex>> posts = this.logic.getPosts(BibTex.class, this.getGroupingEntity(), requestedName, Arrays.asList(tags.split(" ")), null, null, null, null, null, null, 0, PostLogicInterface.MAX_QUERY_SIZE);
		BibTexUtils.removeDuplicates(posts);

		/*
		 * if the user wants to sort them, do so
		 */
		boolean sortPosts = false;
		final String sortValue = tagAttributes.get(SORT);
		if (ALLOWED_SORTPAGE_JABREF_LAYOUTS.contains(sortValue)) {
			String orderValue = tagAttributes.get(ORDER);
			if (null == orderValue) {
				orderValue = defaultOrder.get(sortValue);
				tagAttributes.put(ORDER, orderValue);
			}
			if (ALLOWED_SORTPAGEORDER_JABREF_LAYOUTS.contains(orderValue)) {
				sortPosts = true;
				BibTexUtils.sortBibTexList(posts, SortUtils.parseSortKeys(sortValue), SortUtils.parseSortOrders(orderValue));
			}
		}

		/*
		 * after the publications being sorted, cut the quantity if the user
		 * wants to
		 */
		if (tagAttributes.get(LIMIT) != null) {
			try {
				posts = posts.subList(0, Integer.parseInt(tagAttributes.get(LIMIT)));
			} catch (final IndexOutOfBoundsException e) {
				log.debug(e);
			} catch (final Exception e) {
				log.error(e);
			}
		}

		final String group = tagAttributes.get(GROUP_BY);
		// grouping publications is only supported for sorted lists, ordered by
		// year
		if (sortPosts && ORDER_YEAR.equals(tagAttributes.get(SORT)) && (TRUE.equals(group))) {

			final SortedMap<String, List<Post<BibTex>>> groupedPostsMap;
			if (ORDER_ASC.equals(tagAttributes.get(ORDER))) {
				groupedPostsMap = new TreeMap<String, List<Post<BibTex>>>();
			} else {
				groupedPostsMap = new TreeMap<String, List<Post<BibTex>>>(Collections.reverseOrder());
			}
			for (final Post<BibTex> post : posts) {
				final String year = post.getResource().getYear();
				List<Post<BibTex>> groupedList = groupedPostsMap.get(year);
				if (null == groupedList) {
					groupedList = new LinkedList<Post<BibTex>>();
					groupedPostsMap.put(year, groupedList);
				}
				groupedList.add(post);
			}
			for (final Entry<String, List<Post<BibTex>>> groupedListEntry : groupedPostsMap.entrySet()) {
				this.renderPublicationList(renderedHTML, tagAttributes, requestedName, groupedListEntry.getValue(), groupedListEntry.getKey());
			}
		} else {
			this.renderPublicationList(renderedHTML, tagAttributes, requestedName, posts, null);
		}
		return renderedHTML.toString();
	}

	private void renderPublicationList(final StringBuilder renderedHTML, final Map<String, String> tagAttributes, final String requestedName, final List<Post<BibTex>> posts, final String groupName) {
		try {
			Layout layout;
			if (null != tagAttributes.get(LAYOUT)) {
				layout = this.layoutRenderer.getLayout(tagAttributes.get(LAYOUT).toLowerCase(), requestedName);

				if (!layout.getMimeType().equals("text/html")) {
					layout = this.layoutRenderer.getLayout(DEFAULT_LAYOUT, requestedName);
				}
			} else {
				layout = this.layoutRenderer.getLayout(DEFAULT_LAYOUT, requestedName);
			}

			if (null != groupName) {
				renderedHTML.append("<h3 class=\"mw-headline level3\" level3>" + groupName + "</h3>");
			}
			renderedHTML.append("<div id='publications'>" + this.layoutRenderer.renderLayout(layout, posts, true) + "</div>"); // class='entry
																																// bibtex'
		} catch (final LayoutRenderingException e) {
			log.error(e.getMessage());
		} catch (final IOException e) {
			log.error(e.getMessage());
		}
	}

	private void addDropDownMenu(final StringBuilder renderedHTML, final String tags, final String requestedName) {
		final String selectedLayout = "plain";

		// TODO: Mehrere moegliche Layouts einbinden
		// (<a
		// href='/export/").append(this.getGroupingEntity().toString()).append("/").append(requestedName).append("/").append(tags).append("'
		// title='show all export formats (including RSS, CVS, ...)''>all
		// formats</a>):
		renderedHTML.append("<div><span id='citation_formats'><form name='citation_format_form' action='' " + "style='font-size:80%;'>" + this.messageSource.getMessage("bibtex.citation_format", new Object[] {}, this.locale) + ": <select size='1' name='layout' class='layout' onchange='return formatPublications(this,\"").append(this.getGroupingEntity().toString()).append("\")'>");

		for (final String layoutName : this.layoutRenderer.getLayouts().keySet()) {
			try {
				final Layout layout = this.layoutRenderer.getLayout(layoutName, requestedName);
				if (layout.getMimeType().equals("text/html") && layout.hasEmbeddedLayout()) {
					renderedHTML.append("<option value='" + layoutName + "'" + (selectedLayout.equals(layoutName) ? " selected" : "") + ">" + layoutName + "</option>");
				}
			} catch (final LayoutRenderingException e) {
				log.error(e.getMessage());
			} catch (final IOException e) {
				log.error(e.getMessage());
			}
		}
		renderedHTML.append("</select><input id='reqUser' type='hidden' value='").append(requestedName).append("' /><input id='reqTags' type='hidden' value='").append(tags).append("' /></form></span></div>");
	}

}
