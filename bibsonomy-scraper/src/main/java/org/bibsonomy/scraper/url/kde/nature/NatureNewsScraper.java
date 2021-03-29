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
package org.bibsonomy.scraper.url.kde.nature;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for Nature
 *
 * @author Johannes
 */
public class NatureNewsScraper extends AbstractUrlScraper{
	private static final Log log = LogFactory.getLog(NatureNewsScraper.class);

	private static final String SITE_URL = "http://www.nature.com/";
	private static final String SITE_NAME = "Nature";

	private static final String HOST = "nature.com";
	private static final String INFO = "Scraper for publications from " + href(SITE_URL, SITE_NAME)+".";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), Pattern.compile("/news/.*")));
	
	private static final Pattern author = Pattern.compile("<meta name=\"citation_authors\" content=\"(.*?)\"/>");
	private static final Pattern journal = Pattern.compile("<meta name=\"citation_journal_title\" content=\"(.*?)\"/>");
	private static final Pattern doi = Pattern.compile("<meta name=\"citation_doi\" content=\"doi:(.*?)\"/>");
	private static final Pattern title = Pattern.compile("<meta name=\"citation_title\" content=\"(.*?)\"/>");
	private static final Pattern pages = Pattern.compile("<meta name=\"citation_firstpage\" content=\"(.*?)\"/>");
	private static final Pattern date = Pattern.compile("<meta name=\"citation_date\" content=\"(.*?)\"/>");
	private static final Pattern volume = Pattern.compile("<meta name=\"citation_volume\" content=\"(.*?)\"/>");
	private static final Pattern number = Pattern.compile("<meta name=\"citation_issue\" content=\"(.*?)\"/>");

	
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.UrlScraper#getSupportedSiteName()
	 */
	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.UrlScraper#getSupportedSiteURL()
	 */
	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#getInfo()
	 */
	@Override
	public String getInfo() {
		return INFO;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#getUrlPatterns()
	 */
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#scrapeInternal(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		try {
			sc.setBibtexResult(constructBibtexFromHtmlMeta(sc));
			sc.setScraper(this);
		} catch (IOException e) {
			throw new ScrapingException(e);
		}
		return true;
	}

	private static String constructBibtexFromHtmlMeta(final ScrapingContext sc) throws IOException {
		final URL url = sc.getUrl();
		final String content = WebUtils.getContentAsString(url);
		final StringBuilder bibtex = new StringBuilder();
		bibtex.append("@article{nokey,\n");
		bibtex.append("url = {" + url + "},\n");

		// add author
		final Matcher m_author = author.matcher(content);
		if (m_author.find())
			bibtex.append("author = {" + m_author.group(1).trim().replaceAll("[;]*$", "").replace(";", " and ") + "},\n"); 

		// add journal
		final Matcher m_journal = journal.matcher(content);
		if (m_journal.find())
			bibtex.append("journal = {" + m_journal.group(1) + "},\n");

		// add doi
		final Matcher m_doi = doi.matcher(content);
		if (m_doi.find())
			bibtex.append("doi = {" + m_doi.group(1) + "},\n");

		// add title
		final Matcher m_title = title.matcher(content);
		if (m_title.find())
			bibtex.append("title = {" + m_title.group(1) + "},\n");

		// add pages
		final Matcher m_pages = pages.matcher(content);
		if (m_pages.find())
			bibtex.append("pages = {" + m_pages.group(1) + "},\n");

		// add date
		final Matcher m_date = date.matcher(content);
		if (m_date.find()) {
			try {
				final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				final Date parse = sdf.parse(m_date.group(1));

				bibtex.append("year = " + new SimpleDateFormat("yyyy").format(parse) + ",\n");
				bibtex.append("month = " + new SimpleDateFormat("MMM").format(parse).toLowerCase() + ",\n");

			} catch(ParseException pe) {
				try {
					throw new ScrapingException(pe);
				} catch (ScrapingException e) {
					log.error("Date parsing error", e);
				}
			}
		}

		// add volume
		final Matcher m_volume = volume.matcher(content);
		if (m_volume.find())
			bibtex.append("volume = {" + m_volume.group(1) + "},\n");

		// add number
		final Matcher m_number = number.matcher(content);
		if (m_number.find())
			bibtex.append("number = {" + m_number.group(1) + "}\n");

		bibtex.append("}");

		return bibtex.toString();
	}
}
