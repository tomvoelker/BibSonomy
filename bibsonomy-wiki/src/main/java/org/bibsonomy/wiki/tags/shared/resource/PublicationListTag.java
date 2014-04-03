package org.bibsonomy.wiki.tags.shared.resource;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import org.bibsonomy.util.Sets;
import org.bibsonomy.util.SortUtils;
import org.bibsonomy.wiki.tags.SharedTag;

/**
 * TODO: abstract resource tag
 * 
 * FIXME: escape ALL data coming from the database
 * 
 * @author philipp
 * @author Bernd Terbrack
 */
public class PublicationListTag extends SharedTag {
	private static final Log log = LogFactory.getLog(PublicationListTag.class);

	private static final String DEFAULT_LAYOUT = "plain";

	private static final String TAGS = "tags";
	private static final String LAYOUT = "layout";
	private static final String KEYS = "keys";
	private static final String ORDER = "order";
	private static final String LIMIT = "limit";
	private static final String YEAR = "year";

	private static final Set<String> ALLOWED_SORTPAGE_JABREF_LAYOUTS = Sets.asSet("year", "author", "title");
	private static final Set<String> ALLOWED_SORTPAGEORDER_JABREF_LAYOUTS = Sets.asSet("asc", "desc");

	private static final String TAG_NAME = "publications";

	private final static Set<String> ALLOWED_ATTRIBUTES_SET = Sets.asSet(TAGS, LAYOUT, KEYS, ORDER, LIMIT, YEAR);


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
	/**
	 * Check if there is a request to sort by year.
	 * @param tagAttributes HTML attributes given in the tag.
	 * @return true, if there is a request to sort by year.
	 */
	private boolean checkSortByYear(final Map<String, String> tagAttributes){
		return "year".equals(tagAttributes.get(KEYS)) && ALLOWED_SORTPAGEORDER_JABREF_LAYOUTS.contains(tagAttributes.get(ORDER));
	}
	
	/**
	 * Check if the tag contains a year filter.
	 * @param tags, list of tags.
	 * @return true, if the tags list contains year.
	 */
	private boolean checkYearTag(final List<String> tags){
		for(String tag : tags){
			if(tag.contains("year") || tag.contains("YEAR")){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if the attribute set contains year.
	 * @param tagAttributes HTM: attributes given in the tag.
	 * @return true, if there is a YEAR attribute.
	 */
	private boolean checkForYearAttribute(final Map<String, String> tagAttributes){
		final Set<String> keysSet = tagAttributes.keySet();
		return keysSet.contains(YEAR);
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
		final Set<String> keysSet = tagAttributes.keySet();
		String tags;
		if (!keysSet.contains(TAGS)) {
			tags = "myown"; // TODO: should be MyOwnSystemTag.NAME but adding
							// dependency to database module only for accessing
							// the constant?!
		} else {
			tags = tagAttributes.get(TAGS);
			// FIXME: Check if the attribute value is valid (i.e. a 
			// space separated list of tags
		}
		
		// Check if there is no order by year and no tag to filter years.
		boolean getAllPosts = false;
		if(!checkSortByYear(tagAttributes) && !checkYearTag(Arrays.asList(tags.split(" ")))){
			getAllPosts = true;
		}
		
		if (checkForYearAttribute(tagAttributes)) {
			getAllPosts = false;
			tags = tagAttributes.get(TAGS) + " sys:year:" + tagAttributes.get(YEAR);
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
		
		// If getAllPosts is true, retrieve all the posts and sort them under a header for each year. Else, get the posts that matches the user tags.
		if (getAllPosts) {
			List<String> yearList = new ArrayList<String>();
			List<Post<BibTex>> allPosts = this.logic.getPosts(BibTex.class, this.getGroupingEntity(), requestedName, Arrays.asList(tags.split(" ")), null, null, null, null, startYear, endYear, 0, Integer.MAX_VALUE);
			BibTexUtils.removeDuplicates(allPosts);
			
			// Get all the years for the user posts.
			for(Post< BibTex> aPost : allPosts){
				if(!yearList.contains(aPost.getResource().getYear())){
					yearList.add(aPost.getResource().getYear());
				}
			}
			
			
			// Convert years from string into integer.
			int[] intYearArray = new int[yearList.size()];
			
			
			for(int i = 0; i < yearList.size(); i++){
				intYearArray[i] = Integer.parseInt(yearList.get(i));
			}
			
			// Sort the years in descending order.
			Arrays.sort(intYearArray);
			
			// Convert sorted array back into strings.
			for (int j = 0, i = yearList.size() - 1; i >= 0 ; i--, j++){
				yearList.set(j, String.valueOf(intYearArray[i]));
			}
			
			// Get the posts for each year and append it under year headings.
			for (String yearString : yearList){
				String updatedTags = tags + " sys:year:" + yearString;
				/*
				 * FIXME: don't query the database again you already have the posts, why do not insert allPosts into a SortedMap?
				 * Than we can loop through the map and group then into html
				 */
				List<Post<BibTex>> posts = this.logic.getPosts(BibTex.class, this.getGroupingEntity(), requestedName, Arrays.asList(updatedTags.split(" ")), null, null, null, null, startYear, endYear, 0, Integer.MAX_VALUE);
				BibTexUtils.removeDuplicates(posts);
				
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
					renderedHTML.append("<h3 class=\"mw-headline level3\" level3>" + yearString + "</h3>");
					renderedHTML.append("<div id='publications'>" + this.layoutRenderer.renderLayout(layout, posts, true) + "</div>"); // class='entry bibtex'
				} catch (final LayoutRenderingException e) {
					log.error(e.getMessage());
				} catch (final IOException e) {
					log.error(e.getMessage());
				}
			}
			
			return renderedHTML.toString();
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
		if (tagAttributes.get(LIMIT) != null) {
			try {
				posts = posts.subList(0, Integer.parseInt(tagAttributes.get(LIMIT)));
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
