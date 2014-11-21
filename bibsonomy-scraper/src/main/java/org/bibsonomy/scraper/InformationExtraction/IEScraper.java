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

package org.bibsonomy.scraper.InformationExtraction;


import static org.bibsonomy.util.ValidationUtils.present;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.ie.BibExtraction;
import org.bibsonomy.util.StringUtils;


/**
 * Extracts data from selected text using the information extraction tool MALLET.
 * 
 * @author rja
 *
 */
public class IEScraper implements Scraper {

	private static final Pattern yearPattern = Pattern.compile("\\d{4}");


	/**
	 * Extract a valid Bibtex entry from a given publication snippet by using information extraction.
	 */
	@Override
	public boolean scrape(final ScrapingContext sc) throws ScrapingException {
		//FIXME: ScrapingContext.getSelectedText returns the selected text within the browser in ISO and not UTF-8 format
		//we need to convert this, because the mallet function removes erroneous signs, that get created
		//when formatting a UTF-8 String in ISO format.
		//A proper fix would be to make the getSelectedText function return UTF-8 only.
		final String selectedText = StringUtils.convertString(sc.getSelectedText(), "ISO-8859-1", StringUtils.CHARSET_UTF_8);
		
		/*
		 * don't scrape, if there is nothing selected
		 */
		if ((selectedText == null) || selectedText.trim().equals("")) {
			return false;
		}

		try {
			final HashMap<String, String> map = new BibExtraction().extraction(selectedText);

			if (map != null) {

				/*
				 * build Bibtex String from map
				 */
				final StringBuffer bibtex = this.getBibtex(map);
				/*
				 * add url to bibtex entry
				 */
				if (sc.getUrl() != null) {
					BibTexUtils.addField(bibtex, "url", sc.getUrl().toString());
				}
				/*
				 * set result
				 */
				sc.setBibtexResult(bibtex.toString());

				/*
				 * save the text the user selected (and the scraper used) into map 
				 */
				map.put("ie_selectedText", selectedText);

				/*
				 * save map data as XML in scraping context 
				 */
				final ByteArrayOutputStream bout = new ByteArrayOutputStream();
				final XMLEncoder encoder = new XMLEncoder(bout);
				encoder.writeObject(map);
				encoder.close();
				sc.setMetaResult(bout.toString("UTF-8"));

				/*
				 * returns itself to know, which scraper scraped this
				 */
				sc.setScraper(this);

				return true;
			}

		} catch (final IOException e) {
			throw new ScrapingException(e);
		} catch (final ClassNotFoundException e) {
			throw new ScrapingException(e);
		} catch (final NamingException e) {
			throw new ScrapingException(e);
		}
		return false;
	}

	/** Builds a bibtex string from a given hashmap
	 * @param map
	 * @return
	 */
	private StringBuffer getBibtex(final HashMap<String, String> map) {
		/*
		 * extract year (needed already here for bibtex key)
		 */
		map.put("year", this.getYearFromDate(map.get("date")));
		/*
		 * generate bibtex key
		 */
		final String bibtexKey = BibTexUtils.generateBibtexKey(map.get("author"), map.get("editor"), map.get("year"), map.get("title"));
		/*
		 * start with a stringbuffer which contains start of bibtex entry
		 */
		final StringBuffer bib = new StringBuffer("@misc{" + bibtexKey + ",\n");
		/*
		 * iterate over fields of hashmap
		 */
		for (final String key:map.keySet()) {
			/*
			 * extract value
			 */
			String value = map.get(key);
			if (value != null) {
				/*
				 *  replace curly brackets
				 */
				value = value.replace('{','(').replace('}',')');
				/*
				 * clean person lists
				 */
				if ("author".equals(key) || "editor".equals(key)) {
					value = this.cleanPerson(value);
				}
				bib.append(key + " = {" + value + "},\n");
			}
		}

		/*
		 * replace last "," with a closing curly bracket "}"
		 */
		final int pos = bib.lastIndexOf(",");
		bib.replace(pos, pos + 1, "\n}");

		return bib;
	}

	/**
	 * Extracts the year from the date string.
	 * 
	 * @param date
	 * @return
	 */
	private String getYearFromDate(final String date) {
		if (date != null) {
			/*
			 * look for YYYY, extract and append it
			 */
			final Matcher m = yearPattern.matcher(date);
			if (m.find()) {
				return m.group();
			}
		}
		return null;
	}

	/** Returns a self description of this scraper.
	 * 
	 */
	@Override
	public String getInfo() {
		return "IEScraper: Extraction of bibliographic references by information extraction. Author: Thomas Steuber";
	}

	@Override
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper>singletonList(this);
	}

	/** Cleans a String containing person names.
	 * @param person
	 * @return
	 */
	private String cleanPerson(final String person) {
		// not modify references with " and " 
		if (person.contains(" and ")) {
			return person;
		}
		// in references with ";" and no " and " replace ";" with " and "
		if (person.contains(";")) {
			return person.replace(";", " and ");
		}
		// in references with "," and no " and " or ";" replace "," with " and "
		if (person.contains(",")) {
			return person.replace(",", " and ");
		}

		return person;
	}

	@Override
	public boolean supportsScrapingContext(final ScrapingContext sc) {
		if (present(sc.getSelectedText())) {
			return true; // supports every snippet
		}
		return false;
	}

	
	/**
	 * @return site name
	 */
	public String getSupportedSiteName(){
		return null;
	}
	
	/**
	 * @return site url
	 */
	public String getSupportedSiteURL(){
		return null;
	}

}
