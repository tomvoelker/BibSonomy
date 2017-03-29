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
package org.bibsonomy.scraper.url.kde.librarything;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import org.apache.commons.lang.StringEscapeUtils;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;


/**
 * Scraper for www.librarything.com
 *
 * @author tst
 */
public class LibrarythingScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "librarything";
	private static final String URL_LIBRARYTHING_PAGE = "http://www.librarything.com";
	private static final String INFO = "Extracts publication from " + href("http://www.librarything.com/work-info", SITE_NAME) + 
	". If a http://www.librarything.com/work page is selected, then the scraper trys to download the according work-info page.";

	private static final Pattern BOOK_ID_PATTERN = Pattern.compile("^\\d+$");
	private static final Pattern LIBRARYTHING_PATTERN_OTHER_AUTHORS = Pattern.compile("<td class=\"bookeditfield\" id=\"bookedit_otherauthors\">([^<]*)</td>");

	private static final Pattern LIBRARYTHING_PATTERN_TITLE = Pattern.compile("<td class=\"bookeditfield\" id=\"bookedit_title\"><b>([^<]*)</b></td>");

	private static final Pattern LIBRARYTHING_PATTERN_WORK_TITLE = Pattern.compile("<span class=\"bookeditfield\" id=\"bookedit_title\"><b>([^<]*)</b></span>");

	private static final Pattern LIBRARYTHING_PATTERN_AUTHOR_LINK = Pattern.compile("<td class=\"bookeditfield\" id=\"bookedit_authorunflip\">(.*)</td>");

	private static final Pattern LIBRARYTHING_PATTERN_AUTHOR = Pattern.compile("<h2>by <a href=\"/author/[^>]*>([^<]*)</a></h2>");

	private static final Pattern LIBRARYTHING_PATTERN_DATE = Pattern.compile("<td class=\"bookeditfield\" id=\"bookedit_date\">([^<]*)</td>");

	private static final Pattern LIBRARYTHING_PATTERN_ISBN = Pattern.compile("<td class=\"bookeditfield\" id=\"bookedit_ISBN\">([^<]*)</td>");

	private static final Pattern LIBRARYTHING_PATTERN_WORK_AUTHOR_LINK = Pattern.compile("<td class=\"left\">Author</td><td class=\"bookNonEditField\">(.*)</td>");

	private static final Pattern LIBRARYTHING_PATTERN_WORK_ISBN_10 = Pattern.compile("<td class=\"left\">ISBN-10</td><td class=\"bookNonEditField\">([^<]*)</td>");

	private static final Pattern LIBRARYTHING_PATTERN_WORK_ISBN_13 = Pattern.compile("<td class=\"left\">ISBN-13</td><td class=\"bookNonEditField\">([^<]*)</td>");

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*librarything\\..*"), AbstractUrlScraper.EMPTY_PATTERN));

	private static final String PATTERN_LINK = "<a\\b[^>]*>([^<]*)</a>";

	/**
	 * This Scraper works only with the following URL-prefixes and no selected text.
	 * http://www.librarything.com/work-info/
	 * http://www.librarything.com/work/
	 */
	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		URL url;

		sc.setScraper(this);

		// build .com url			
		if (!sc.getUrl().getHost().contains("librarything.com")) {
			String urlString = sc.getUrl().toString();

			// extract part before tld
			int indexLibrarything = urlString.indexOf("librarything.");
			String bevorTLD = urlString.substring(0, indexLibrarything + 13);

			// extract part after tld
			urlString = urlString.substring(indexLibrarything+12);
			int indexFirstSlash = urlString.indexOf("/");
			String afterTLD = urlString.substring(indexFirstSlash);

			// build new .com url
			try {
				url = new URL(bevorTLD + "com" + afterTLD);
			} catch (MalformedURLException e) {
				throw new InternalFailureException(e);
			}

			// is already a .com url
		} else {
			url = sc.getUrl();
		}

		try {
			final String content = WebUtils.getContentAsString(url);

			String author = null;
			String title = null;
			String year = null;
			String misc = null;
			String key = SITE_NAME;

			// extract data
			Matcher authorMatcher = LIBRARYTHING_PATTERN_AUTHOR.matcher(content);
			if (authorMatcher.find()) {
				author = authorMatcher.group(1);
			} else {
				authorMatcher = LIBRARYTHING_PATTERN_WORK_AUTHOR_LINK.matcher(content);
				if (authorMatcher.find()) {
					author = authorMatcher.group();
					Pattern linkPattern = Pattern.compile(PATTERN_LINK);
					Matcher linkMatcher = linkPattern.matcher(author);
					if (linkMatcher.find()) {
						author = linkMatcher.group(1);
					}
				} else {
					authorMatcher = LIBRARYTHING_PATTERN_AUTHOR_LINK.matcher(content);
					if(authorMatcher.find()){
						author = authorMatcher.group();
						Pattern linkPattern = Pattern.compile(PATTERN_LINK);
						Matcher linkMatcher = linkPattern.matcher(author);
						if(linkMatcher.find())
							author = linkMatcher.group(1);
					}
				}
			}

			authorMatcher = LIBRARYTHING_PATTERN_OTHER_AUTHORS.matcher(content);
			if (authorMatcher.find()) {
				String otherAuthors = authorMatcher.group(1);
				if (author == null && !otherAuthors.equals("")) {
					author = otherAuthors;
				} else if (!otherAuthors.equals("")) {
					author = author + " and " + authorMatcher.group(1);
				}
			}
			Matcher titleMatcher = LIBRARYTHING_PATTERN_TITLE.matcher(content);
			if (titleMatcher.find()) {
				title = titleMatcher.group(1);
			} else {
				titleMatcher = LIBRARYTHING_PATTERN_WORK_TITLE.matcher(content);
				if(titleMatcher.find()){
					title = titleMatcher.group(1);
				}
			}

			Matcher yearMatcher = LIBRARYTHING_PATTERN_DATE.matcher(content);
			if (yearMatcher.find()) {
				year = yearMatcher.group(1);
				key = key + year;
			}

			Matcher isbnMatcher = LIBRARYTHING_PATTERN_ISBN.matcher(content);
			if (isbnMatcher.find()) {
				misc = "isbn={" + isbnMatcher.group(1) + "}";
			} else {
				isbnMatcher = LIBRARYTHING_PATTERN_WORK_ISBN_10.matcher(content);
				if(isbnMatcher.find()){
					if (misc == null) {
						misc = "isbn={" + isbnMatcher.group(1) + "}";
					} else {
						misc = misc + ", " + "isbn={" + isbnMatcher.group(1) + "}";
					}
				}

				isbnMatcher = LIBRARYTHING_PATTERN_WORK_ISBN_13.matcher(content);
				if(isbnMatcher.find()){
					if(misc == null)
						misc = "isbn={" + isbnMatcher.group(1) + "}";
					else
						misc = misc + ", " + "isbn={" + isbnMatcher.group(1) + "}";
				}
			}

			final StringBuilder resultBibtex = new StringBuilder();
			resultBibtex.append("@book{" + key + ",\n");

			if (author != null) {
				resultBibtex.append("\tauthor = {" + author + "},\n");
			}

			if (title != null) {
				resultBibtex.append("\ttitle = {" + title + "},\n");
			}

			if (year != null) {
				resultBibtex.append("\tyear = {" + year + "},\n");
			}

			if (misc != null) {
				resultBibtex.append("\t" + misc + ",\n");
			}

			if (url != null) {
				resultBibtex.append("\turl = {" + url + "},\n");
			}

			String bibResult = resultBibtex.toString();

			// need to unscaped the html entities since they use them on their page
			bibResult = StringEscapeUtils.unescapeHtml(bibResult);

			bibResult = bibResult.substring(0, bibResult.length()-2) + "\n}\n";

			sc.setBibtexResult(bibResult);
			return true;
		} catch (final IOException ex) {
			throw new InternalFailureException(ex);
		}
	}

	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	@Override
	public String getSupportedSiteName() {
		return "Librarything";
	}

	@Override
	public String getSupportedSiteURL() {
		return URL_LIBRARYTHING_PAGE;
	}

}
