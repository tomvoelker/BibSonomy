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
package org.bibsonomy.scraper.url.kde.nasaads;

import static org.bibsonomy.util.ValidationUtils.present;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scraper for NASA ADS.
 * Collects BibTeX snippets and single references (HTML page or BibTeX page).
 *   
 * @author rja
 */
public class NasaAdsScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "The SAO/NASA Astrophysics Data System";
	private static final String SITE_HOST = "adsabs.harvard.edu";
	private static final String SITE_URL = "http://" + SITE_HOST + "/";

	private static final String INFO = "Extracts publications from " + href(SITE_URL, SITE_NAME) + 
			". Publications can be extracted as a selected BibTeX snippet (one or more publications) or by the URL of a single reference.";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<>(Pattern.compile(".*" + SITE_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern URL_BIBCODE_PATTERN = Pattern.compile("/abs/([A-Za-z\\d.]*)");
	private static final String DOWNLOAD_URL = "https://ui.adsabs.harvard.edu/v1/export/bibtex";
	private static final String AUTHORIZATION_URL = "https://ui.adsabs.harvard.edu/v1/accounts/bootstrap";


	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);

		try {
			URL url = WebUtils.getRedirectUrl(scrapingContext.getUrl());
			if (!present(url)){
				url = scrapingContext.getUrl();
			}

			String authorizationJson = WebUtils.getContentAsString(AUTHORIZATION_URL);
			if (!present(authorizationJson)){
				throw new ScrapingException("can't get authorization-json from " + authorizationJson);
			}
			String authorizationHeaderValue = getAuthorizationTokenFromJson(authorizationJson);
			if (!present(authorizationHeaderValue)){
				throw new ScrapingException("couln't get authorization token from " + authorizationJson);
			}

			String bibcode;
			Matcher m_bibcode = URL_BIBCODE_PATTERN.matcher(url.getPath());
			if (m_bibcode.find()){
				bibcode = m_bibcode.group(1);
			}else {
				throw new ScrapingException(url + " did not contain bibcode");
			}

			HttpPost post = new HttpPost(DOWNLOAD_URL);
			post.setHeader("Content-Type", "application/json");
			post.setHeader("Authorization", authorizationHeaderValue);
			String jsonForPost = "{\"bibcode\":[\""+ bibcode +"\"],\"sort\":[\"date desc, bibcode desc\"],\"maxauthor\":[0],\"authorcutoff\":[200],\"journalformat\":[1]}";
			post.setEntity(new StringEntity(jsonForPost));

			String bibtexJson = WebUtils.getContentAsString(WebUtils.getHttpClient(), post);
			if (!present(bibtexJson)){
				throw new ScrapingException("can't get json of bibtex from " + DOWNLOAD_URL);
			}

			String bibtex = JsonToBibtex(bibtexJson);
			if (!present(bibtex)){
				throw new ScrapingException(bibtexJson + " did not contain bibtex");
			}

			scrapingContext.setBibtexResult(bibtex);
			return true;
		} catch (final IOException | HttpException e) {
			throw new ScrapingFailureException(e);
		}
	}

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

	protected String JsonToBibtex(String json) {
		JSONArray hostArray = JSONArray.fromObject("["+json+"]");
		JSONObject hostObject = hostArray.getJSONObject(0);
		String bibtex = hostObject.getString("export");
		if (present(bibtex)){
			return bibtex;
		}
		return null;
	}

	protected String getAuthorizationTokenFromJson(String json) {
		JSONArray hostArray = JSONArray.fromObject("["+json+"]");
		JSONObject hostObject = hostArray.getJSONObject(0);
		String tokenType = hostObject.getString("token_type");
		String accessToken = hostObject.getString("access_token");
		if (present(tokenType)&&present(accessToken)){
			return tokenType + ":" +  accessToken;
		}
		return null;
	}

}
