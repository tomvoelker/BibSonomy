package org.bibsonomy.scraper.converter;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.id.ISBNUtils;

/**
 * This converter tries to find and extract DublinCore metadata out of a
 * html forrmatted string.
 * 
 * @author Lukas
 * @version $Id$
 */
public class DublinCoreToBibtexConverter {

	
	private static final String BIBTEX_END_LINE = ",\n";
	
	private static final String TITLE_KEY = "title";
	private static final String AUTHOR_KEY = "author";
	private static final String ID_KEY = "id";
	private static final String TYPE_KEY = "type";

	// pattern to extract all DC key-value pairs, placed in the page's html
	private static final Pattern EXTRACTION_PATTERN = Pattern.compile("(?im)<\\s*meta(?=[^>]*lang=\"([^>\"]*)\")?(?=[^>]*content=\"([^>\"]*)\")[^>]*name=\"(?-i)DC(?i).([^>\"]*)\"[^>]*>");

	// pattern to extract a year out of a string
	private static final Pattern EXTRACT_YEAR = Pattern.compile("\\d\\d\\d\\d");
	
	/**
	 * Searches for HTML Dublin Core metadata in an html formatted string, extracts
	 * the data and converts it to a BibTeX formatted string.
	 * 
	 * @param dublinCore a string, with HTML formatted Dublin Core metadata
	 * 
	 * @return a BibTeX formatted string with the extracted information
	 * 
	 */
	public static String getBibTeX(final String dublinCore) {
		// get all DC values
		final Map<String, String> data = extractData(dublinCore);

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
			} else {
				// add bibtex key values pair to the bibtex string
				bibtex.append(getBibTeXEntry(key, dataEntry.getValue()));
				if (dataEntryInterator.hasNext()) {
					bibtex.append(BIBTEX_END_LINE);
				}
			}
		}

		// close brackets
		bibtex.append("\n}");

		return bibtex.toString();
	}

	/**
	 * parses the html code and returns a HashMap which maps the bibtex keys to
	 * their values in the code
	 * 
	 * @param pageContent the html code of the site
	 * 
	 * @return a map which maps bibtex key to their contained DC values
	 */
	private static Map<String, String> extractData(final String pageContent) {
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
				addOrAppendField(TYPE_KEY, value, data);
			} else if (StringUtils.containsIgnoreCase(key, TITLE_KEY) && present(lang) && lang.equalsIgnoreCase("eng")) {
				addOrAppendField(TITLE_KEY, value, data);
			} else if (StringUtils.containsIgnoreCase(key, "creator")) {
				addOrAppendField(AUTHOR_KEY, value, data);
			} else if (StringUtils.containsIgnoreCase(key, "identifier")) {
				addOrAppendField(ID_KEY, value, data);
			} else if (lang != null && lang.equalsIgnoreCase("eng") && StringUtils.containsIgnoreCase(key, "description")) {
				addOrAppendField("abstract", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "date")) {
				data.put("year", extractYear(value));
			} else if (StringUtils.containsIgnoreCase(key, "Contributor.CorporateName")) {
				data.put("school", value);
				data.put("institution", value);
			} else if (StringUtils.containsIgnoreCase(key, "contributor")) {
				addOrAppendField("editor", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "publisher")) {
				addOrAppendField("publisher", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "journal")) {
				addOrAppendField("journal", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "conference")) {
				addOrAppendField("conference", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "organization")) {
				addOrAppendField("organization", value, data);
			}
		}

		return data;
	}

	/**
	 * if the DC metatags contain more than one corresponding value, the
	 * additional info is appended, separated by commata
	 * 
	 * @param key the key to set for the map
	 * @param value the value to set for the key in the map
	 * @param data the map itself
	 */
	private static void addOrAppendField(final String key, final String value, final Map<String, String> data) {
		if (present(value)) {
			// append
			if (data.containsKey(key)) {
				data.put(key, data.get(key) + ", " + value);
			} else {
				// insert new entry
				data.put(key, value);
			}
		}
	}

	/**
	 * extracts a year, which must be given by four decimals out of a string
	 * 
	 * @param date the string which contains the year to extract
	 * 
	 * @return the year as a string
	 */
	private static String extractYear(final String date) {
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
			} else {
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
			//conference was set in DC data or type contains conference -> should be proceedings
			if (present(data.get("conference")) || StringUtils.containsIgnoreCase(data.get(TYPE_KEY), "conference") || StringUtils.containsIgnoreCase(data.get(TYPE_KEY), "poceedings")) {
				return BibTexUtils.PROCEEDINGS;
			} 
		}
		/*
		 * as default return misc
		 */
		return BibTexUtils.MISC;
	}
	
}
