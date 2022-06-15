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
package org.bibsonomy.scraper.generic;

import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpGet;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
For sites which use a Get-Requests with Citation/Download as Path and resourceId, resourceType, citationFormat as parameters.
 */

public abstract class CitationManager2Scraper extends AbstractUrlScraper {

	private static final Pattern ARTICLE_PATTERN = Pattern.compile("article(?:-abstract)?/(?:doi|\\d*)/.*?/.*?/(\\d*)/?");
	private static final Pattern BOOK_PATTERN = Pattern.compile("book/(\\d+)");
	private static final Pattern CHAPTER_PATTERN = Pattern.compile("book/\\d*/chapter(?:-abstract)?/(\\d*)/?");

	private static final String DOWNLOAD_PATH = "/Citation/Download";

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		URL url = sc.getUrl();

		Matcher m_article = ARTICLE_PATTERN.matcher(url.toString());
		String downloadParams = null;
		if (m_article.find()) {
			downloadParams = "?resourceType=3&citationFormat=2&resourceId=" + m_article.group(1);
		} else {
			Matcher m_chapter = CHAPTER_PATTERN.matcher(url.toString());
			if (m_chapter.find()) {
				downloadParams = "?resourceType=3&citationFormat=2&resourceId=" + m_chapter.group(1);
			} else {
				Matcher m_book = BOOK_PATTERN.matcher(url.toString());
				if (m_book.find()) {
					downloadParams = "?resourceType=1&citationFormat=2&resourceId=" + m_book.group(1);
				}
			}
		}
		String downloadUrl = normalizeSiteURL(this.getSupportedSiteURL()) + DOWNLOAD_PATH + downloadParams;
		HttpGet get = new HttpGet(downloadUrl);

		try {
			String cookies = getCookies(url);
			if (cookies!=null){
				get.setHeader("Cookie", cookies);
			}
			String bibtex = WebUtils.getContentAsString(WebUtils.getHttpClient(), get);
			sc.setBibtexResult(HtmlUtils.htmlUnescape(bibtex));
			return true;
		} catch (HttpException | IOException e) {
			throw new ScrapingException(e);
		}

	}
	protected String getCookies(URL url) throws IOException {
		return null;
	}

	private static String normalizeSiteURL(String siteURL){
		try {
			URL url = new URL(siteURL);
			return "https://" + url.getHost();
		} catch (MalformedURLException e) {
			return siteURL;
		}
	}
}


