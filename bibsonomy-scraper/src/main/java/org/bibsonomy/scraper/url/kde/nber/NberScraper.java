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
package org.bibsonomy.scraper.url.kde.nber;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericRISURLScraper;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wbi
 */
public class NberScraper extends GenericRISURLScraper {

	private static final String SITE_URL = "http://www.nber.org/";
	private static final String SITE_NAME = "National Bureau of Economic Research";
	private static final String info = "This Scraper parses a publication from " + href(SITE_URL, SITE_NAME)+".";

	private static final String NBER_HOST  = "www.nber.org";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + NBER_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern URL_CODE_PATTERN = Pattern.compile("/papers/(w\\d{5})");
	
	public String getInfo() {
		return info;
	}

	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		return url.toExternalForm();
	}

	@Override
	protected List<NameValuePair> getDownloadData(URL url, String cookies) throws ScrapingException {
		String code;
		String urlPath = url.getPath();
		Matcher m_code = URL_CODE_PATTERN.matcher(urlPath);
		if (m_code.find()){
			code = m_code.group(1);
		}else {
			throw new ScrapingException("can't get code from " + url);
		}

		List<NameValuePair> postData = new LinkedList<>();
		postData.add(new BasicNameValuePair("code", code));
		postData.add(new BasicNameValuePair("file_type", "ris"));
		postData.add(new BasicNameValuePair("form_id", "download_citation"));

		return postData;
	}

	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}

}
