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

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.bibsonomy.common.Pair;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.bibsonomy.model.util.BibTexUtils;

/**
 * converter to convert JSON CSL entries to BibTeX
 *
 * FIXME: does not work for all BibTeX types
 *
 * @author Mohammed Abed
 */
public class CslToBibtexConverter implements BibtexConverter {
	// FIXME: remove pair unused
	private final Map<String, Pair<String, String>> entryTypeMap = new HashMap<>();

	private final Map<String, String> fieldMap = new HashMap<>();

	private final Map<String, Map<String, String>> entryTypeSpecificMapping = new HashMap<>();
	
	public CslToBibtexConverter() {
		this.entryTypeMap.put("book", new Pair<>(BibTexUtils.BOOK, "booktitle"));
		this.entryTypeMap.put("statute", new Pair<>("book", "booktitle"));

		this.entryTypeMap.put("journal", new Pair<>("article", "journal"));
		this.entryTypeMap.put("case", new Pair<>("article", "journal"));
		this.entryTypeMap.put("computer_program", new Pair<>("article", "journal"));
		this.entryTypeMap.put("generic", new Pair<>("article", "journal"));
		this.entryTypeMap.put("journal_article", new Pair<>("article", "journal"));
		this.entryTypeMap.put("magazine_article", new Pair<>("article", "journal"));
		this.entryTypeMap.put("newspaper_article", new Pair<>("article", "journal"));
		this.entryTypeMap.put("working_paper", new Pair<>("article", "journal"));

		this.entryTypeMap.put("book_section", new Pair<>(BibTexUtils.INBOOK, "booktitle"));
		this.entryTypeMap.put("thesis", new Pair<>(BibTexUtils.PHD_THESIS, "booktitle"));
		this.entryTypeMap.put("article", new Pair<>(BibTexUtils.ARTICLE, ""));

		this.entryTypeMap.put("conference_proceedings", new Pair<>("inproceedings", "booktitle"));
		this.entryTypeMap.put("paper_conference", new Pair<>(BibTexUtils.INPROCEEDINGS, ""));
		this.entryTypeMap.put("report", new Pair<>("techreport", "booktitle"));

		// some general field mappings
		this.fieldMap.put("title", "title");
		this.fieldMap.put("abstract", "abstract");
		this.fieldMap.put("DOI", "doi");
		this.fieldMap.put("ISBN", "isbn");
		this.fieldMap.put("ISSN", "issn");
		this.fieldMap.put("pages", "pages");
		this.fieldMap.put("page", "pages");
		this.fieldMap.put("publisher-place", "address");
		this.fieldMap.put("event-place", "location");
		this.fieldMap.put("URL", "url");
		this.fieldMap.put("website", "url");
		this.fieldMap.put("keyword", "keywords");

		// TODO: I think these fields are entry type specific
		this.fieldMap.put("volume", "volume");
		this.fieldMap.put("issue", "number");
		this.fieldMap.put("publisher", "publisher");

		final Map<String, String> inproceedingsEntryType = new HashMap<>();
		inproceedingsEntryType.put("container-title", "booktitle");
		inproceedingsEntryType.put("collection-title", "series");
		this.entryTypeSpecificMapping.put(BibTexUtils.INPROCEEDINGS, inproceedingsEntryType);

		final Map<String, String> inbookEntryTypeMappings = new HashMap<>();
		inbookEntryTypeMappings.put("container-title", "booktitle");
		inbookEntryTypeMappings.put("published_in", "booktitle"); // TODO: is this standard? Added to get the test working
		this.entryTypeSpecificMapping.put(BibTexUtils.INBOOK, inbookEntryTypeMappings);

		final Map<String, String> articleEntryTypeMappings = new HashMap<>();
		articleEntryTypeMappings.put("container-title", "journal");
		articleEntryTypeMappings.put("number-of-pages", "numpages");
		articleEntryTypeMappings.put("source", "issue_date");
		this.entryTypeSpecificMapping.put(BibTexUtils.ARTICLE, articleEntryTypeMappings);

		// FIXME: add more entry type specific mappings
	}

	/**
	 * Maps CSL type to BibTeX type.
	 * 
	 * @param type
	 * @return
	 */
	private Pair<String, String> getEntryType(final String type) {
		final String lowerCaseType = type.toLowerCase();
		if (entryTypeMap.containsKey(lowerCaseType)) {
			return entryTypeMap.get(lowerCaseType);
		}

		return new Pair<>("misc", "booktitle");
	}

	/**
	 * converts csl entry to bibtex representation
	 * @param cslEntry
	 * @return
	 */
	public String toBibtex(final JSONObject cslEntry) {
		final Pair<String, String> entryTypeTitle = getEntryType(cslEntry.getString("type"));
		final String entryType = entryTypeTitle.getFirst();

		final String authors = getAuthors(cslEntry);
		final String editors = getPersons(cslEntry, "editors");
		final String year = extractYear(cslEntry);

		final String citationKey = getCitationKey(authors, editors, year);

		final StringBuilder result = new StringBuilder("@");
		result.append(entryType).append("{").append(citationKey).append(",\n");

		if (present(authors)) {
			result.append(getBibTeX("author", authors));
		}

		if (present(editors)) {
			result.append(getBibTeX("editor", editors));
		}

		if (present(year)) {
			result.append(getBibTeX("year", year));
		}

		// get all identifiers from the csl entry
		if (cslEntry.has("identifiers")) {
			final JSONObject identifiers = cslEntry.getJSONObject("identifiers");

			final Set<Object> keys = identifiers.keySet();
			for (final Object key : keys) {
				final String keyAsString = key.toString();
				final String value = identifiers.getString(keyAsString);
				result.append(getBibTeX(keyAsString, value));
			}
		}

		result.append(getFieldIfPresent(CslToBibtexConverter::extractMonth, cslEntry, "month"));
		result.append(getFieldIfPresent(CslToBibtexConverter::extractDay, cslEntry, "day"));

		// apply general mapping
		applyMapping(cslEntry, this.fieldMap, result);

		// apply entry type specific mapping
		if (this.entryTypeSpecificMapping.containsKey(entryType)) {
			final Map<String, String> fieldMapping = this.entryTypeSpecificMapping.get(entryType);
			applyMapping(cslEntry, fieldMapping, result);
		}

		result.append("}");
		return result.toString();
	}

	private String getFieldIfPresent(Function<JSONObject, String> valueExtractor, JSONObject cslEntry, String fieldName) {
		final String value = valueExtractor.apply(cslEntry);
		if (present(value)) {
			return getBibTeX(fieldName, value);
		}
		return "";
	}

	private void applyMapping(final JSONObject cslEntry, final Map<String, String> mapping, final StringBuilder result) {
		mapping.forEach((key, value1) -> {
			if (cslEntry.has(key)) {
				final String valueToCopy = cslEntry.getString(key);
				result.append(getBibTeX(value1, valueToCopy));
			}
		});
	}

	private static String extractDay(final JSONObject cslEntry) {
		final Optional<JSONArray> issued = extractIssued(cslEntry);
		if (issued.isPresent()) {
			final JSONArray jsonArray = issued.get();
			if (jsonArray.size() > 2) {
				return jsonArray.getString(2);
			}
		}
		return "";
	}

	private static String extractMonth(final JSONObject cslEntry) {
		final Optional<JSONArray> issued = extractIssued(cslEntry);
		if (issued.isPresent()) {
			final JSONArray jsonArray = issued.get();
			if (jsonArray.size() > 1) {
				return jsonArray.getString(1);
			}
		}
		return "";
	}

	private static String extractYear(final JSONObject cslEntry) {
		if (cslEntry.has("year")) {
			return cslEntry.getString("year");
		}
		final Optional<JSONArray> issued = extractIssued(cslEntry);
		return issued.map(x -> x.getString(0)).orElse("");
	}

	private static Optional<JSONArray> extractIssued(JSONObject cslEntry) {
		if (cslEntry.has("issued")) {
			final JSONObject issued = cslEntry.getJSONObject("issued");
			if (issued.has("date-parts")) {
				final JSONArray dateParts = issued.getJSONArray("date-parts");
				return Optional.of(dateParts.getJSONArray(0));
			}
		}
		return Optional.empty();
	}

	private String getAuthors(final JSONObject cslEntry) {
		if (cslEntry.has("author")) {
			return getPersons(cslEntry, "author");
		}
		return getPersons(cslEntry, "authors");
	}

	/** Function is to convert csl format to bibtex
	 * 
	 * @param cslCitation
	 * @return The resulting BibTeX string.
	 */
	@Override
	public String toBibtex(final String cslCitation) throws JSONException {
		final String jsonRead = cslCitation.replaceAll("\\/","/");
		final JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonRead);  

		return this.toBibtex(json);
	}
	
	private String getBibTeX(final String key, final CharSequence value) {
		return "  " + key + " = {" + value + "},\n";
	}

	private String getFirstSurname(final String s) {
		final int indexOfComma = s.indexOf(",");
		if (indexOfComma > 0) {
			return s.substring(0, indexOfComma);
		}
		return "";
	}
	
	private String getCitationKey(final String authors, final String editors, final String year) {
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
	
	private static String getPersons(final JSONObject json, final String key) {
		final StringBuilder fullName = new StringBuilder();
		if (json.has(key)) {
			final JSONArray persons = json.getJSONArray(key);
			if (present(persons)) {
				for (final Object author : persons) {
					if (fullName.length() > 0) {
						fullName.append(" and ");
					} 
					final JSONObject person = (JSONObject) author;

					fullName.append(parsePersonName(person));
				}
			}
		}
		return fullName.toString();
	}

	private static String parsePersonName(final JSONObject person) {
		final StringBuilder personName = new StringBuilder();
		final boolean hasFamilyName = person.has("family");
		final boolean hasSurName = person.has("surname");
		final boolean hasLastName = hasFamilyName || hasSurName;
		if (hasFamilyName) {
			personName.append(person.get("family"));
		} else if (hasSurName) {
			personName.append(person.get("surname"));
		}

		boolean hasGivenName = person.has("given");
		boolean hasForeName = person.has("forename");
		final boolean hasFirstName = hasGivenName || hasForeName;
		if (hasFirstName && hasLastName) {
			personName.append(", ");
		}

		if (hasGivenName) {
			personName.append(person.get("given"));
		} else if (hasForeName) {
			personName.append(person.get("forename"));
		}

		return personName.toString();
	}
}
