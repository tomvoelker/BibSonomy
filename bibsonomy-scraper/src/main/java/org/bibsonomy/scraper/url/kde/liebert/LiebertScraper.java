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
package org.bibsonomy.scraper.url.kde.liebert;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 */
public class LiebertScraper extends AbstractUrlScraper {
	private static final Log log = LogFactory.getLog(LiebertScraper.class);

	private static final String SITE_NAME = "Liebert Online";
	private static final String LIEBERT_HOST_NAME  = "http://www.liebertonline.com";
	private static final String SITE_URL  = LIEBERT_HOST_NAME+"/";
	private static final String info = "This Scraper parses a publication from " + href(SITE_URL, SITE_NAME)+".";

	private static final String LIEBERT_HOST1  = "liebertonline.com";
	private static final String LIEBERT_HOST2  = "online.liebertpub.com";
	private static final String LIEBERT_BIBTEX_DOWNLOAD_PATH = "/action/downloadCitation";
	private static final String LIEBERT_BIBTEX_PARAMS = "?downloadFileName=bibsonomy&include=cit&format=bibtex&direct=on&doi=";

	// to extract the abstract from the HTML
	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("<div class=\"abstractSection.*?\">\\s*<p.*?>(.+?)</p>");

	// e.g., http://www.liebertonline.com/doi/abs/10.1089/152308604773934350
	private static final Pattern PATH_ABSTRACT_PATTERN = Pattern.compile("/doi/abs/(.+?)(\\?.+)?$");
	// e.g., http://www.liebertonline.com/action/showCitFormats?doi=10.1089%2F152308604773934350
	private static final Pattern PATH_CIT_PATTERN = Pattern.compile("/action/showCitFormats");
	private static final Pattern PAQU_CIT_PATTERN = Pattern.compile("/action/showCitFormats\\?doi=(.+?)(&.+)?$");

	private static final Pattern HOST_PATTERN1 = Pattern.compile(".*?" + LIEBERT_HOST1);
	private static final Pattern HOST_PATTERN2 = Pattern.compile(".*?" + LIEBERT_HOST2);

	private static final List<Pair<Pattern,Pattern>> PATTERNS = new LinkedList<Pair<Pattern,Pattern>>();
	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(HOST_PATTERN1, PATH_ABSTRACT_PATTERN));
		PATTERNS.add(new Pair<Pattern, Pattern>(HOST_PATTERN1, PATH_CIT_PATTERN));
		PATTERNS.add(new Pair<Pattern, Pattern>(HOST_PATTERN2, PATH_ABSTRACT_PATTERN));
		PATTERNS.add(new Pair<Pattern, Pattern>(HOST_PATTERN2, PATH_CIT_PATTERN));
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		final URL url = sc.getUrl();
		// extract id (DOI) from URL
		final String id = getId(url.toString());

		if (ValidationUtils.present(id)) {
			try {
				final URL citURL = new URL(LIEBERT_HOST_NAME + LIEBERT_BIBTEX_DOWNLOAD_PATH + LIEBERT_BIBTEX_PARAMS + id.replaceAll("/", "%2F"));
				// we need cookies for this publisher ...
				final String cookies = WebUtils.getCookies(url);
				final String bibResult = WebUtils.getContentAsString(citURL, cookies);

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
		throw new ScrapingFailureException("getting bibtex failed");
	}

	/**
	 * Attempts to extract the id (DOI) from the URL.
	 * 
	 * @param url
	 * @return
	 */
	private static String getId(final String url) {
		final Matcher m1 = PATH_ABSTRACT_PATTERN.matcher(url);
		if (m1.find()) {
			return m1.group(1);
		}
		final Matcher m2 = PAQU_CIT_PATTERN.matcher(url);
		if (m2.find()) {
			return m2.group(1);
		}
		return null;
	}

	private static String getAbstract(final URL url, final String cookies) throws IOException{
		final String contentAsString = WebUtils.getContentAsString(url, cookies);
		final Matcher m = ABSTRACT_PATTERN.matcher(contentAsString);
		if (m.find()) {
			return m.group(1).trim();
		}
		return null;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

}
