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
package org.bibsonomy.scraper.url.kde.genome;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author Mohammed Abed
 */
public class GenomeBiologyScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Genome Biology";
	private static final String GENOMEBIOLOGY_HOST_NAME = "http://www.genomebiology.com";
	private static final String SITE_URL = GENOMEBIOLOGY_HOST_NAME + "/";
	private static final String info = "This Scraper parse a publication from " + href(SITE_URL, SITE_NAME) + ".";
	private static final String GENOMEBIOLOGY_HOST = "genomebiology.com";

	private static final String GENOMEBIOLOGY_BIBTEX_PATH = "citation";
	private static final String GENOMEBIOLOGY_BIBTEX_PARAMS = "format=bibtex&include=cit&direct=on&action=submit";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + GENOMEBIOLOGY_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		final String url = sc.getUrl().toString();
		final String bibtexDownloadUrl = appendIfNotPresent(url, GENOMEBIOLOGY_BIBTEX_PATH);

		try {
			final String bibResult = WebUtils.getPostContentAsString(new URL(bibtexDownloadUrl), GENOMEBIOLOGY_BIBTEX_PARAMS);
			if (bibResult != null) {
				sc.setBibtexResult(bibResult);
				return true;
			}
		} catch (final IOException ex) {
			throw new ScrapingFailureException(ex);
		}
		return false;
	}

	/**
	 * @param url
	 * @param gnomebiologyBibtexPath
	 * @return
	 */
	private static String appendIfNotPresent(String url, final String subPath) {
		// norm url
		if (!url.endsWith("/")) {
			url += "/";
		}
		// check for existing subPath
		if (!url.endsWith(subPath + "/")) {
			return url + subPath;
		}
		return url;
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

	@Override
	public String getInfo() {
		return info;
	}
}
