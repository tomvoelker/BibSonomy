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
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.UrlUtils;

/**
 * Scraper for pages with a span element which matches to the COinS specification.
 *  
 * @author tst
 */
public class CoinsScraper implements Scraper {

	private static final String AUTHOR_LAST_KEY = "rft.aulast";
	private static final String AUTHOR_FIRST_KEY = "rft.aufirst";
	private static final String DATE_KEY = "rft.date";
	private static final String END_PAGE_KEY = "rft.epage";
	private static final String START_PAGE_KEY = "rft.spage";
	private static final String PAGES_KEY = "rft.pages";
	private static final String ENTRY_TYPE_KEY = "rft_val_fmt";
	private static final String RFT_AU = "rft.au";
	
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
	public boolean scrape(final ScrapingContext sc) throws ScrapingException {
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
			final Map<String, String> tuples = new HashMap<>();

			final Matcher m = PATTERN_KEY_VALUE.matcher(titleValue);

			// search tuples and store it in map
			while (m.find()) {
				final String key = UrlUtils.safeURIDecode(m.group(1));
				String value = UrlUtils.safeURIDecode(m.group(2));

				// store only values which are not null and not empty
				if (present(key) && present(value)) {
					// rft.au is repeatable
					if (key.equals(RFT_AU) && tuples.containsKey(RFT_AU)) {
						value = tuples.get(RFT_AU) + PersonNameUtils.PERSON_NAME_DELIMITER + value;
					}
					tuples.put(key, value);
				}
			}

			/*
			 * first get values which are needed for books and journals
			 */

			// get author
			final StringBuilder authorBuf = new StringBuilder();
			if (tuples.containsKey(RFT_AU)) {
				authorBuf.append(tuples.get(RFT_AU));
			}
			if (tuples.containsKey(AUTHOR_FIRST_KEY) || tuples.containsKey(AUTHOR_LAST_KEY)) {
				final String au = getAuthorFirstLast(tuples.get(AUTHOR_FIRST_KEY), tuples.get(AUTHOR_LAST_KEY));
				if (authorBuf.length() == 0) {
					authorBuf.append(au);
				} else {
					authorBuf.insert(0, PersonNameUtils.PERSON_NAME_DELIMITER).insert(0, au);
				}
			}
			final String author = authorBuf.toString();

			// get title
			final String title;
			if (tuples.containsKey("rft.atitle")) {
				title = tuples.get("rft.atitle");
			} else {
				title = tuples.get("rft.title");
			}

			// get year
			String year = null;
			if (tuples.containsKey(DATE_KEY)){
				// get year from date
				final Matcher dateMatcher = PATTERN_DATE.matcher(tuples.get(DATE_KEY));
				if (dateMatcher.find()) {
					year = dateMatcher.group(1);
				}
			}

			// get pages
			String pages = null;
			if (tuples.containsKey(PAGES_KEY)) {
				pages = tuples.get(PAGES_KEY);
			} else if (tuples.containsKey(START_PAGE_KEY) && tuples.containsKey(END_PAGE_KEY)){
				// build pages with spage and epage
				final String spage = tuples.get(START_PAGE_KEY);
				final String epage = tuples.get(END_PAGE_KEY);
				pages = spage + "--" + epage;
			}

			/*
			 * check different entry types
			 */
			if (tuples.containsKey(ENTRY_TYPE_KEY)) {
				final String entryType = tuples.get(ENTRY_TYPE_KEY);
				if (entryType.contains(":journal")) {
					final String journal = get(tuples, "rft.title");
					/*
					 * build BibTeX
					 */
					bibtex.append("@article{").append(BibTexUtils.generateBibtexKey(author, null, year, title)).append(",\n");
					append("journal", journal, bibtex);
				} else if (entryType.contains(":book")) {
					final String btitle = get(tuples, "rft.btitle");
					/*
					 * build BibTeX
					 */
					bibtex.append("@book{").append(BibTexUtils.generateBibtexKey(author, null, year, btitle)).append(",\n");
					append("booktitle", btitle, bibtex);
				} else {
					bibtex.append("@misc{").append(BibTexUtils.generateBibtexKey(author, null, year, title)).append(",\n");
				}
				
				append("title", title, bibtex);
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
			}
			throw new ScrapingFailureException("span not contains a book or journal");
		}
		return false;
	}

	private static String getAuthorFirstLast(final String aufirst, final String aulast) {
		if (present(aufirst)) {
			if (present(aulast)) {
				return aulast + ", " + aufirst;
			}
			return aufirst;
		} else if (present(aulast)) {
			return aulast;
		}
		return "";
	}

	private static void append(final String fieldName, final String fieldValue, final StringBuffer bibtex) {
		if (present(fieldValue)) {
			bibtex.append(fieldName).append(" = {").append(fieldValue).append("},\n");
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
