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
package org.bibsonomy.scraper.url.kde.nature;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for publication from nature.com
 * @author tst
 */
public class NatureScraper extends AbstractUrlScraper implements ReferencesScraper {
	private static final Log log = LogFactory.getLog(NatureScraper.class);

	private static final String SITE_URL = "http://www.nature.com/";
	private static final String SITE_NAME = "Nature";

	private static final String HOST = "nature.com";
	private static final String INFO = "Scraper for publications from " + href(SITE_URL, SITE_NAME)+".";

	/** pattern for links */
	private static final Pattern linkPattern = Pattern.compile("<a\\b[^<]*</a>");

	/** pattern for href field */
	private static final Pattern hrefPattern = Pattern.compile("href=\"[^\"]*\"");

	/** name from download link */
	private static final String CITATION_DOWNLOAD_LINK_NAME = ">Export citation<";
	private static final String CITATION_DOWNLOAD_LINK_NAME2 = ">Citation<";

	private static final Pattern author = Pattern.compile("<meta name=\"citation_authors\" content=\"(.*?)\"/>");
	private static final Pattern journal = Pattern.compile("<meta name=\"citation_journal_title\" content=\"(.*?)\"/>");
	private static final Pattern doi = Pattern.compile("<meta name=\"citation_doi\" content=\"doi:(.*?)\"/>");
	private static final Pattern title = Pattern.compile("<meta name=\"citation_title\" content=\"(.*?)\"/>");
	private static final Pattern pages = Pattern.compile("<meta name=\"citation_firstpage\" content=\"(.*?)\"/>");
	private static final Pattern date = Pattern.compile("<meta name=\"citation_date\" content=\"(.*?)\"/>");
	private static final Pattern volume = Pattern.compile("<meta name=\"citation_volume\" content=\"(.*?)\"/>");
	private static final Pattern number = Pattern.compile("<meta name=\"citation_issue\" content=\"(.*?)\"/>");

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));
	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("(?s)Abstract.*\\s+<p>(.*)</p>\\s+<div class=\"article-keywords inline-list cleared\">");
	private static final Pattern REFERENCES_PATTERN = Pattern.compile("<a href=\"(.*)\">Download references</a>");
	
	
	private final RisToBibtexConverter ris = new RisToBibtexConverter();
	
	/** get INFO */
	@Override
	public String getInfo() {
		return INFO;
	}

	/**
	 * Gets the page content of a publication page. It can't be commonly applied since it violates
	 * RFC 2616.
	 * 
	 * @param url The url to the publication page
	 * @return the publication page as a String
	 * @throws IOException
	 */
	private static String getPageContent(URL url) throws IOException {
		HttpURLConnection con = null;
		InputStream in = null;
		try {
			con = (HttpURLConnection) url.openConnection();
			con.connect();
			//sometimes the page is behind an gzip stream. this will be indicated by response code 401.
			if (con.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
				in = new GZIPInputStream(con.getErrorStream());
			} else {
				in = con.getInputStream();
			}
			return WebUtils.inputStreamToStringBuilder(in, WebUtils.extractCharset(con.getContentType())).toString();
		} finally {
			if (con != null) con.disconnect();
			try {
				if (in != null) in.close();
			} catch (IOException ex) {}
		}
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

	private static String abstractParser(URL url){
		try {
			final Matcher m = ABSTRACT_PATTERN.matcher(WebUtils.getContentAsString(url));
			if (m.find()) {
				return m.group(1);
			}
		} catch(IOException e){
			log.error("error while getting abstract for " + url, e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#scrapeInternal(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		try {
			final String bibtexUrl = findBibtexUrl(sc.getUrl());
			if (ValidationUtils.present(bibtexUrl)) {
				sc.setBibtexResult(BibTexUtils.addFieldIfNotContained(ris.risToBibtex(WebUtils.getContentAsString(bibtexUrl)),"abstract",abstractParser(sc.getUrl())));
			} else {
				sc.setBibtexResult(constructBibtexFromHtmlMeta(sc));	
			}
			sc.setScraper(this);
			return true;
		} catch (final IOException e) {
			throw new ScrapingException(e);
		} catch (final ParseException pe) {
			throw new ScrapingException(pe);
		}
	}

	private String findBibtexUrl(final URL url) throws IOException {
		// get publication page
		final String publicationPage = getPageContent(url);
		// extract download citation link
		final Matcher linkMatcher = linkPattern.matcher(publicationPage);
		while (linkMatcher.find()) {
			final String link = linkMatcher.group();
			// check if link is download link
			if (link.contains(CITATION_DOWNLOAD_LINK_NAME) || link.contains(CITATION_DOWNLOAD_LINK_NAME2)) {
				// get href attribute
				final Matcher hrefMatcher = hrefPattern.matcher(link);
				if (hrefMatcher.find()) {
					final String href = hrefMatcher.group();
					// download citation (as RIS)
					return  "http://" + url.getHost() + "/" + href.substring(6, href.length() - 1);
				} 
			}
		}
		return null;
	}

	private String constructBibtexFromHtmlMeta(final ScrapingContext sc) throws IOException, ParseException {
		final URL url = sc.getUrl();
		final String content = WebUtils.getContentAsString(url.toExternalForm());
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

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext sc) throws ScrapingException {
		try {
			final Matcher m = REFERENCES_PATTERN.matcher(WebUtils.getContentAsString(sc.getUrl()));
			if(m.find()) {
				sc.setReferences(WebUtils.getContentAsString(SITE_URL + m.group(1)).trim());
				return true;
			}
		} catch (IOException e) {
			log.error("References are not available for " + sc.getUrl(), e);
		}
		return false;
	}
}
