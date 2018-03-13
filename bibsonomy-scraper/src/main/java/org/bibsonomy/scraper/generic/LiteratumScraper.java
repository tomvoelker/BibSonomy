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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for sites running the online publishing software Atypon Literatum.
 * 
 * TODO: check whether this scraper should be moved to the generic package and not 
 * be a subclass from {@link AbstractUrlScraper} - although that wo
 * 
 * @author wbi
 */
public class LiteratumScraper implements Scraper {

	private static final Log log = LogFactory.getLog(LiteratumScraper.class);

	private static final String INFO = "This scraper parses publications from the following sites: ";

	private static final String BIBTEX_DOWNLOAD_PATH = "/action/downloadCitation";
	private static final String BIBTEX_PARAMS = "?downloadFileName=f&include=cit&format=bibtex&direct=on&doi=";

	// to extract the abstract from the HTML
	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("<div class=\"abstractSection.*?\">\\s*<p.*?>(.+?)</p>");

	// e.g., http://www.liebertonline.com/doi/abs/10.1089/152308604773934350
	private static final String PATH_DOI_ABS = "/doi/abs/";
	private static final Pattern PATH_ABSTRACT_PATTERN = Pattern.compile("/doi/abs/(.+?)(\\?.+)?$");
	// e.g., http://www.liebertonline.com/action/showCitFormats?doi=10.1089%2F152308604773934350
	private static final String PATH_ACTION_SHOW_CIT_FORMATS = "/action/showCitFormats";
	private static final Pattern QUERY_DOI_PATTERN = Pattern.compile("doi=(.+?)(&.+)?$");


	private static final Set<String> HOST_NAMES = new HashSet<String>();
	static {
		HOST_NAMES.add("www.liebertonline.com");
		HOST_NAMES.add("online.liebertpub.com");
		HOST_NAMES.add("econtent.hogrefe.com");
	}
	private static final String HOST_NAMES_INFO = String.join(", ", HOST_NAMES);

	
	@Override
	public String getInfo() {
		return INFO + HOST_NAMES_INFO;
	}

	/**
	 * 
	 * @param sc
	 * @return the scraped BibTeX
	 * @throws ScrapingException
	 */
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		final URL url = sc.getUrl();
		// extract id (DOI) from URL
		final String id = getId(url);

		if (ValidationUtils.present(id)) {
			try {
				final URL citUrl = new URL(url.getProtocol() + "://" + url.getHost() + BIBTEX_DOWNLOAD_PATH + BIBTEX_PARAMS + id.replaceAll("/", "%2F"));
				// we need cookies for this publisher ...
				final String cookies = WebUtils.getCookies(url);
				final String bibResult = WebUtils.getContentAsString(citUrl, cookies);

				if (ValidationUtils.present(bibResult)) {
					try {
						sc.setBibtexResult(BibTexUtils.addFieldIfNotContained(bibResult, "abstract", getAbstract(url, cookies)));
					} catch (IOException e) {
						log.error("error while scraping " + url, e);
					}
					return true;
				}

			} catch (IOException ex) {
				throw new InternalFailureException(ex);
			}

		}
		throw new ScrapingFailureException("getting BibTeX failed (could not extract id)");
	}

	/**
	 * Attempts to extract the id (DOI) from the URL.
	 * 
	 * @param url
	 * @return
	 */
	private static String getId(final URL url) {
		final Matcher m1 = PATH_ABSTRACT_PATTERN.matcher(url.getPath());
		if (m1.find()) {
			return m1.group(1);
		}
		final Matcher m2 = QUERY_DOI_PATTERN.matcher(url.getQuery());
		if (m2.find()) {
			return m2.group(1);
		}
		return null;
	}

	private static String getAbstract(final URL url, final String cookies) throws IOException {
		final String contentAsString = WebUtils.getContentAsString(url, cookies);
		final Matcher m = ABSTRACT_PATTERN.matcher(contentAsString);
		if (m.find()) {
			return m.group(1).trim();
		}
		return null;
	}


	@Override
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if (ValidationUtils.present(sc) && this.supportsScrapingContext(sc)) {
			return this.scrapeInternal(sc);
		}
		return false;
	}

	@Override
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper>singletonList(this);
	}

	@Override
	public boolean supportsScrapingContext(ScrapingContext scrapingContext) {
		return supportsUrl(scrapingContext.getUrl());
	}
	
	/**
	 * @param url
	 * @return whether the URL is supported
	 */
	protected boolean supportsUrl(final URL url) {
		if (ValidationUtils.present(url)) {
			if (HOST_NAMES.contains(url.getHost())) {
				return url.getPath().startsWith(PATH_DOI_ABS) || url.getPath().startsWith(PATH_ACTION_SHOW_CIT_FORMATS);
			}
		}
		return false;
	}
}