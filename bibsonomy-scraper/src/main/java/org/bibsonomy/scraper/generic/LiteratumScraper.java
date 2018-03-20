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
package org.bibsonomy.scraper.generic;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for sites running the online publishing software Atypon Literatum.
 * 
 * @author rja
 */
public abstract class LiteratumScraper extends AbstractUrlScraper {

	private static final Log log = LogFactory.getLog(LiteratumScraper.class);

	private static final String BIBTEX_DOWNLOAD_PATH = "/action/downloadCitation";
	private static final String BIBTEX_PARAMS = "?include=abs&format=bibtex&direct=on&doi=";
	// private static final String BIBTEX_PARAMS = "?downloadFileName=f&include=cit&format=bibtex&direct=on&doi=";

	// to extract the DOI from the URL
	private static final Pattern PATH_ABSTRACT_PATTERN = Pattern.compile("/doi/(abs|full|pdf|pdfplus)/(.+?)(\\?.+)?$");
	private static final int PATH_ABSTRACT_PATTERN_DOI_GROUP = 2;
	// to extract the DOI from the query
	private static final Pattern QUERY_DOI_PATTERN = Pattern.compile("doi=(.+?)(&.+)?$");
	private static final int QUERY_DOI_PATTERN_DOI_GROUP = 1;
	// to extract the abstract from the HTML
	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("<div class=\"abstractSection.*?\">\\s*<p.*?>(.+?)</p>");
	private static final int ABSTRACT_PATTERN_ABSTRACT_GROUP = 1;


	/**
	 * 
	 * @param sc
	 * @return the scraped BibTeX
	 * @throws ScrapingException
	 */
	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		final URL url = sc.getUrl();
		final String doi = getDOI(url);

		if (ValidationUtils.present(doi)) {
			try {
				final String citUrl = url.getProtocol() + "://" + url.getHost() + BIBTEX_DOWNLOAD_PATH + BIBTEX_PARAMS + UrlUtils.safeURIEncode(doi);
				final String cookies = getCookies(url);
				final String bibtex = WebUtils.getContentAsString(citUrl, cookies, getPostContent(doi), null);

				if (ValidationUtils.present(bibtex)) {
					// download and add abstract, if necessary
					final String bibtexWithAbstract = addAbstract(bibtex, url, cookies);
					// postprocess BibTeX
					sc.setBibtexResult(postProcessBibtex(sc, bibtexWithAbstract));
					return true;
				}

			} catch (IOException ex) {
				throw new InternalFailureException(ex);
			}

		}
		throw new ScrapingFailureException("getting BibTeX failed (could not extract id)");
	}

	/**
	 * @param bibtex
	 * @param url
	 * @param cookies
	 * @return the bibtex with the abstract added
	 */
	protected String addAbstract(final String bibtex, final URL url, final String cookies) {
		if (downloadAbstract()) {
			try {
				return BibTexUtils.addFieldIfNotContained(bibtex, "abstract", getAbstract(url, cookies));
			} catch (IOException e) {
				log.warn("error while scraping the abstract for " + url, e);
			}
		}
		return bibtex;
	}

	/**
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private String getCookies(final URL url) throws IOException {
		// do we need cookies for this publisher?
		if (requiresCookie()) {
			return WebUtils.getCookies(url);
		} 
		return null;
	}

	/**
	 * Override this method if a cookie must be retrieved before downloading BibTeX
	 * @param url
	 * @return <code>true</code> if cookie shall be downloaded
	 */
	protected boolean requiresCookie() {
		return false;
	}

	/**
	 * If the abstract is not contained in the BibTeX, this method should return <code>true</code>
	 * such that it is downloaded separately.
	 * 
	 * @return <code>false</code>
	 */
	protected boolean downloadAbstract() {
		return false;
	}

	/**
	 * Override this if a HTTP POST request shall be made
	 * @param doi
	 * @return the string representing the content of the POST request's body
	 */
	protected List<NameValuePair> getPostContent(final String doi) {
		return null;
	}

	/**
	 * Attempts to extract the id (DOI) from the URL.
	 * 
	 * @param url
	 * @return
	 */
	private static String getDOI(final URL url) {
		final Matcher m1 = PATH_ABSTRACT_PATTERN.matcher(url.getPath());
		if (m1.find()) {
			return m1.group(PATH_ABSTRACT_PATTERN_DOI_GROUP);
		}
		final Matcher m2 = QUERY_DOI_PATTERN.matcher(url.getQuery());
		if (m2.find()) {
			return UrlUtils.safeURIDecode(m2.group(QUERY_DOI_PATTERN_DOI_GROUP));
		}
		return null;
	}

	/**
	 * @param url
	 * @param cookies
	 * @return the abstract
	 * @throws IOException
	 */
	protected static String getAbstract(final URL url, final String cookies) throws IOException {
		final String contentAsString = WebUtils.getContentAsString(url, cookies);
		final Matcher m = ABSTRACT_PATTERN.matcher(contentAsString);
		if (m.find()) {
			return m.group(ABSTRACT_PATTERN_ABSTRACT_GROUP).trim();
		}
		return null;
	}

	/**
	 * Override this method in case the scraped BibTeX needs to be "polished".
	 * 
	 * @param scrapingContext
	 * @param bibtex
	 * @return the postprocessed bibtex
	 */
	protected String postProcessBibtex(ScrapingContext scrapingContext, String bibtex) {
		return bibtex;
	}

}