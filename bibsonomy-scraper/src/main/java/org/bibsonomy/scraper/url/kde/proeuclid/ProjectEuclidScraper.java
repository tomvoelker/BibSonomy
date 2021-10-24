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
package org.bibsonomy.scraper.url.kde.proeuclid;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.DOIUtils;

/**
 * scraper for the Astronomy and Astrophysics
 *
 * @author rja
 */
public class ProjectEuclidScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Astronomy and Astrophysics";
	private static final String SITE_HOST = "projecteuclid.org";
	private static final String SITE_URL = "https://" + SITE_HOST + "/";
	private static final String INFO = "Scraper for references from " + href(SITE_URL, SITE_NAME)+".";
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<>(Pattern.compile(".*"+ SITE_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	private static final String DOWNLOAD_URL = "https://projecteuclid.org/citation/download";

	private static final Pattern URL_ID_PATTERN = Pattern.compile("/([A-z]*/\\d*)\\.full");



	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		try {

			URL url = WebUtils.getRedirectUrl(scrapingContext.getUrl());
			if (!present(url)){
				url = scrapingContext.getUrl();
			}
			url = new URL(url.getProtocol() + "://" + url.getHost() + url.getPath());


			String id = DOIUtils.getDoiFromURL(url);
			if (!present(id)){
				Matcher m_id = URL_ID_PATTERN.matcher(url.toExternalForm());
				if (m_id.find())id = m_id.group(1);
			}else {
				id = id.replaceAll("\\.full|\\.short", "");
			}

			HttpClient client = WebUtils.getHttpClient();
			HttpPost post = new HttpPost(DOWNLOAD_URL);
			post.setHeader("Content-Type", "application/json; charset=UTF-8");
			post.setEntity(new StringEntity("{\"contentType\":\"0\",\"formatType\":\"2\",\"referenceType\":\"\",\"urlid\":\"" + id + "\"}"));
			String urlId = WebUtils.getContentAsString(client, post).replaceAll("\n", "");

			String fullDownloadUrl = DOWNLOAD_URL + "/" + URLEncoder.encode(urlId, "UTF-8");

			String bibtex = WebUtils.getContentAsString(fullDownloadUrl);
			if (present(bibtex)){
				scrapingContext.setBibtexResult(bibtex);
				return true;
			}

		} catch (HttpException | IOException e) {
			throw new ScrapingFailureException(e);
		}
		return false;
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

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

}
