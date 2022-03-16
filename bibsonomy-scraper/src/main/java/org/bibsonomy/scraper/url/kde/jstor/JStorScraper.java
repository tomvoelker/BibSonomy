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
package org.bibsonomy.scraper.url.kde.jstor;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpGet;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 */
public class JStorScraper extends AbstractUrlScraper {

	private static final String info = "This Scraper parses a publication from " + href("http://www.jstor.org/", "JSTOR");
	private static final String SITE_NAME = "Jstor";
	private static final String JSTOR_HOST  = "jstor.org";
	private static final String JSTOR_HOST_NAME  = "http://www.jstor.org";
	private static final String JSTOR_STABLE_PATH = "/stable/";
	private static final String DOWNLOAD_URL = JSTOR_HOST_NAME + "/citation/text/"; //http://www.jstor.org/citation/text/10.2307/4142852
	private static final List<Pair<Pattern,Pattern>> patterns = new LinkedList<>();
	private static final Pattern DOI = Pattern.compile("\"objectDOI\" : \"(.*?)\"");

	static {
		final Pattern hostPattern = Pattern.compile(".*" + JSTOR_HOST);
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, Pattern.compile(JSTOR_STABLE_PATH + ".*")));
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		try {
			//we can access the main-page of jstor to get the cookies, if we don't retrieve the cookies first then the other urls return http-403
			String cookies = WebUtils.getCookies( new URL("https://www.jstor.org/"));

			HttpGet get = new HttpGet(sc.getUrl().toString());
			get.setHeader("Cookie", cookies);
			String pageContent = WebUtils.getContentAsString(WebUtils.getHttpClient(), get);

			final Matcher m_doi = DOI.matcher(pageContent);
			if (!m_doi.find()) {
				throw new ScrapingException("DOI not found");
			}

			String downloadUrl = DOWNLOAD_URL + m_doi.group(1);
			String bibtex = WebUtils.getContentAsString(downloadUrl);

			sc.setBibtexResult(bibtex);
			return true;
		} catch (IOException | HttpException e) {
			throw new ScrapingException(e);
		}
	}

	@Override
	public String getInfo() {
		return info;
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
		return JSTOR_HOST_NAME;
	}
}
