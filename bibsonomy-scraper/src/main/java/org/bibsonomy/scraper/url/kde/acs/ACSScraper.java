/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.acs;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class ACSScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "ACS";
	private static final String SITE_URL = "http://www.acs.org/";
	private static final String info = "This Scraper parses a publication from " + href(SITE_URL, SITE_NAME)+".";

	private static final String ACS_HOST_NAME  = "http://pubs.acs.org";
	private static final String DOI_PATH = "doi/(abs|pdf|full|pdfplus)/";
	private static final String ACS_PATH = "/" + DOI_PATH;
	private static final String ACS_BIBTEX_PATH = "/action/downloadCitation";
	private static final String ACS_BIBTEX_PARAMS = "?include=abs&format=bibtex&doi=";

	private static Pattern PATTERN_GETTING_DOI_PATH = Pattern.compile(DOI_PATH + "([^\\?]*)");
	private static Pattern PATTERN_GETTING_DOI_QUERY = Pattern.compile("doi=([^\\&]*)");
	
	private static final Pattern pathPatternAbstract = Pattern.compile(ACS_PATH + ".*");
	private static final Pattern pathPatternBibtex = Pattern.compile(ACS_BIBTEX_PATH + ".*");
	
	private static final List<Pair<Pattern,Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();
	private static final Pattern URL_PATTERN_FOR_URL = Pattern.compile("URL = \\{ \n        (.*)\n    \n\\}");
	
	static {
		final Pattern hostPattern = Pattern.compile(".*" + "pubs.acs.org");
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, pathPatternBibtex));
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, pathPatternAbstract));
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		final URL citationURL = sc.getUrl();

		String bibResult = null;

		/*
		 * http://pubs.acs.org/action/downloadCitation?doi=10.1021%2Fci049894n&include=abs&format=bibtex
		 * 
		 * Cookie: JSESSIONID=yyCNJ10bJFpTNTysSn2nNzxq1HdTRYky5ZK1gqJn19vhMvy3FkQv!-1004683069; SERVER=172.25.11.116:16092; pubs=OVWPXNS172.25.1.54CKKLW; appsrv=OVWPXNS172.23.10.162CKMLK; I2KBRCK=1; I2KBRCK=1; REQUESTIP=172.25.0.60
		 */
		try {
			// get doi from url
			String doi = null;
			Matcher matcherPath = PATTERN_GETTING_DOI_PATH.matcher(citationURL.toString());
			if (matcherPath.find()) {
				doi = matcherPath.group(2);
			} else{
				Matcher matcherQuery = PATTERN_GETTING_DOI_QUERY.matcher(citationURL.toString());
				if (matcherQuery.find()) {
					doi = matcherQuery.group(1);
				}
			}
			
			if (doi != null){
				final String cookie = WebUtils.getCookies(citationURL);
				bibResult = WebUtils.getPostContentAsString(cookie, new URL(ACS_HOST_NAME + ACS_BIBTEX_PATH + ACS_BIBTEX_PARAMS + doi), doi);
			}
			
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}

		/*
		 * clean the bibtex for better format
		 */
		if (bibResult != null) {
			Matcher m = URL_PATTERN_FOR_URL.matcher(bibResult);
			if(m.find()) {
				bibResult = bibResult.replaceAll(URL_PATTERN_FOR_URL.toString(), "URL = {" + m.group(1) + "}");
			}
			sc.setBibtexResult(bibResult);
			return true;
		}
		throw new ScrapingFailureException("getting bibtex failed");
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	
	@Override
	public String getSupportedSiteName() {
		return SITE_URL;
	}

	@Override
	public String getSupportedSiteURL() {
		return ACS_HOST_NAME;
	}
}
