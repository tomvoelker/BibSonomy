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
package org.bibsonomy.scraper.url.kde.apa;

import org.apache.http.HttpException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hagen
 */
public class APAScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "American Psychological Association";
	private static final String SITE_URL = "http://www.apa.org/";
	private static final String INFO = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME)+".";

	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = new ArrayList<Pair<Pattern,Pattern>>();

	static {
		URL_PATTERNS.add(new Pair<>(Pattern.compile(".*" + "psycnet.apa.org"), EMPTY_PATTERN));
	}


	private static final Pattern URL_UID_PATTERN = Pattern.compile("https://psycnet\\.apa\\.org/record/(.*)");
	private static final String VISIT_SECOND_URL = "https://psycnet.apa.org/api/request/record.exportRISFile";
	private static final String VISIT_THIRD_URL = "https://psycnet.apa.org/ris/download";

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
		return INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}

	@Override
	protected boolean scrapeInternal(final ScrapingContext scrapingContext) throws ScrapingException{
		scrapingContext.setScraper(this);

		try {
			HttpClient client = WebUtils.getHttpClient();
			String url = scrapingContext.getUrl().toExternalForm();

			String uid = "";
			Matcher m_uid = URL_UID_PATTERN.matcher(url);
			if (m_uid.find()) uid = m_uid.group(1);

			String userAgentHeader = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36";

			HttpGet get1 = new HttpGet(url);
			get1.addHeader("User-Agent", userAgentHeader);
			String cookie = WebUtils.getHeaders(client, get1, "set-cookie");

			HttpPost post = new HttpPost(VISIT_SECOND_URL);
			post.setHeader("Cookie", cookie);
			post.setHeader("Content-Type", "application/json");
			post.setEntity(new StringEntity("{\"api\":\"record.exportRISFile\",\"params\":{\"UIDList\":[{\"UID\":\"" + uid + "\",\"ProductCode\":\"PA\"}],\"exportType\":\"referenceSoftware\"}}"));
			WebUtils.getContentAsString(client, post);

			HttpGet get2 = new HttpGet(VISIT_THIRD_URL);
			get2.addHeader("Cookie", cookie);
			get2.addHeader("User-Agent", userAgentHeader);
			String ris = WebUtils.getContentAsString(client, get2);

			scrapingContext.setBibtexResult(RIS2BIB.toBibtex(ris));
			return true;
		} catch (IOException | HttpException e) {
			e.printStackTrace();
			return false;
		}
	}
}
