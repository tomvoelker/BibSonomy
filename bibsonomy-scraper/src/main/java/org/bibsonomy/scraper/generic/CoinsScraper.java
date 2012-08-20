/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * Scraper for Pages with a span element which matches to the COinS specification.
 *  
 * @author tst
 * @version $Id$
 */
public class CoinsScraper implements Scraper {

	private static final String SITE_NAME = "CoinsScraper";
	private static final String SITE_URL = "http://ocoins.info/";
	private static final String INFO = "<a href=\"http://ocoins.info/\">COinS</a> Scraper: Scraper for Metadata in COinS format.";

	private static final Pattern PATTERN_COINS = Pattern.compile("<span class=\"Z3988\" title=\"([^\\\"]*)\"");
	private static final Pattern PATTERN_KEY_VALUE = Pattern.compile("([^=]*)=([^&]*)(&amp;)?");
	private static final Pattern PATTERN_DATE = Pattern.compile("(\\d{4})");


	public String getInfo() {
		return INFO;
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if (sc == null || sc.getUrl() == null) return false;
		
		final String page = sc.getPageContent();
		final StringBuffer bibtex = new StringBuffer();

		final Matcher matcherCoins = PATTERN_COINS.matcher(page);
		
		if (matcherCoins.find()) {
			// span found, this scraper is responsible
			sc.setScraper(this);

			String titleValue = matcherCoins.group(1);

			// store all key/value tuples
			final HashMap<String, String> tuples = new HashMap<String, String>();

			final Matcher m = PATTERN_KEY_VALUE.matcher(titleValue);

			// search tuples and store it in map
			while (m.find()) {
				final String key;
				String value;
				try {
					key = URLDecoder.decode(m.group(1), "UTF-8");
					value = URLDecoder.decode(m.group(2), "UTF-8");
				} catch (UnsupportedEncodingException ex) {
					throw new InternalFailureException(ex);
				}

				// store only values which are not null and not empty
				if (present(key) && present(value)) {
					// rft.au is repeatable
					if (key.equals("rft.au") && tuples.containsKey("rft.au")) {
						value = tuples.get("rft.au") + " and " + value;
					}
					tuples.put(key, value);
				}
			}

			/*
			 * first get values which are needed for books and journals
			 */

			// get author
			String author = null;
			if (tuples.containsKey("rft.au")) {
				author = tuples.get("rft.au");
			} else {
				final String aufirst = tuples.get("rft.aufirst");
				final String aulast = tuples.get("rft.aulast");
				if (present(aufirst)) {
					if (present(aulast)) {
						author = aulast + ", " + aufirst;
					} else {
						author = aufirst;
					}
				} else if (present(aulast)) {
					author = aulast;
				}
			}

			// get title
			final String atitle;
			if (tuples.containsKey("rft.atitle")) {
				atitle = tuples.get("rft.atitle");
			} else {
				atitle = tuples.get("rft.title");
			}

			// get year
			String year = null;
			if (tuples.containsKey("rft.date")){
				final String date = tuples.get("rft.date");
				// get year from date
				final Matcher dateMatcher = PATTERN_DATE.matcher(date);
				if (dateMatcher.find())
					year = dateMatcher.group(1);
			}

			// get pages
			String pages = null;
			if(tuples.containsKey("rft.pages"))
				pages = tuples.get("rft.pages");
			else if(tuples.containsKey("rft.spage") && tuples.containsKey("rft.epage")){
				// build pages with spage and epage
				String spage = tuples.get("rft.spage");
				String epage = tuples.get("rft.epage");
				pages = spage + "-" + epage;
			}

			final String issn = get(tuples, "rft.issn");
			final String isbn = get(tuples, "rft.isbn");

			

			/*
			 * book and journal specific behaviour
			 */

			// check for journal
			if (tuples.containsKey("rft_val_fmt") && tuples.get("rft_val_fmt").contains(":journal")){

				final String title = get(tuples, "rft.title"); // FIXME: rename variable to journal!?
				final String volume = get(tuples, "rft.volume");
				final String issue = get(tuples, "rft.issue");
				final String eissn = get(tuples, "rft.eissn");
				final String coden = get(tuples, "rft.coden");
				final String sici = get(tuples, "rft.sici");

				/*
				 * build BibTeX
				 */
				bibtex.append("@article{").append(BibTexUtils.generateBibtexKey(author, null, year, title)).append(",\n");

				append("title", atitle, bibtex);
				append("author", author, bibtex);
				append("journal", title, bibtex); // journal FIXME: rename "title" to "journal"?
				append("year", year, bibtex);
				append("volume", volume, bibtex);
				append("number", issue, bibtex);
				append("pages", pages, bibtex);
				append("issn", issn, bibtex);
				append("eissn", eissn, bibtex);
				append("coden", coden, bibtex);
				append("sici", sici, bibtex);
				append("isbn", isbn, bibtex);

				bibtex.append("\n}\n");

				// check for book
			} else if(tuples.containsKey("rft_val_fmt") && tuples.get("rft_val_fmt").contains(":book")){

				final String btitle = get(tuples, "rft.btitle");
				final String address = get(tuples, "rft.place");
				final String publisher = get(tuples, "rft.pub");
				final String edition = get(tuples, "rft.edition");
				final String series = get(tuples, "rft.series");
				final String bici = get(tuples, "bici");

				/*
				 * build BibTeX
				 */
				bibtex.append("@book{").append(BibTexUtils.generateBibtexKey(author, null, year, atitle)).append(",\n");

				append("title", atitle, bibtex);
				append("booktitle", btitle, bibtex);
				append("author", author, bibtex);
				append("isbn", isbn, bibtex);
				append("address", address, bibtex);
				append("publisher", publisher, bibtex);
				append("year", year, bibtex);
				append("edition", edition, bibtex);
				append("series", series, bibtex);
				append("pages", pages, bibtex);
				append("issn", issn, bibtex);
				append("bici", bici, bibtex);

				bibtex.append("\n}\n");
			}

			// return bibtex
			if (present(bibtex)) {
				// append url
				BibTexUtils.addFieldIfNotContained(bibtex, "url", sc.getUrl().toString());
				
				// add downloaded bibtex to result 
				sc.setBibtexResult(bibtex.toString());
				
				return true;
			} else
				throw new ScrapingFailureException("span not contains a book or journal");
		}
		return false;
	}

	private static void append(final String fieldName, final String fieldValue, final StringBuffer bibtex) {
		if (present(fieldValue))  bibtex.append(fieldName + " = {").append(fieldValue).append("},\n");
	}
	
	private static String get(final Map<String, String> tuples, final String key) {
		if (tuples.containsKey(key))
			return tuples.get(key);
		return null;
	}
	
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper>singleton(this);
	}

	public boolean supportsScrapingContext(ScrapingContext sc) {
		if(sc.getUrl() != null){
			Matcher matcherCoins;
			try {
				matcherCoins = PATTERN_COINS.matcher(sc.getPageContent());
				if(matcherCoins.find())
					return true;
			} catch (ScrapingException ex) {
				return false;
			}
		}
		return false;
	}

	public static ScrapingContext getTestContext(){
		ScrapingContext context = new ScrapingContext(null);
		try {
			context.setUrl(new URL("http://www.westmidlandbirdclub.com/bibliography/NBotWM.htm"));
		} catch (MalformedURLException ex) {
		}
		return context;
	}
	
	/**
	 * @return site name
	 */
	public String getSupportedSiteName(){
		return SITE_NAME;
	}
	
	
	/**
	 * @return site url
	 */
	public String getSupportedSiteURL(){
		return SITE_URL;
	}


}
