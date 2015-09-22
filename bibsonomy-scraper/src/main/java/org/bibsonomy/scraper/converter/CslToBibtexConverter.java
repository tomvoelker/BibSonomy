/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
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
/**
 * 
 */
package org.bibsonomy.scraper.converter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.common.Pair;
import org.bibsonomy.util.ValidationUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * @author Mohammed Abed
 */
public class CslToBibtexConverter {

	private final Map<String, Pair<String, String>> entryTypeMap = new HashMap<String, Pair<String, String>>();
	// these fields are handled by mapField()
	private final Map<String, String> fieldMap = new HashMap<String, String>();
	// these fields need to be handled specially
	private final Set<String> specialFields = new HashSet<String>();
	
	public CslToBibtexConverter() {

		this.entryTypeMap.put("book", new Pair<String, String>("book", "booktitle"));
		this.entryTypeMap.put("statute", new Pair<String, String>("book", "booktitle"));

		this.entryTypeMap.put("journal", new Pair<String, String>("article", "journal"));
		this.entryTypeMap.put("case", new Pair<String, String>("article", "journal"));
		this.entryTypeMap.put("computer_program", new Pair<String, String>("article", "journal"));
		this.entryTypeMap.put("generic", new Pair<String, String>("article", "journal"));
		this.entryTypeMap.put("journal_article", new Pair<String, String>("article", "journal"));
		this.entryTypeMap.put("magazine_article", new Pair<String, String>("article", "journal"));
		this.entryTypeMap.put("newspaper_article", new Pair<String, String>("article", "journal"));
		this.entryTypeMap.put("working_paper", new Pair<String, String>("article", "journal"));

		this.entryTypeMap.put("book_section", new Pair<String, String>("inbook", "booktitle"));
		this.entryTypeMap.put("thesis", new Pair<String, String>("phdthesis", "booktitle"));
		this.entryTypeMap.put("conference_proceedings", new Pair<String, String>("inproceedings", "booktitle"));
		this.entryTypeMap.put("report", new Pair<String, String>("techreport", "booktitle"));
	
		this.fieldMap.put("title", "title");
		this.fieldMap.put("volume", "volume");
		this.fieldMap.put("issue", "number");
		this.fieldMap.put("publisher", "publisher");
		this.fieldMap.put("pages", "pages");
	
		this.specialFields.add("authors");
		this.specialFields.add("editors");
		this.specialFields.add("published_in");
		this.specialFields.add("type");
		this.specialFields.add("year");
		this.specialFields.add("identifiers");
		this.specialFields.add("url");
		this.specialFields.add("website");
		
	}

	/**
	 * Maps CSL type to BibTeX type.
	 * 
	 * @param type
	 * @return
	 */
	private Pair<String, String> getEntryType(final String type) {
		if (entryTypeMap.containsKey(type)) {
			return entryTypeMap.get(type);
		}
		return new Pair<String,String>("misc", "booktitle");
	}


	/** Function is to convert csl format to bibtex
	 * 
	 * @param cslCitation
	 * @return The resulting BibTeX string.
	 */
	public String cslToBibtex(final String cslCitation)  throws JSONException {

		final String jsonRead = cslCitation.replaceAll("\\/","/");

		final JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonRead);  


		final Pair<String, String> entryTypeTitle = getEntryType(json.getString("type"));
		final String entryType = entryTypeTitle.getFirst(); 
		final String lblTitle = entryTypeTitle.getSecond();


		final StringBuilder authors = getPersons(json, "authors");
		final StringBuilder editors = getPersons(json, "editors");
		final int year = json.has("year") ? json.getInt("year") : 0;

		final String citationKey = getCitationKey(authors, editors, year);


		final StringBuilder result = new StringBuilder("@");
		result.append(entryType).append("{").append(citationKey).append(",\n");

		// handle standard fields
		mapFields(json, result);
		
		// append special fields
		if (json.has("identifiers")) {
			mapFields(json.getJSONObject("identifiers"), result);
		}
		if (json.has("published_in")) {
			result.append(getBibTeX(lblTitle, json.getString("published_in")));
		}
		if (ValidationUtils.present(authors)) {
			result.append(getBibTeX("author", authors));
		}
		if (ValidationUtils.present(editors)) {
			result.append(getBibTeX("editor", editors));
		}
		if (json.has("website")) {
			result.append(getBibTeX("url", json.getString("website")));
		} else if (json.has("url")) {
			result.append(getBibTeX("url", json.getString("url")));
		}
		if (year != 0) {
			result.append(getBibTeX("year", Integer.toString(year)));
		}	    
		result.append("}");
		return result.toString();		
	}
	
	private String getBibTeX(final String key, final CharSequence value) {
		return("  " + key + " = {" + value + "},\n");
	}
	

	private void mapFields(final JSONObject json, final StringBuilder result) {
		@SuppressWarnings("rawtypes")
		final Iterator keys = json.keys();
		while (keys.hasNext()) {
			final String key = (String)keys.next();
			final String value = json.getString(key);
			
			final String field = mapField(key, value);
			if (ValidationUtils.present(field)) {
				result.append(field);
			}
		}
	}
	
	private String mapField(final String key, final String value) {
		// ignore special fields
		if (!this.specialFields.contains(key)) {
			final String bibtexKey;
			if (this.fieldMap.containsKey(key)) {
				bibtexKey = this.fieldMap.get(key);
			} else {
				bibtexKey = key;
			}
			return getBibTeX(bibtexKey, value);
		}
		return null;
	}
	
	
	private String getFirstSurname(final StringBuilder s) {
		final int indexOfComma = s.indexOf(",");
		if (indexOfComma > 0) {
			return s.substring(0, indexOfComma);
		}
		return "";
	}
	
	private String getCitationKey(final StringBuilder authors, final StringBuilder editors, final int year) {
		final String name;
		if (authors.length() > 0) {
			name = getFirstSurname(authors);
		} else if (editors.length() > 0) {
			name = getFirstSurname(editors);
		} else {
			name = "";
		}
		return name + year;
	}
	
	private String getString(final JSONObject input, final String key, final String defaultValue) {
		if (input.has(key)) {
			return input.getString(key);
		}
		return defaultValue;
	}
	
	private StringBuilder getPersons(final JSONObject json, final String key) {
		final StringBuilder fullName = new StringBuilder();
		if (json.has(key)) {
			final JSONArray persons = json.getJSONArray(key);
			if (ValidationUtils.present(persons)) {
				for (final Object author : persons) {
					if (fullName.length() > 0) {
						fullName.append(" and ");
					} 
					final JSONObject person = (JSONObject) author;
					fullName.append(getString(person, "surname", "")).append(", ").append(getString(person, "forename", ""));
				}
			}
		}
		return fullName;
	}
}
