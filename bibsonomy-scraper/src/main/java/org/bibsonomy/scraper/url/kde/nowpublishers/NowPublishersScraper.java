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

package org.bibsonomy.scraper.url.kde.nowpublishers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.WebUtils;

/** Scrapes publications from NowPublishers.
 * 
 * @author Mohammed Abed
 *
 */
public class NowPublishersScraper extends AbstractUrlScraper{

	private static final String SITE_NAME = "Now The essence of Knowledge";
	private static final String SITE_URL = "http://www.nowpublishers.com";
	private static final String INFO 	= "Scrapes publications from " + href(SITE_URL, SITE_NAME);
	private static final String NOWPUBLISHERS_HOST = "nowpublishers.com";
	private static final String NOWPUBLISHERS_URL_PATH_START = "/article/Details/";
	private static final Pattern formPublicationIdPattern = Pattern.compile("<input.*type=\"hidden\".*name=\"id\".*value=\"(.*)\".*>");
	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();
	static{
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + NOWPUBLISHERS_HOST), Pattern.compile(NOWPUBLISHERS_URL_PATH_START + ".*")));
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
		return INFO;
	}

	public String getDownloadURL(URL url) throws ScrapingException {
		return "";
	}
	
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
	
	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		
		final String articlePageContent = sc.getPageContent();
		final Matcher publicationIdMatcher = formPublicationIdPattern.matcher(articlePageContent);
		String pubId = "";
		while (publicationIdMatcher.find()) {
			pubId = publicationIdMatcher.group(1);
		}
		
		final String postArgs = "id="+ pubId + 
				"&format=BIB";
		
		String bibtex = "";
		try {
			bibtex = WebUtils.getPostContentAsString(new URL("http://www.nowpublishers.com/article/ExportCitation"), postArgs, StringUtils.CHARSET_UTF_8);
		} catch (MalformedURLException ex) {
			throw new ScrapingFailureException("URL to scrape does not exist. It maybe malformed.");
		} catch (IOException ex) {
			throw new ScrapingFailureException("An unexpected IO error has occurred. Maybe NowPublishers is down.");
		}
		if (bibtex != null) {
			sc.setBibtexResult(bibtex.trim());
			return true;
		}
		return false;
	}
}
