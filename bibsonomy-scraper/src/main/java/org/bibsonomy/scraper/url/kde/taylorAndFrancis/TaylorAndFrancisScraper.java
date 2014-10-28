/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
	
	private final static Pattern ref_pattern = Pattern.compile("(?s)<ul class=\"references\">(.*)</ul></div></div>");
	private static PostMethod postContent(PostMethod method, String doi) {
		method.addParameter("doi", doi);
		method.addParameter("downloadFileName", DOWNLOADFILENAME);
		method.addParameter("format", "bibtex");
		method.addParameter("direct", "true");
		method.addParameter("include", "includeAbs");
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
		Matcher matcher = DOI_PATTERN.matcher(scrapingContext.getUrl().toString());
		if (!matcher.find()) throw new ScrapingException("URL pattern not supported yet");
		try {
			HttpClient client = WebUtils.getHttpClient();
			//get the page to start the session
			WebUtils.getContentAsString(client, scrapingContext.getUrl().toExternalForm());
			//post to receive the BibTeX file
			PostMethod method = new PostMethod(SITE_URL + TANDF_BIBTEX_DOWNLOAD_PATH);
			String doi = matcher.group().substring(1);
			String bibtexEntry = WebUtils.getPostContentAsString(client, postContent(method, doi));
			if (present(bibtexEntry)) {
				scrapingContext.setBibtexResult(bibtexEntry.trim());
				return true;
			} else {
				throw new ScrapingFailureException("getting BibTeX failed");
			}
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
			final Matcher m = ref_pattern.matcher(WebUtils.getContentAsString(scrapingContext.getUrl().toString().replace("abs", "ref") + "#tabModule"));
			String references = null;
			if (m.find()) {
				references = m.group(1);
			}
			
			if (references != null) {
				scrapingContext.setReferences(references);
				return true;
			}
		} catch (IOException ex) {
			throw new ScrapingException(ex);
		}
		
		return false;
	}

}
