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
package org.bibsonomy.scraper.url.kde.igiglobal;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * scraper igi global
 *
 * @author Haile
 */
public class IGIGlobalScraper extends GenericBibTeXURLScraper {

	private static final String SITE_NAME = "IGI Global";
	private static final String SITE_URL = "https://www.igi-global.com";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);

	private static final Pattern EVENTVALIDATION = Pattern.compile("<input type=\"hidden\" name=\"__EVENTVALIDATION\" id=\"__EVENTVALIDATION\" value=\"(.*?)\" />");
	private static final Pattern VIEWSTATE = Pattern.compile("<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"(.*?)\" />");
	private static final Pattern SUBMIT_ENDNOTE = Pattern.compile("<input type=\"image\" name=\"(.*?)\" id=\".*?\" src=\"https://coverimages\\.igi-global\\.com/images/endnote\\.png\" alt=\"EndNote\" onclick=\"this\\.form\\.target=&quot;_blank&quot;;\" />");

	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(
					new Pair<>(Pattern.compile(".*" + "igi-global.com"), AbstractUrlScraper.EMPTY_PATTERN)
	);

	private static final RisToBibtexConverter RIS2BIB = new RisToBibtexConverter();

	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		// can t download over http
		if (url.getProtocol().equals("http")){
			return "https" + url.toExternalForm().substring(4);
		}
		return url.toExternalForm();
	}

	@Override
	protected List<NameValuePair> getDownloadData(URL url, String cookies) {
		String html = null;
		try {
			html = WebUtils.getContentAsString(url);
		}catch (IOException io){
			io.printStackTrace();
		}
		final Matcher m_eventvalidation = EVENTVALIDATION.matcher(html);
		String eventvalidation = "";
		if(m_eventvalidation.find())  eventvalidation = m_eventvalidation.group(1);

		final Matcher m_viewstate = VIEWSTATE.matcher(html);
		String viewstate = "";
		if(m_viewstate.find()) viewstate = m_viewstate.group(1);

		final Matcher m_submitEndnote = SUBMIT_ENDNOTE.matcher(html);
		String submitEndnote = "";
		if(m_submitEndnote.find()) submitEndnote = m_submitEndnote.group(1);

		final List<NameValuePair> postData = new ArrayList<>();

		postData.add(new BasicNameValuePair("__EVENTVALIDATION", eventvalidation));
		postData.add(new BasicNameValuePair("__VIEWSTATE", viewstate));
		// the values can be ignored
		postData.add(new BasicNameValuePair(submitEndnote + ".x", "42"));
		postData.add(new BasicNameValuePair(submitEndnote + ".y", "2"));

		return postData;

	}

	@Override
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		return RIS2BIB.toBibtex(bibtex);
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
		return URL_PATTERNS;
	}

}