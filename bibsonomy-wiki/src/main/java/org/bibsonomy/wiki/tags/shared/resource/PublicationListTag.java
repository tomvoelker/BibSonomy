package org.bibsonomy.wiki.tags.shared.resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
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
import org.bibsonomy.wiki.tags.SharedTag;

/**
 * TODO: abstract resource tag
 * 
 * FIXME: escape ALL data coming from the database
 * 
 * @author philipp
 * @author Bernd Terbrack
 * @version $Id$
 */
public class PublicationListTag extends SharedTag {

	private static final String DEFAULT_LAYOUT = "plain";

	private static final Log log = LogFactory.getLog(PublicationListTag.class);

	private static final String TAGS = "tags";
	private static final String LAYOUT = "layout";
	private static final String KEYS = "keys";
	private static final String ORDER = "order";
	private static final String QUANTITY = "qty";
	private static final String DROPDOWN = "dropdown";
	private static final String FROMYEAR = "fromyear";
	private static final String TOYEAR = "toyear";
	private static final Set<String> ALLOWED_SORTPAGE_JABREF_LAYOUTS = new HashSet<String>(Arrays.asList("year", "author", "title"));
	private static final Set<String> ALLOWED_SORTPAGEORDER_JABREF_LAYOUTS = new HashSet<String>(Arrays.asList("asc", "desc"));

	private static final String TAG_NAME = "publications";

	private final static Set<String> ALLOWED_ATTRIBUTES_SET = new HashSet<String>(Arrays.asList(TAGS, LAYOUT, KEYS, ORDER, QUANTITY, DROPDOWN, FROMYEAR, TOYEAR));

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
	protected String renderSharedTag() {
		final StringBuilder renderedHTML = new StringBuilder();
		final Map<String, String> tagAttributes = this.getAttributes();
		final Set<String> keysSet = tagAttributes.keySet();
		final String tags;
		if (!keysSet.contains(TAGS)) {
			tags = "myown"; // TODO: should be MyOwnSystemTag.NAME but adding
							// dependency to database module only for accessing
							// the constant?!
		} else {
			tags = tagAttributes.get(TAGS);
		}

		final String requestedName = this.getRequestedName();
		
		
		final boolean dropdownMenuEnabled = tagAttributes.get(DROPDOWN) != null ? tagAttributes.get(DROPDOWN).toLowerCase().equals("true") : true;
		
		if (dropdownMenuEnabled) {
			final String selectedLayout = tagAttributes.get(LAYOUT).toLowerCase();
	
			// TODO: Mehrere moegliche Layouts einbinden
			// (<a href='/export/").append(this.getGroupingEntity().toString()).append("/").append(requestedName).append("/").append(tags).append("' title='show all export formats (including RSS, CVS, ...)''>all formats</a>):
			renderedHTML.append("<div><span id='citation_formats'><form name='citation_format_form' action='' style='font-size:80%;'>Citation format: <select size='1' name='layout' class='layout' onchange='return formatPublications(this,\"").append(this.getGroupingEntity().toString()).append("\")'>");
			renderedHTML.append("<option value='plain'" + (selectedLayout.equals("plain") ? " selected" : "") + ">plain</option>");
			renderedHTML.append("<option value='harvardhtml'" + (selectedLayout.equals("harvardhtml") ? " selected" : "") + ">harvardhtml</option>");
			renderedHTML.append("<option value='din1505'" + (selectedLayout.equals("din1505") ? " selected" : "") + ">din1505</option>");
			renderedHTML.append("<option value='simplehtml'" + (selectedLayout.equals("simplehtml") ? " selected" : "") + ">simplehtml</option>");
			renderedHTML.append("</select><input id='reqUser' type='hidden' value='").append(requestedName).append("' /><input id='reqTags' type='hidden' value='").append(tags).append("' /></form></span></div>");
		}
		
		
		/*
		 * get the publications, maybe restricted to a certain interval of years.
		 */
		Date startYear = null, endYear = null;
		if (tagAttributes.get(FROMYEAR) != null) {
			try {
				startYear = new Date(Integer.parseInt(tagAttributes.get(FROMYEAR)) - 1900, 0, 0);
			} catch (NumberFormatException e) {
				// Do nothing.
			}
		}
		
		if (tagAttributes.get(TOYEAR) != null) {
			try {
				endYear = new Date(Integer.parseInt(tagAttributes.get(TOYEAR)) - 1900, 0, 0);
			} catch (NumberFormatException e) {
				// Do nothing.
			}
		}
		List<Post<BibTex>> posts = this.logic.getPosts(BibTex.class, this.getGroupingEntity(), requestedName, Arrays.asList(tags.split(" ")), null, null, null, null, startYear, endYear, 0, Integer.MAX_VALUE);
		BibTexUtils.removeDuplicates(posts);
		
		/*
		 * if the user wants to sort them, do so
		 */
		if (this.checkSort(tagAttributes)) {
			BibTexUtils.sortBibTexList(posts, SortUtils.parseSortKeys(tagAttributes.get(KEYS)), SortUtils.parseSortOrders(tagAttributes.get(ORDER)));
		}

		/*
		 * after the publications being sorted, cut the quantity if the user wants to
		 */
		if (tagAttributes.get(QUANTITY) != null) {
			try {
				posts = posts.subList(0, Integer.parseInt(tagAttributes.get(QUANTITY)));
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
			if (null != tagAttributes.get(LAYOUT)) {
				layout = this.layoutRenderer.getLayout(tagAttributes.get(LAYOUT).toLowerCase(), requestedName);
			} else {
				layout = this.layoutRenderer.getLayout(DEFAULT_LAYOUT, requestedName);
			}
			
			renderedHTML.append("<div id='publications'>" + this.layoutRenderer.renderLayout(layout, posts, false) + "</div>"); // class='entry bibtex'
		} catch (final LayoutRenderingException e) {
			log.error(e.getMessage());
		} catch (final IOException e) {
			log.error(e.getMessage());
		}

		return renderedHTML.toString();
	}
}
