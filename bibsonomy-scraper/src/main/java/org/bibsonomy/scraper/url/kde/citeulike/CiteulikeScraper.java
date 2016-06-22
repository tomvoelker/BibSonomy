/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.citeulike;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for citeulike.org
 * @author tst
 */
public class CiteulikeScraper extends GenericBibTeXURLScraper {

	private static final String SITE_NAME = "CiteUlike";

	private static final String SITE_URL = "http://www.citeulike.org/";

	private static final String INFO = "Scrapes publications from " + href(SITE_URL, SITE_NAME)+".";

	private static final String HOST = "citeulike.org";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));

	private static final String ARTICLE_POSTS = "article-posts";

	private static final String ARTICLE = "article";

	private static final String BIBTEX = "/bibtex";

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
		return SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	@Override
	public String getDownloadURL(URL url, String cookies) throws ScrapingException {
		String downloadUrl = url.toString();

		// build bibtex download URL
		if (downloadUrl.contains(ARTICLE_POSTS)) {
			downloadUrl = downloadUrl.replace(ARTICLE_POSTS, ARTICLE);
			try {
				downloadUrl = WebUtils.getRedirectUrl(new URL(downloadUrl)).toString();
			} catch (final MalformedURLException ex) {
				throw new InternalFailureException(ex);
			}
		}

		return downloadUrl.replace(HOST, HOST + BIBTEX);
	}

}
