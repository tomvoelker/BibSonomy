/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.id.ISBNUtils;

/**
 * abstract DC converter
 *
 * @author dzo
 */
public abstract class AbstractDublinCoreToBibTeXConverter implements BibtexConverter {
	private static final String PREFERRED_LANGUAGE = "en";
	
	private static final String BIBTEX_END_LINE = ",\n";
	
	// pattern to extract a year out of a string
	private static final Pattern EXTRACT_YEAR = Pattern.compile("\\d\\d\\d\\d");
	
	/** title key */
	protected static final String TITLE_KEY = "title";
	/** author key */
	protected static final String AUTHOR_KEY = "author";
	/** id key */
	protected static final String ID_KEY = "id";
	/** type key */
	protected static final String TYPE_KEY = "type";
	
	/**
	 * extracts a year, which must be given by four decimals out of a string
	 * 
	 * @param date the string which contains the year to extract
	 * 
	 * @return the year as a string
	 */
	protected static String extractYear(final String date) {
		Matcher m = EXTRACT_YEAR.matcher(date);
		if (m.find()) {
			return m.group();
		}
		return "";
	}
	
	/**
	 * generates a well formed bibtex-entry for the bibtex string
	 * 
	 * @param key the bibtex key to set
	 * @param value the value to the corresponding key to set
	 * 
	 * @return an well formed bibtex entry as a string
	 */
	private static String getBibTeXEntry(final String key, final String value) {
		return key + " = {" + value + "}";
	}
	
	/**
	 * if the DC metatags contain more than one corresponding value, the
	 * additional info is appended, separated by commata
	 * 
	 * @param key the key to set for the map
	 * @param value the value to set for the key in the map
	 * @param data the map itself
	 */
	protected static void addOrAppendField(final String key, final String value, final String language,  final Map<String, String> data) {
		// insert new entry and overwrite existing (they should be in a different language)
		if (present(language) && language.equalsIgnoreCase(PREFERRED_LANGUAGE) && present(value)) {
			data.put(key, value);
		}
		// language not set
		else if (present(value)) {
			// append
			if (data.containsKey(key)) {
				if(key.equals(AUTHOR_KEY)|| key.equals("editor")){
					data.put(key, data.get(key) + " and " + value);
				}else{
					data.put(key, data.get(key) + ", " + value);
				}
			} else {
				// insert new entry
				data.put(key, value);
			}
		}
	}
	
	/**
	 * because of the optional fields and the non-standardized values for the DC
	 * fields, this method tries to extract the bibtex entrytype out of the
	 * given DC data presented in the data param
	 * 
	 * TODO: Improve entrytype generation
	 * 
	 * @param bibtex the bibtex string to which the entrytype is appended
	 * @param data the data map, which contains bibtex information , generated
	 *            out of the DC HTML values
	 * 
	 * @return the entrytype
	 */
	private static String getEntrytype(final Map<String, String> data) {
		// instance of DCMI type text
		if (StringUtils.containsIgnoreCase(data.get(TYPE_KEY), "text")) {
			// possible values for phdthesis
			if (data.get(TYPE_KEY).equalsIgnoreCase("Text.Thesis.Doctoral") || data.get(TYPE_KEY).equalsIgnoreCase("Text.phdthesis")) {
				return BibTexUtils.PHD_THESIS;
			} 
			// type is a research article
			else if(data.get(TYPE_KEY).equalsIgnoreCase("Text.Serial.Journal")) {
				return BibTexUtils.ARTICLE;
			}
			// type could not be directly extracted -> try id
			else {
				final String idValue = data.get(ID_KEY);
				if (StringUtils.containsIgnoreCase(idValue, "ISBN") || StringUtils.containsIgnoreCase(data.get(TYPE_KEY), "book")) {
					return BibTexUtils.BOOK;
				}
				// issn or articel in type -> should be an article
				if (StringUtils.containsIgnoreCase(idValue, "ISSN") || StringUtils.containsIgnoreCase(data.get(TYPE_KEY), "article")) {
					return BibTexUtils.ARTICLE;
				}
				// conference was set in DC data or type contains conference -> should be proceedings
				if (present(data.get("conference")) || StringUtils.containsIgnoreCase(data.get(TYPE_KEY), "conference") || StringUtils.containsIgnoreCase(data.get(TYPE_KEY), "proceedings")) {
					return BibTexUtils.PROCEEDINGS;
				}
			}
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
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.converter.BibtexConverter#toBibtex(java.lang.String)
	 */
	@Override
	public final String toBibtex(final String dublinCore) {
		// get all DC values
		final Map<String, String> data = this.extractData(dublinCore);
		
		// check if enough information is present
		if (!present(data.get(TYPE_KEY)) || !present(data.get(AUTHOR_KEY)) || !present(data.get(TITLE_KEY))) {
			return "";
		}

		final String entrytype = getEntrytype(data);
		final StringBuilder bibtex = new StringBuilder("@");
		bibtex.append(entrytype);
		bibtex.append("{");
		final String bibtexKey = BibTexUtils.generateBibtexKey(data.get(AUTHOR_KEY), data.get("editor"), data.get("year"), data.get(TITLE_KEY));
		bibtex.append(bibtexKey).append(BIBTEX_END_LINE);

		/*
		 * extract isbn and issn
		 */
		if (BibTexUtils.ARTICLE.equals(entrytype)) {
			final String issn = ISBNUtils.extractISSN(data.get(ID_KEY));
			if (present(issn)) {
				bibtex.append(getBibTeXEntry("ISSN", issn)).append(BIBTEX_END_LINE);
			}
		}
		if (BibTexUtils.BOOK.equals(entrytype)) {
			final String isbn = ISBNUtils.extractISBN(data.get(ID_KEY));
			if (present(isbn)) {
				bibtex.append(getBibTeXEntry("ISBN", isbn)).append(BIBTEX_END_LINE);
			}
		}

		final boolean isPHDThesis = BibTexUtils.PHD_THESIS.equals(entrytype);
		final Iterator<Entry<String, String>> dataEntryInterator = data.entrySet().iterator();
		while (dataEntryInterator.hasNext()) {
			final Entry<String, String> dataEntry = dataEntryInterator.next();
			/*
			 * if corporate is set as a DC field, it must be interpreted
			 * differently for different entrytypes
			 */
			final String key = dataEntry.getKey();
			if (key.equals("school") && !isPHDThesis || key.equals("institution") && isPHDThesis) {
				continue;
			}
			// add bibtex key values pair to the bibtex string
			bibtex.append(getBibTeXEntry(key, dataEntry.getValue()));
			if (dataEntryInterator.hasNext()) {
				bibtex.append(BIBTEX_END_LINE);
			}
		}
		// close brackets
		bibtex.append("\n}");
		return bibtex.toString();
	}

	/**
	 * @param dublinCore
	 * @return the map to parse
	 */
	protected abstract Map<String, String> extractData(String dublinCore);

}
