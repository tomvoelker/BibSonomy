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
package org.bibsonomy.scraper.url.kde.muse;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for muse.jhu.edu
 * @author tst
 */
public class ProjectmuseScraper extends AbstractUrlScraper implements ReferencesScraper {
	private static final Log log = LogFactory.getLog(ProjectmuseScraper.class);
	
	private static final String SITE_NAME = "Project MUSE";
	private static final String SITE_URL = "http://muse.jhu.edu/";
	private static final String INFO = "Scraper for citations from " + href(SITE_URL, SITE_NAME)+".";

	private static final String HOST = "muse.jhu.edu";

	private static final String PREFIX_DOWNLOAD_URL = "http://muse.jhu.edu/metadata/sgml/journals/";

	private static final Pattern PATTERN_JOURNAL_ID = Pattern.compile("/journals/(.*)");
	/*
	 * regex pattern (sgml)
	 */
	private static final Pattern PATTERN_URL = Pattern.compile("<url>(.*)</url>");
	private static final Pattern PATTERN_JOURNAL = Pattern.compile("<journal>(.*)</journal>");
	private static final Pattern PATTERN_ISSN = Pattern.compile("<issn>(.*)</issn>");
	private static final Pattern PATTERN_VOLUME = Pattern.compile("<volume>(.*)</volume>");
	private static final Pattern PATTERN_ISSUE = Pattern.compile("<issue>(.*)</issue>");
	private static final Pattern PATTERN_YEAR = Pattern.compile("<year>(.*)</year>");
	private static final Pattern PATTERN_FPAGES = Pattern.compile("<fpage>(.*)</fpage>");
	private static final Pattern PATTERN_LPAGES = Pattern.compile("<lpage>(.*)</lpage>");
	private static final Pattern PATTERN_TITLE = Pattern.compile("<doctitle>(.*)</doctitle>");
	private static final Pattern PATTERN_AUTHOR = Pattern.compile("<docauthor>(.*)</docauthor>");
	private static final Pattern PATTERN_SURNAME = Pattern.compile("<surname>(.*)</surname>");
	private static final Pattern PATTERN_FNAME = Pattern.compile("<fname>(.*)</fname>");
	private static final Pattern PATTERN_ABSTRACT = Pattern.compile("<abstract>\\s*<p>([^<]*)</p>\\s*</abstract>");
	private static final Pattern references_pattern = Pattern.compile("(?s)<h3 class=\"references\">(.*)</div>");

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));
	
	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);

		/*
		 * get article ID from URL
		 */
		final String journalID = getRegexResult(PATTERN_JOURNAL_ID, sc.getUrl().toString());

		try {
			final String sgml = WebUtils.getContentAsString(new URL(PREFIX_DOWNLOAD_URL + journalID));

			final StringBuilder bibKey = new StringBuilder();
			final StringBuilder authors = new StringBuilder();

			/*
			 * author may be occur more then one time ...
			 */
			final Matcher matcher = PATTERN_AUTHOR.matcher(sgml);
			while (matcher.find()) {
				final String author = matcher.group(1);
				final String surname = getRegexResult(PATTERN_SURNAME, author); 
				final String fname = getRegexResult(PATTERN_FNAME, author);

				// append authors
				if (authors.length() > 0) {
					authors.append(" and ");
				} else {
					// first author
					bibKey.append(surname.toLowerCase());
				}
				authors.append(surname).append(", ").append(fname);
			}

			// get year
			final String year = getRegexResult(PATTERN_YEAR, sgml);
			// add year to bibtex key
			if (ValidationUtils.present(year)) {
				bibKey.append(year);
			}
			
			
			/*
			 * build BibTeX
			 */
			final StringBuilder bibtex = new StringBuilder("@inproceedings{");

			// add BibTeX key
			if (ValidationUtils.present(bibKey))
				bibtex.append(bibKey).append(",\n");
			else
				bibtex.append("noKey,\n");
			
			appendValue(bibtex, "title", getRegexResult(PATTERN_TITLE, sgml));
			appendValue(bibtex, "url", getRegexResult(PATTERN_URL, sgml));
			appendValue(bibtex, "journal", getRegexResult(PATTERN_JOURNAL, sgml));
			appendValue(bibtex, "issn", getRegexResult(PATTERN_ISSN, sgml));
			appendValue(bibtex, "volume", getRegexResult(PATTERN_VOLUME, sgml));
			appendValue(bibtex, "number", getRegexResult(PATTERN_ISSUE, sgml));
			appendValue(bibtex, "author", authors);
			appendValue(bibtex, "year", year);
			appendValue(bibtex, "pages", getPages(sgml));
			appendValue(bibtex, "abstract", getRegexResult(PATTERN_ABSTRACT, sgml));

			// close entry
			bibtex.append("}\n");

			sc.setBibtexResult(bibtex.toString());
			return true;

		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}

	}

	private String getPages(final String sgml) {
		final String fpages = getRegexResult(PATTERN_FPAGES, sgml);
		final String lpages = getRegexResult(PATTERN_LPAGES, sgml);
		String pages = null;
		if (fpages != null && lpages == null)
			pages = fpages;
		else if (fpages == null && lpages != null)
			pages = lpages;
		else if (fpages != null && lpages != null)
			pages = fpages + "--" + lpages;
		return pages;
	}
	
	private void appendValue(final StringBuilder bibtex, final String key, final CharSequence value) {
		if (ValidationUtils.present(value)) {
			bibtex.append("  ").append(key).append(" = {").append(value).append("},\n");
		}
	}
	

	/**
	 * execute regex and return matching result
	 * @param regex Regular Expression
	 * @param content target of regex
	 * @return matching result, null if no matching
	 */
	private static String getRegexResult(final Pattern regex, final String content){
		final Matcher matcher = regex.matcher(content);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext scrapingContext)throws ScrapingException {
		try {
			final Matcher m = references_pattern.matcher(WebUtils.getContentAsString(scrapingContext.getUrl()));
			if (m.find()) {
				scrapingContext.setReferences(m.group(1));
				return true;
			}
		} catch (final Exception e) {
			log.error("error while scraping references " + scrapingContext.getUrl(), e);
		}
		return false;
	}
}
