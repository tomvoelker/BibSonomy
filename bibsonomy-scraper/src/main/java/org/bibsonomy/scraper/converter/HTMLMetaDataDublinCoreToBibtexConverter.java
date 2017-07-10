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
import java.util.Map;
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

		Map<String, String> data = new HashMap<String, String>();

		String key = "";
		String value = "";
		String lang = "";

		// search for DC patterns as long as possible
		while (matcher.find()) {
			key = matcher.group(3);
			value = matcher.group(2);
			lang = matcher.group(1);

			if (key.equalsIgnoreCase("Type")) {
				addOrAppendFieldIfNewValue(TYPE_KEY, value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, TITLE_KEY)) {
				addOrAppendFieldIfNewValue(TITLE_KEY, value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "creator")) {
				addOrAppendFieldIfNewValue(AUTHOR_KEY, value, lang, data);
			} else if (StringUtils.equalsIgnoreCase(key, "identifier")) {
				addOrAppendFieldIfNewValue(ID_KEY, value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "identifier.doi")){
				addOrAppendFieldIfNewValue("doi", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "description")||StringUtils.containsIgnoreCase(key, "abstract")) {
				addOrAppendFieldIfNewValue("abstract", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "date")) {
				data.put("year", extractYear(value));
			} else if (StringUtils.containsIgnoreCase(key, "Contributor.CorporateName")) {
				data.put("school", value);
				data.put("institution", value);
			} else if (StringUtils.containsIgnoreCase(key, "contributor")) {
				addOrAppendFieldIfNewValue("editor", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "publisher")) {
				addOrAppendFieldIfNewValue("publisher", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "journal")) {
				addOrAppendFieldIfNewValue("journal", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "conference")) {
				addOrAppendFieldIfNewValue("conference", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "organization")) {
				addOrAppendFieldIfNewValue("organization", value, lang, data);
			} else if (StringUtils.equalsIgnoreCase(key, "source")){
				addOrAppendFieldIfNewValue("source", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "source.issn")){
				addOrAppendFieldIfNewValue("issn", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "source.issue")){
				addOrAppendFieldIfNewValue("issue", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "source.uri")){
				addOrAppendFieldIfNewValue("uri", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "source.volume")){
				addOrAppendFieldIfNewValue("volume", value, lang, data);
			} else if (StringUtils.containsIgnoreCase(key, "pageNumber")) {
				addOrAppendFieldIfNewValue("pages", value, lang, data);
			}
		}
		return data;
	}
	
	private static void addOrAppendFieldIfNewValue(final String key, final String value, final String language,  final Map<String, String> data) {
		String valueInData = data.get(key);
		
		if (valueInData == null) {
			addOrAppendField(key, value, language, data);
		} else {
			if (value.trim().equals(valueInData.trim())) {
				return;
			}
			
			String[] valueSplitted;
			
			if(key.equals(AUTHOR_KEY)|| key.equals("editor")){
				valueSplitted = valueInData.split("and");
			} else {
				valueSplitted = valueInData.split(",");
			}
			
			/*
			 * checking for every part of value in data if it equals the value
			 * if true there is no need to save it again
			 */
			boolean valueContained = false;
			for (String s : valueSplitted) {
				if (s.trim().equals(value.trim())) {
					valueContained = true;
					break;
				}
			}
			
			if (!valueContained) {
				addOrAppendField(key, value, language, data);
			}
		}
	}
}
