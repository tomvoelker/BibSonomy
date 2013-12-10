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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.UrlUtils;

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

	private static final Pattern PATTERN_COINS = Pattern.compile("<span class=\"Z3988\" title=\"([^\"]*)\"");
	private static final Pattern PATTERN_KEY_VALUE = Pattern.compile("([^=]*)=([^&]*)(&amp;|&)?");
	private static final Pattern PATTERN_DATE = Pattern.compile("(\\d{4})");


	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public boolean scrape(final ScrapingContext sc)throws ScrapingException {
		if ((sc == null) || (sc.getUrl() == null)) {
			return false;
		}

		final String page = sc.getPageContent();
		final StringBuffer bibtex = new StringBuffer();

		final Matcher matcherCoins = PATTERN_COINS.matcher(page);

		if (matcherCoins.find()) {
			// span found, this scraper is responsible
			sc.setScraper(this);

			final String titleValue = matcherCoins.group(1);

			// store all key/value tuples
			final HashMap<String, String> tuples = new HashMap<String, String>();

			final Matcher m = PATTERN_KEY_VALUE.matcher(titleValue);

			// search tuples and store it in map
			while (m.find()) {
				final String key = UrlUtils.safeURIDecode(m.group(1));
				String value = UrlUtils.safeURIDecode(m.group(2));

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
				// some pages use both formats! :-(
				if (tuples.containsKey("rtf.au")) {
					author = author + " and " + tuples.get("rft.au");
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
				// get year from date
				final Matcher dateMatcher = PATTERN_DATE.matcher(tuples.get("rft.date"));
				if (dateMatcher.find()) {
					year = dateMatcher.group(1);
				}
			}

			// get pages
			String pages = null;
			if (tuples.containsKey("rft.pages")) {
				pages = tuples.get("rft.pages");
			} else if(tuples.containsKey("rft.spage") && tuples.containsKey("rft.epage")){
				// build pages with spage and epage
				final String spage = tuples.get("rft.spage");
				final String epage = tuples.get("rft.epage");
				pages = spage + "--" + epage;
			}

			/*
			 * check different entry types
			 */
			if (tuples.containsKey("rft_val_fmt")) {
				final String entryType = tuples.get("rft_val_fmt");
				if (entryType.contains(":journal")) {
					final String journal = get(tuples, "rft.title");
					/*
					 * build BibTeX
					 */
					bibtex.append("@article{").append(BibTexUtils.generateBibtexKey(author, null, year, atitle)).append(",\n");

					append("journal", journal, bibtex);
				} else if (entryType.contains(":book")) {
					final String btitle = get(tuples, "rft.btitle");
					/*
					 * build BibTeX
					 */
					bibtex.append("@book{").append(BibTexUtils.generateBibtexKey(author, null, year, btitle)).append(",\n");

					append("booktitle", btitle, bibtex);
				} else {
					bibtex.append("@misc{").append(BibTexUtils.generateBibtexKey(author, null, year, atitle)).append(",\n");
				}
				
				append("title", atitle, bibtex);
				append("author", author, bibtex);
				append("year", year, bibtex);
				append("volume", get(tuples, "rft.volume"), bibtex);
				append("number", get(tuples, "rft.issue"), bibtex);
				append("pages", pages, bibtex);
				append("publisher", get(tuples, "rft.pub"), bibtex);
				append("address", get(tuples, "rft.place"), bibtex);
				append("edition", get(tuples, "rft.edition"), bibtex);
				append("series", get(tuples, "rft.series"), bibtex);
				append("issn", get(tuples, "rft.issn"), bibtex);
				append("eissn", get(tuples, "rft.eissn"), bibtex);
				append("isbn", get(tuples, "rft.isbn"), bibtex);
				append("sici", get(tuples, "rft.sici"), bibtex);
				append("bici", get(tuples, "rft.bici"), bibtex);
				append("coden", get(tuples, "rft.coden"), bibtex);

				bibtex.append("\n}\n");
			}

			/*
			 * return BibTeX
			 */
			if (present(bibtex)) {
				// append url
				BibTexUtils.addFieldIfNotContained(bibtex, "url", sc.getUrl().toString());

				// add downloaded bibtex to result 
				sc.setBibtexResult(bibtex.toString());

				return true;
			} else {
				throw new ScrapingFailureException("span not contains a book or journal");
			}
		}
		return false;
	}

	private static void append(final String fieldName, final String fieldValue, final StringBuffer bibtex) {
		if (present(fieldValue)) {
			bibtex.append(fieldName + " = {").append(fieldValue).append("},\n");
		}
	}

	private static String get(final Map<String, String> tuples, final String key) {
		if (tuples.containsKey(key)) {
			return tuples.get(key);
		}
		return null;
	}

	@Override
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper>singleton(this);
	}

	@Override
	public boolean supportsScrapingContext(final ScrapingContext sc) {
		if (present(sc.getUrl())) {
			try {
				return PATTERN_COINS.matcher(sc.getPageContent()).find();
			} catch (final ScrapingException ex) {
				return false;
			}
		}
		return false;
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
