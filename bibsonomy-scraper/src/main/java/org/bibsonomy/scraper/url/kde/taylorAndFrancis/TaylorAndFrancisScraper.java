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
package org.bibsonomy.scraper.url.kde.taylorAndFrancis;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author schwass
 */
public class TaylorAndFrancisScraper extends AbstractUrlScraper implements ReferencesScraper {

	private static final String SITE_NAME = "Taylor & Francis Online";
	private static final String SITE_URL = "http://www.tandfonline.com/";
	private static final String INFO = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME)+".";

	private static final String TANDF_HOST_NAME = "tandfonline.com";
	
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + TANDF_HOST_NAME), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern DOI_PATTERN = Pattern.compile("/10\\.1080/\\d++(\\.\\d++)*");
	
	private static final String TANDF_BIBTEX_DOWNLOAD_PATH = "action/downloadCitation";
	private static final String DOWNLOADFILENAME = "tandf_rajp2080_124";
	
	private final Pattern URL_PATTERN_FOR_URL = Pattern.compile("URL = \\{ \n        (.*)\n    \n\\}");
	
	private final static Pattern REF_PATTERN = Pattern.compile("(?s)<ul class=\"references\">(.*)</ul></div></div>");
	private static PostMethod postContent(PostMethod method, String doi) {
		method.addParameter("doi", doi);
		method.addParameter("downloadFileName", DOWNLOADFILENAME);
		method.addParameter("format", "bibtex");
		method.addParameter("direct", "true");
		method.addParameter("include", "abs");
		return method;
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

	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		final Matcher matcher = DOI_PATTERN.matcher(scrapingContext.getUrl().toString());
		if (!matcher.find()) throw new ScrapingException("URL pattern not supported yet");
		try {
			final HttpClient client = WebUtils.getHttpClient();
			//get the page to start the session
			WebUtils.getContentAsString(client, scrapingContext.getUrl().toExternalForm());
			//post to receive the BibTeX file
			final PostMethod method = new PostMethod(SITE_URL + TANDF_BIBTEX_DOWNLOAD_PATH);
			final String doi = matcher.group().substring(1);
			String bibtexEntry = WebUtils.getPostContentAsString(client, postContent(method, doi));
			if (present(bibtexEntry)) {
				/*
				* clean the bibtex for better format
				*/
				Matcher m = URL_PATTERN_FOR_URL.matcher(bibtexEntry);
				if(m.find()) {
					bibtexEntry = bibtexEntry.replaceAll(URL_PATTERN_FOR_URL.toString(), "URL = {" + m.group(1) + "}");
				}

				scrapingContext.setBibtexResult(bibtexEntry.trim());
				return true;
			}
			throw new ScrapingFailureException("getting BibTeX failed");
		} catch (IOException ex) {
			throw new ScrapingException(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext scrapingContext) throws ScrapingException {
		try{
			final Matcher m = REF_PATTERN.matcher(WebUtils.getContentAsString(scrapingContext.getUrl().toString().replace("abs", "ref") + "#tabModule"));
			if (m.find()) {
				scrapingContext.setReferences(m.group(1));
				return true;
			}
		} catch (IOException ex) {
			throw new ScrapingException(ex);
		}
		
		return false;
	}

}
