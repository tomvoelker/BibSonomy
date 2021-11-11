/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.scraper.url.kde.emerald;

import static org.bibsonomy.util.ValidationUtils.present;
import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.DOIUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This scraper supports download links from emeraldinsight.com
 * 
 * FIXME: currently does not work, as the server sends a 302 redirect response for the 
 * POST request and the HttpClient does not support following redirects for POST requests.
 * Needs to be handled by manually implementing the request handling using the HttpClient
 * directly 
 * 
 * @author Mohammed Abed
 */
public class EmeraldScraper extends AbstractUrlScraper {
	private static final String SITE_NAME = "Emerald Publishing";
	private static final String SITE_HOST = "emerald.com";
	private static final String SITE_URL  = "https://" + SITE_HOST + "/";
	private static final String SITE_INFO = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";


	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*"+ SITE_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	private static final String DOWNLOAD_URL = "https://www.emerald.com/insight/api/citations/format/ris";

	private static final RisToBibtexConverter RIS2BIB = new RisToBibtexConverter();

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
		return SITE_INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		try {
			URL url = WebUtils.getRedirectUrl(scrapingContext.getUrl());
			if (!present(url)){
				url = scrapingContext.getUrl();
			}

			String doi = DOIUtils.getDoiFromURL(url);
			if (!present(doi)){
				throw new ScrapingException("can't get doi from " + url);
			}
			doi = doi.replaceAll("/[a-z]*/[a-z]*$", "");

			HttpPost post = new HttpPost(DOWNLOAD_URL);
			post.setHeader("Content-Type", "application/json; charset=UTF-8");
			StringEntity postBody = new StringEntity("{\"dois\":[\""+doi+"\"]}");
			post.setEntity(postBody);
			String ris = WebUtils.getContentAsString(WebUtils.getHttpClient(), post);
			if (!present(ris)){
				throw new ScrapingException("can't get ris from" + DOWNLOAD_URL + " with doi " + doi);
			}

			String bibtex = RIS2BIB.toBibtex(ris);
			scrapingContext.setBibtexResult(bibtex);
			return true;
		} catch (HttpException | IOException e) {
			throw new ScrapingException(e);
		}
	}

}
