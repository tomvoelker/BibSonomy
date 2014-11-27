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
package org.bibsonomy.scraper.url.kde.cambridge;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 */
public class CambridgeScraper extends AbstractUrlScraper {
	
	private static final String SITE_NAME = "Cambridge Journals";
	private static final String CAMBRIDGE_HOST_NAME  = "http://journals.cambridge.org";
	private static final String SITE_URL = CAMBRIDGE_HOST_NAME+"/";
	private static final String info = "This Scraper parses a journal from " + href(SITE_URL, SITE_NAME)+".";

	private static final String CAMBRIDGE_HOST  = "journals.cambridge.org";
	private static final String CAMBRIDGE_ABSTRACT_PATH = "/action/displayAbstract";
	private static final String CAMBRIDGE_BIBTEX_DOWNLOAD_PATH = "/action/exportCitation?org.apache.struts.taglib.html.TOKEN=51cf342977f2aaa784c6ddfa66c3572c&emailid=&Download=Download&displayAbstract=No&format=BibTex&componentIds=";

	private static final Pattern idPattern = Pattern.compile("aid=([^&]*)");

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + CAMBRIDGE_HOST), Pattern.compile(CAMBRIDGE_ABSTRACT_PATH + ".*")));
	
	public String getInfo() {
		return info;
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		String url = sc.getUrl().toString();
		
		//get a client for cookie management
		HttpClient client = WebUtils.getHttpClient();

		String id = null;
		HttpURL citUrl = null;
		if(url.startsWith(CAMBRIDGE_HOST_NAME + CAMBRIDGE_ABSTRACT_PATH)) {
			final Matcher idMatcher = idPattern.matcher(url);
			if(idMatcher.find())
				id = idMatcher.group(1);
			else
				throw new ScrapingFailureException("No aid found.");

			try {
				citUrl = new HttpURL(CAMBRIDGE_HOST_NAME + CAMBRIDGE_BIBTEX_DOWNLOAD_PATH + id);
			} catch (URIException ex) {
				throw new InternalFailureException(ex);
			}
		}

		String bibResult = null;
		try {
			bibResult = WebUtils.getContentAsString(client, citUrl);
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}

		if(bibResult != null) {
			sc.setBibtexResult(bibResult);
			return true;
		}else
			throw new ScrapingFailureException("getting bibtex failed");
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
