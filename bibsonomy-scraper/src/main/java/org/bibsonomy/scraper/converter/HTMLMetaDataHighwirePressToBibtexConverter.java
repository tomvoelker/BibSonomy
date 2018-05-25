package org.bibsonomy.scraper.converter;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bibsonomy.model.util.BibTexUtils;
import org.springframework.web.util.HtmlUtils;

/**
 * This converter tries to find and extract Highwire Press metadata out of a
 * html formatted string.
 *
 * @author Johannes
 */
public class HTMLMetaDataHighwirePressToBibtexConverter extends AbstractDublinCoreToBibTeXConverter {

	private static final Pattern EXTRACTION_PATTERN = Pattern.compile("(?im)<\\s*meta(?=[^>]*name=\"([^\"]*)\")[^>]*content=\"([^\"]*)\"[^>]*>");
	private static final Pattern TYPE_EXTRACTION_PATTERN = Pattern.compile("(?im)<\\s*meta(?=[^>]*name=\"og:type\")[^>]*content=\"([^\"]*)\"[^>]*>");
	private static final Pattern TYPE_EXTRACTION_PATTERN2 = Pattern.compile("(?im)<\\s*meta(?=[^>]*property=\"og:type\")[^>]*content=\"([^\"]*)\"[^>]*>");


	@Override
	protected Map<String, String> extractData(final String pageContent) {	
		final Matcher matcher = EXTRACTION_PATTERN.matcher(pageContent);

		Map<String, Set<String>> data = new HashMap<String, Set<String>>();

		String key = "";
		String value = "";
		String firstPage = "";
		String lastPage = "";

		// search for DC patterns as long as possible
		while (matcher.find()) {
			key = matcher.group(1);
			value = matcher.group(2);

			if (StringUtils.equalsIgnoreCase(key, "citation_title")) {
				addValueToDataIfNotContained(TITLE_KEY, value, data);
			} else if (StringUtils.equalsIgnoreCase(key, "citation_author")) {
				addValueToDataIfNotContained(AUTHOR_KEY, value, data);
			} else if (StringUtils.containsIgnoreCase(key, "doi")){
				addValueToDataIfNotContained("doi", value, data);
			} else if (StringUtils.equalsIgnoreCase(key, "citation_id")) {
				addValueToDataIfNotContained(ID_KEY, value, data);
			} else if (StringUtils.equalsIgnoreCase(key, "citation_abstract")) {
				addValueToDataIfNotContained("abstract", HtmlUtils.htmlUnescape(value), data);
			} else if (StringUtils.containsIgnoreCase(key, "citation_date")) {
				addValueToDataIfNotContained("year", extractYear(value), data);
			} else if (StringUtils.containsIgnoreCase(key, "publisher")) {
				addValueToDataIfNotContained("publisher", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "journal_title")) {
				addValueToDataIfNotContained("journal", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "issn")){
				addValueToDataIfNotContained("issn", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "issue")){
				addValueToDataIfNotContained("issue", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "volume")){
				addValueToDataIfNotContained("volume", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "firstpage")) { 
				firstPage = value;
			} else if (StringUtils.containsIgnoreCase(key, "lastpage")) {
				lastPage = value;
			}
		}

		if (present(firstPage)) {
			String pages = firstPage + "-" + lastPage;
			addValueToDataIfNotContained("pages", pages, data);
		}

		/*
		 * there is no entry for the type in Highwire Press tags
		 * but often next to Highwire Press tags there is an open graph tag "og:type" that specifies the type
		 */
		Matcher typeMatcher = TYPE_EXTRACTION_PATTERN.matcher(pageContent);
		if (typeMatcher.find()) {
			addValueToDataIfNotContained(TYPE_KEY, typeMatcher.group(1), data);
		} else {
			typeMatcher = TYPE_EXTRACTION_PATTERN2.matcher(pageContent);
			if (typeMatcher.find()) {
				addValueToDataIfNotContained(TYPE_KEY, typeMatcher.group(1), data);
			} else {
				//default value if no type was found
				addValueToDataIfNotContained(TYPE_KEY, "misc", data);
			}
		}

		return convertMap(data);
	}

	private static void addValueToDataIfNotContained(final String key, final String value, final Map<String, Set<String>> data) {
		HTMLMetaDataDublinCoreToBibtexConverter.addValueToDataIfNotContained(key, value, null, data);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.converter.AbstractDublinCoreToBibTeXConverter#getEntrytype(java.util.Map)
	 */
	@Override
	protected String getEntrytype(Map<String, String> data) {
		final String idValue = data.get(ID_KEY);
		if (StringUtils.containsIgnoreCase(idValue, "ISBN") || present(data.get("isbn"))|| StringUtils.containsIgnoreCase(data.get(TYPE_KEY), "book")) {
			return BibTexUtils.BOOK;
		}
		
		// issn or article in type -> should be an article
		if (StringUtils.containsIgnoreCase(idValue, "ISSN") || present(data.get("issn")) || StringUtils.containsIgnoreCase(data.get(TYPE_KEY), "article")) {
			return BibTexUtils.ARTICLE;
		}
		
		// conference was set in DC data or type contains conference -> should be proceedings
		if (present(data.get("conference")) || StringUtils.containsIgnoreCase(data.get(TYPE_KEY), "conference") || StringUtils.containsIgnoreCase(data.get(TYPE_KEY), "proceedings")) {
			return BibTexUtils.PROCEEDINGS;
		}

		// type event, may a conference?
		if (StringUtils.containsIgnoreCase(data.get(TYPE_KEY), "event")) {
			// conference was set in DC data or type contains conference -> should be proceedings
			if (present(data.get("conference")) || StringUtils.containsIgnoreCase(data.get(TYPE_KEY), "conference") || StringUtils.containsIgnoreCase(data.get(TYPE_KEY), "poceedings")) {
				return BibTexUtils.PROCEEDINGS;
			} 
		}
		
		/*
		 * as default return misc
		 */
		return BibTexUtils.MISC;
	}

	/**
	 * converts all the values in each list to a single concatenated value
	 * @param data is a Map<String, List<String>>
	 * @return a Map<String, String>
	 */
	private static Map<String, String> convertMap(Map<String, Set<String>> data) {
		Map<String, String> r = new HashMap<String, String>();

		for (String k : data.keySet()){
			for (String v : data.get(k)){
				addOrAppendField(k, v, null, r);
			}
		}

		return r;
	}
}

