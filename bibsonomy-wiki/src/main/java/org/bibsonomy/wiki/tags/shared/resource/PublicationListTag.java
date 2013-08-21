package org.bibsonomy.wiki.tags.shared.resource;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
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
	private static final String FROMYEAR = "fromyear";
	private static final String TOYEAR = "toyear";

	private static final Set<String> ALLOWED_SORTPAGE_JABREF_LAYOUTS = new HashSet<String>(Arrays.asList("year", "author", "title"));
	private static final Set<String> ALLOWED_SORTPAGEORDER_JABREF_LAYOUTS = new HashSet<String>(Arrays.asList("asc", "desc"));

	private static final String TAG_NAME = "publications";

	private final static Set<String> ALLOWED_ATTRIBUTES_SET = new HashSet<String>(Arrays.asList(TAGS, LAYOUT, KEYS, ORDER, QUANTITY)); //, FROMYEAR, TOYEAR));
	
	// TODO: Hard coding that is a bit ewww.
	// How can I get these layouts from bibsonomy-layout?
	private final static String HARVARD = "harvardhtml";
	private final static String PLAIN = "plain";
	private final static String DIN1505 = "din1505";
	private final static String SIMPLEHTML = "simplehtml";
	private final static Set<String> RENDERABLE_LAYOUTS = new HashSet<String>(Arrays.asList(PLAIN, HARVARD, DIN1505, SIMPLEHTML));

	/**
	 * sets the tag name
	 */
	public PublicationListTag() {
		super(TAG_NAME);
	}

	/**
	 * check if the requested sorting order is valid.
	 * @param tagAttributes HTML attributes given in the tag
	 * @return true, if the given sorting order is valid.
	 */
	private boolean checkSort(final Map<String, String> tagAttributes) {
		return ALLOWED_SORTPAGE_JABREF_LAYOUTS.contains(tagAttributes.get(KEYS)) && ALLOWED_SORTPAGEORDER_JABREF_LAYOUTS.contains(tagAttributes.get(ORDER));
	}

	@Override
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES_SET.contains(attName);
	}
	
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
			// FIXME: Check if the attribute value is valid (i.e. a 
			// space separated list of tags
		}

		final String requestedName = this.getRequestedName();
		
		// Either I set a layout by hand, then I won't see the dropdown menu.
		// Otherwise I do not set a layout, then I can choose from a dropdown menu.
		final boolean dropdownMenuEnabled = tagAttributes.get(LAYOUT) == null;
		
		if (dropdownMenuEnabled) {
			// Standard selected layout is plain.
			final String selectedLayout = "plain";
			
			// TODO: Mehrere moegliche Layouts einbinden
			// (<a href='/export/").append(this.getGroupingEntity().toString()).append("/").append(requestedName).append("/").append(tags).append("' title='show all export formats (including RSS, CVS, ...)''>all formats</a>):
			renderedHTML.append("<div><span id='citation_formats'><form name='citation_format_form' action='' " +
					"style='font-size:80%;'>" +	this.messageSource.getMessage("bibtex.citation_format", new Object[]{}, this.locale) +
					": <select size='1' name='layout' class='layout' onchange='return formatPublications(this,\"")
					.append(this.getGroupingEntity().toString()).append("\")'>");
			
			for (final String layoutName : this.layoutRenderer.getLayouts().keySet()) {
				try {
					Layout layout = this.layoutRenderer.getLayout(layoutName, requestedName);
					if (layout.getMimeType().equals("text/html") && layout.hasEmbeddedLayout())
						renderedHTML.append("<option value='" + layoutName + "'" + (selectedLayout.equals(layoutName)
								? " selected" : "") + ">" + layoutName + "</option>");
				} catch (LayoutRenderingException e) {
					log.error(e.getMessage());
				} catch (final IOException e) {
					log.error(e.getMessage());
				}
			}
			renderedHTML.append("</select><input id='reqUser' type='hidden' value='").append(requestedName).append("' /><input id='reqTags' type='hidden' value='").append(tags).append("' /></form></span></div>");
		}
		
		
		/*
		 * get the publications, maybe restricted to a certain interval of years.
		 * 
		 * FIXME: We want these working in a different way. We want the publication's year,
		 * not the BibSonomy year of the posting.
		 */
		final Date startYear = null;
		final Date endYear = null;
//		final Date startYear = extractYear(tagAttributes.get(FROMYEAR));
//		final Date endYear = extractYear(tagAttributes.get(TOYEAR));
		
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
			Layout layout;
			if (null != tagAttributes.get(LAYOUT)) {
				layout = this.layoutRenderer.getLayout(tagAttributes.get(LAYOUT).toLowerCase(), requestedName);
				
				if (!layout.getMimeType().equals("text/html")) {
					layout = this.layoutRenderer.getLayout(DEFAULT_LAYOUT, requestedName);
				}
			} else {
				layout = this.layoutRenderer.getLayout(DEFAULT_LAYOUT, requestedName);
			}
			
			renderedHTML.append("<div id='publications'>" + this.layoutRenderer.renderLayout(layout, posts, true) + "</div>"); // class='entry bibtex'
		} catch (final LayoutRenderingException e) {
			log.error(e.getMessage());
		} catch (final IOException e) {
			log.error(e.getMessage());
		}

		return renderedHTML.toString();
	}

	protected static Date extractYear(final String yearString) {
		if (!present(yearString)) {
			return null;
		}
		try {
			final Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, Integer.parseInt(yearString));
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.DATE, 1);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			return calendar.getTime();
		} catch (final NumberFormatException e) {
			// Do nothing.
		}
		
		return null;
	}
}
