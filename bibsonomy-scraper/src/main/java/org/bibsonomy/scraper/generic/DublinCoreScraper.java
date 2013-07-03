/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.scraper.generic;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * Scraper to extract bibtex information from a site, which holds Dublin Core Metadata
 * in it's HTML
 * 
 * @author Lukas
 * @version $Id$
 */
public class DublinCoreScraper implements Scraper {

	//scraper informations
	private static final String SITE_NAME = "DublinCoreScraper";
	private static final String SITE_URL = "http://dublincore.org/";
	private static final String INFO = "The DublinCoreScraper resolves bibtex out of HTML Metatags, which are defined" + 
	" in the DublinCore Metaformat, given by the " + AbstractUrlScraper.href(SITE_URL, "Dublin Core Metadata Initiative") + 
	"\n Because all components of DC-Metadata are optional and their values not standardized, the scraper may not always be successful.";

	//pattern for checking support for a given site
	private static final Pattern DUBLIN_CORE_PATTERN = Pattern.compile("(?im)(?=<\\s*meta[^>]*name=\"DC.Title[^>]\"[^>]*>)(?=<\\s*meta[^>]*name=\"DC.Type[^>]\"[^>]*>)" +
	"(?=<\\s*meta[^>]*name=\"DC.Creator[^>]\"[^>]*>)");

	//pattern to extract all DC key-value pairs, placed in the page's html
	private static final Pattern EXTRACTION_PATTERN = Pattern.compile("(?im)<\\s*meta(?=[^>]*lang=\"([^>\"]*)\")?(?=[^>]*content=\"([^>\"]*)\")[^>]*name=\"(?-i)DC(?i).([^>\"]*)\"[^>]*>");

	//pattern to extract a year out of a string
	private static final Pattern EXTRACT_YEAR = Pattern.compile("\\d\\d\\d\\d");
	
	@Override
	public boolean scrape(ScrapingContext scrapingContext) throws ScrapingException {
		if (!present(scrapingContext.getUrl())) {
			return false;
		}
		// get the page content to find the dublin core data
		final String page = scrapingContext.getPageContent();
		if (present(page)) {
			// set scraper found
			scrapingContext.setScraper(this);
			// get bibtex information out of the DC metatags in the page
			scrapingContext.setBibtexResult(this.getBibTeX(page));
			return true;
		}
		return false;
	}

	/**
	 * extracts bibtex information out of a page which contains publication
	 * information in the Dublin Core format
	 * 
	 * @param pageContent the html page as a string
	 * @return the correct formatted bibtex
	 * 
	 * @throws ScrapingFailureException thrown idf not enough information was found
	 */
	private String getBibTeX(final String pageContent) throws ScrapingFailureException {
		String bibtex = "";

		// get all DC values
		HashMap<String, String> data = this.extractData(pageContent);

		// check if enough information is present
		if (data.get("type") == null || data.get("author") == null || data.get("title") == null) {
			throw new ScrapingFailureException("Not enough Dublin Core Metadata found!");
		}

		// generate the bibtex entrytype
		bibtex = this.setEntrytype(bibtex, data);

		// add all key value pairs to the well formatted bibtex string
		for (String key : data.keySet()) {
			// if corporate is set as a DC field, it must be interpreted
			// differently for
			// different entrytypes
			if (key.equals("school") && !bibtex.contains("@phdthesis") || key.equals("institution") && bibtex.contains("@phdthesis")) {
				continue;
			}
			// add bibtex key values pair to the bibtex string
			else {
				bibtex += this.getBibTeXEntry(key, data.get(key));
			}
		}

		// remove last comma
		final int index = bibtex.lastIndexOf(',');
		bibtex = bibtex.substring(0, index) + bibtex.substring(index + 1);

		// close brackets
		bibtex += "}";

		return insertBibTexKey(bibtex, data);

	}

	/**
	 * parses the html code and returns a HashMap which maps the bibtex keys to
	 * their values in the code
	 * 
	 * @param pageContent the html code of the site
	 * 
	 * @return a hashmap which maps bibtex key to thei contained DC values
	 */
	private HashMap<String, String> extractData(final String pageContent) {

		final Matcher matcher = EXTRACTION_PATTERN.matcher(pageContent);
		HashMap<String, String> data = new HashMap<String, String>();

		String key = "";
		String value = "";
		String lang = "";

		// search for DC patterns as long as possible
		while (matcher.find()) {
			key = matcher.group(3);
			value = matcher.group(2);
			lang = matcher.group(1);

			if (key.equalsIgnoreCase("Type")) {
				data = addOrAppendField("type", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "title") && present(lang) && lang.equalsIgnoreCase("eng")) {
				data = addOrAppendField("title", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "creator")) {
				data = addOrAppendField("author", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "identifier")) {
				data = addOrAppendField("id", value, data);
			} else if (lang != null && lang.equalsIgnoreCase("eng") && StringUtils.containsIgnoreCase(key, "description")) {
				data = addOrAppendField("abstract", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "date")) {
				data.put("year", this.extractYear(value));
			} else if (StringUtils.containsIgnoreCase(key, "Contributor.CorporateName")) {
				data.put("school", value);
				data.put("institution", value);
			} else if (StringUtils.containsIgnoreCase(key, "contributor")) {
				data = addOrAppendField("editor", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "publisher")) {
				data = addOrAppendField("publisher", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "journal")) {
				data = addOrAppendField("journal", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "conference")) {
				data = addOrAppendField("conference", value, data);
			} else if (StringUtils.containsIgnoreCase(key, "organization")) {
				data = addOrAppendField("organization", value, data);
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
	 * 
	 * @return the altered map
	 */
	private HashMap<String, String> addOrAppendField(final String key, final String value, final HashMap<String, String> data) {
		// append
		if (present(data.get(key))) {
			data.put(key, data.get(key) + ", " + value);
		}
		// insert new entry
		else {
			data.put(key, value);
		}
		return data;
	}

	/**
	 * generates and inserts the bibtexkey at the correct position in the bibtex
	 * string
	 * 
	 * @param bibtex string without bibtexkey in which to insert
	 * @param data the generated hashmap with bibtex key-value pairs
	 * 
	 * @return the bibtex string with bibtexkey
	 */
	private String insertBibTexKey(final String bibtex, final HashMap<String, String> data) {
		final String key = BibTexUtils.generateBibtexKey(data.get("author"), data.get("editor"), data.get("year"), data.get("title"));
		int index = bibtex.indexOf("{") + 1;

		return (bibtex.substring(0, index) + key + ",\n" + bibtex.substring(index));
	}

	/**
	 * extracts a year, which must be given by four decimals out of a string
	 * 
	 * @param date the string which contains the year to extract
	 * 
	 * @return the year as a string
	 */
	private String extractYear(final String date) {
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
	private String getBibTeXEntry(final String key, final String value) {
		return key + " = {" + value + "},\n";
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
	 * @return the new bibtexstring in the form \@type{
	 */
	private String setEntrytype(String bibtex, final HashMap<String, String> data) {
		//instance of DCMI type text
		if (StringUtils.containsIgnoreCase(data.get("type"), "text")) {
			//possible values for phdthesis
			if (data.get("type").equalsIgnoreCase("Text.Thesis.Doctoral") || data.get("type").equalsIgnoreCase("Text.phdthesis")) {
				bibtex = "@phdthesis{";
			} 
			//isbn or substring book in type -> should be a book
			else if (StringUtils.containsIgnoreCase(data.get("id"), "ISBN") || StringUtils.containsIgnoreCase(data.get("type"), "book")) {
				Pattern getISBN = Pattern.compile("\\d{3}-\\d-\\d{5}-\\d{3}-\\d");
				Matcher m = getISBN.matcher(data.get("id"));
				if (m.find()) {
					bibtex = "@book{";
					bibtex += getBibTeXEntry("ISBN", m.group());
				}
			} 
			//issn or articel in type -> should be an article
			else if (StringUtils.containsIgnoreCase(data.get("id"), "ISSN") || StringUtils.containsIgnoreCase(data.get("type"), "article")) {
				Pattern getISSN = Pattern.compile("\\d{4}-\\d{4}");
				Matcher m = getISSN.matcher(data.get("id"));
				if (m.find()) {
					bibtex = "@article{";
					bibtex += getBibTeXEntry("ISSN", m.group());
				}
			} 
			//conference was set in DC data or type contains conference -> should be proceedings
			else if (present(data.get("conference")) || StringUtils.containsIgnoreCase(data.get("type"), "conference") || StringUtils.containsIgnoreCase(data.get("type"), "proceedings")) {
				bibtex = "@proceedings{";
			} 
			//no hints for a specific textual type were found, so take misc
			else {
				// default entrytype for DCMIType text
				bibtex = "@misc{";
			}
		} 
		//type event, may a conference?
		else if (StringUtils.containsIgnoreCase(data.get("type"), "text")) {
			//conference was set in DC data or type contains conference -> should be proceedings
			if (present(data.get("conference")) || StringUtils.containsIgnoreCase(data.get("type"), "conference") || StringUtils.containsIgnoreCase(data.get("type"), "poceedings")) {
				bibtex = "@proceedings{";
			} 
			//no hints for a specific event were found, so take misc as default type for event
			else {
				// default entrytype for DCMIType event
				bibtex = "@misc{";
			}
		} 
		//type is something exotic, so misc is choosen as entrytype
		else {
			bibtex = "@misc{";
		}

		return bibtex;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#getScraper()
	 */
	@Override
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper> singleton(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#supportsScrapingContext(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean supportsScrapingContext(ScrapingContext scrapingContext) {
		if (!present(scrapingContext.getUrl())) {
			return false;
		}
		try {
			final String page = scrapingContext.getPageContent();
			// check whether page has got Dublin Core Metadata or not
			return DUBLIN_CORE_PATTERN.matcher(page).find();
		} catch (final ScrapingException ex) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#getInfo()
	 */
	@Override
	public String getInfo() {
		return INFO;
	}

	/**
	 * @return site name
	 */
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	/**
	 * @return site url
	 */
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

}
