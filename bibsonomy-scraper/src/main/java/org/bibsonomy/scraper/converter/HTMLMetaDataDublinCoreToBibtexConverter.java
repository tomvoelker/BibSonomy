/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scraper.converter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * This converter tries to find and extract DublinCore metadata out of a
 * html formatted string.
 * 
 * @author Lukas
 */
public class HTMLMetaDataDublinCoreToBibtexConverter extends AbstractDublinCoreToBibTeXConverter {
	
	// pattern to extract all DC key-value pairs, placed in the page's html
	private static final Pattern EXTRACTION_PATTERN = Pattern.compile("(?im)<\\s*meta(?=[^>]*lang=\"([^\"]*)\")?(?=[^>]*content=\"([^\"]*)\")[^>]*name=\"[d|D][c|C].([^\"]*)\"[^>]*>");
	
	/**
	 * parses the html code and returns a HashMap which maps the bibtex keys to
	 * their values in the code
	 * 
	 * @param pageContent the html code of the site
	 * 
	 * @return a map which maps bibtex key to their contained DC values
	 */
	@Override
	protected Map<String, String> extractData(final String pageContent) {
		final Matcher matcher = EXTRACTION_PATTERN.matcher(pageContent);

		Map<String, Set<String>> data = new HashMap<String, Set<String>>();

		String key = "";
		String value = "";
		String lang = "";

		// search for DC patterns as long as possible
		while (matcher.find()) {
			key = matcher.group(3);
			value = matcher.group(2);
			lang = matcher.group(1);

			if (key.equalsIgnoreCase("Type")) {
				addValueToDataIfNotContained(TYPE_KEY, value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, TITLE_KEY)) {
				addValueToDataIfNotContained(TITLE_KEY, value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "creator")) {
				addValueToDataIfNotContained(AUTHOR_KEY, value, lang, data);
			} else if (StringUtils.equalsIgnoreCase(key, "identifier")) {
				addValueToDataIfNotContained(ID_KEY, value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "identifier.doi")){
				addValueToDataIfNotContained("doi", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "description")||StringUtils.containsIgnoreCase(key, "abstract")) {
				addValueToDataIfNotContained("abstract", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "date")) {
				addValueToDataIfNotContained("year", extractYear(value), lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "Contributor.CorporateName")) {
				addValueToDataIfNotContained("school", value, lang, data);
				addValueToDataIfNotContained("institution", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "contributor")) {
				addValueToDataIfNotContained("editor", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "publisher")) {
				addValueToDataIfNotContained("publisher", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "journal")) {
				addValueToDataIfNotContained("journal", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "conference")) {
				addValueToDataIfNotContained("conference", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "organization")) {
				addValueToDataIfNotContained("organization", value, lang, data);
			} else if (StringUtils.equalsIgnoreCase(key, "source")){
				addValueToDataIfNotContained("source", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "source.issn")){
				addValueToDataIfNotContained("issn", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "source.issue")){
				addValueToDataIfNotContained("issue", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "source.uri")){
				addValueToDataIfNotContained("uri", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "source.volume")){
				addValueToDataIfNotContained("volume", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "pageNumber")) {
				addValueToDataIfNotContained("pages", value, lang, data);
			}
		}
		
		return convertMap(data);
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

	// TODO: move to utils class
	protected static void addValueToDataIfNotContained(final String key, final String value, final String language, final Map<String, Set<String>> data) {
		Set<String> valueInData = data.get(key);

		if (valueInData == null) {
			Set<String> s = new HashSet<String>();
			s.add(value);
			data.put(key, s);
		} else if (!valueInData.contains(value)){
			valueInData.add(value.trim());
		}
	}
}
