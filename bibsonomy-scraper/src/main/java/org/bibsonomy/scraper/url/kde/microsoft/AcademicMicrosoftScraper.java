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
package org.bibsonomy.scraper.url.kde.microsoft;

import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AcademicMicrosoftScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Microsoft Academic";
	private static final String SITE_HOST = "academic.microsoft.com";
	private static final String SITE_URL = "https://academic.microsoft.com/";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(
					new Pair<>(Pattern.compile(".*" + SITE_HOST), Pattern.compile("paper"))
	);

	private static final String DOWNLOAD_URL = "https://academic.microsoft.com/api/bib";
	private static final Pattern URL_ID_PATTERN = Pattern.compile("paper/(\\d+)/");

	private static final Pattern EXTRACT_BIBTEX_PATTERN = Pattern.compile("(@.*})");

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		try {
			Matcher m_id = URL_ID_PATTERN.matcher(sc.getUrl().getPath());
			String id;
			if (m_id.find()){
				id = m_id.group(1);
			}else {
				throw new ScrapingException("id not found in URL " + sc.getUrl().toString());
			}
			HttpPost post = new HttpPost(DOWNLOAD_URL);
			String postBody = "{\"format\":1,\"paperIds\":["+ id +"]}";
			post.setHeader("Content-Type", "application/json; charset=utf-8");
			post.setEntity(new StringEntity(postBody));
			String bibtex = WebUtils.getContentAsString(WebUtils.getHttpClient(), post);
			bibtex = postProcessBibtex(bibtex);
			sc.setBibtexResult(bibtex);
			return true;

		} catch (HttpException | IOException e) {
			throw new ScrapingException(e);
		}
	}

	private String postProcessBibtex(String bibtex) throws ScrapingException {
		Matcher m_bibtex = EXTRACT_BIBTEX_PATTERN.matcher(bibtex);
		if (m_bibtex.find()){
			return m_bibtex.group(1).replaceAll("\\\\r\\\\n(?:\\\\t)?", "\r\n\t").replace("\\\"", "\"");
		}else {
			throw new ScrapingException("no bibtex found in " + bibtex);
		}
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

	@Override
	public String getInfo() {
		return INFO;
	}
}
