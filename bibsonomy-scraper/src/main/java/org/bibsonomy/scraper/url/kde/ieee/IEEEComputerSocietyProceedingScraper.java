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
package org.bibsonomy.scraper.url.kde.ieee;

import net.sf.json.JSONObject;
import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IEEEComputerSocietyProceedingScraper extends GenericBibTeXURLScraper {
	private static final String SITE_NAME = "IEEE Computer Society";
	private static final String SITE_URL = "https://www.computer.org";
	private static final String INFO = "Scraper for publications from " + href(SITE_URL, SITE_NAME);
	private static final String HOST = "computer.org";

	private static final List<Pair<Pattern, Pattern>> patterns = Arrays.asList(
					new Pair<>(Pattern.compile(".*" + HOST), Pattern.compile("csdl/proceedings-article/"))
	);

	private static final Pattern URL_ARTICLE_ID_PATTERN = Pattern.compile("([A-z1-9]*)$");
	private static final String API_URL = "https://www.computer.org/csdl/api/v1/graphql";
	private static final String START_OF_DOWNLOAD_URL = "https://www.computer.org/csdl/api/v1/citation/bibtex/proceedings/";

	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		Matcher m_articleId = URL_ARTICLE_ID_PATTERN.matcher(url.toString());
		if (m_articleId.find()){
			try {
				String articleId = m_articleId.group(1);

				HttpPost post = new HttpPost(API_URL);
				// we request the parameters, which we need for the download link, from the api
				String postBody = "{\"operationName\":null,\"variables\":{\"articleId\":\""+articleId+"\"},\"query\":\"query ($articleId: String!) {\\n  proceeding: proceedingByArticleId(articleId: $articleId) {id}\\n  article: articleById(articleId: $articleId) {fno}\\n}\\n\"}";
				post.setEntity(new StringEntity(postBody));
				post.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
				// the response of the api is a json.
				String responseContent= WebUtils.getContentAsString(WebUtils.getHttpClient(), post);

				JSONObject json = JSONObject.fromObject(responseContent);
				String fno = json.getJSONObject("data").getJSONObject("article").getString("fno");
				String id = json.getJSONObject("data").getJSONObject("proceeding").getString("id");
				return START_OF_DOWNLOAD_URL + id + "/" + fno;
			} catch (HttpException e) {
				throw new ScrapingException(e);
			}
		}
		return null;
	}

	@Override
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		//extracts the bibtex from the html
		Matcher m_bibtex = Pattern.compile("<pre>(.*?)</pre>", Pattern.DOTALL).matcher(bibtex);
		String cleanedBibtex = bibtex;
		if (m_bibtex.find()){
			cleanedBibtex = m_bibtex.group(1);
		}
		//Bibtex has no key. So we just use the doi as key.
		Matcher m_doi = Pattern.compile("doi = \\{(.*?)}").matcher(cleanedBibtex);
		if (m_doi.find()){
			return cleanedBibtex.replaceAll("\\{,", "\\{" + m_doi.group(1)+",");
		}
		return bibtex;
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
		return INFO;
	}
}
