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
package org.bibsonomy.scraper.url.kde.hindawi;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.CitedbyScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.generic.GenericEndnoteURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * @author Haile
 */
public class HindawiScraper extends GenericEndnoteURLScraper implements ReferencesScraper, CitedbyScraper{
	private static final Log log = LogFactory.getLog(HindawiScraper.class);
	
	private static final String SITE_NAME = "Hindawi Publishing Corporation";
	private static final String SITE_URL = "http://hindawi.com";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "hindawi.com"), AbstractUrlScraper.EMPTY_PATTERN));
	private static final String ENDNOTE_URL = "http://files.hindawi.com/journals/";
	private static final Pattern ID_PATTERN = Pattern.compile(".*/journals/(.*\\d+)");
	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("<meta name=\"citation_abstract\" content=\"(.*)\"/>");
	private static final Pattern REFERENCES_PATTERN = Pattern.compile("<h4>Linked References</h4>\\s+<ol>\\s+(.*)</ol>");
	
	private static final int ID_GROUP = 1;
	
	
	/**
	 * extracts publication id from url
	 * 
	 * @param url
	 * @return publication id
	 */
	private static String extractId(final String url) {
		final Matcher matcher = ID_PATTERN.matcher(url);
		if (matcher.find()) {
			return matcher.group(ID_GROUP);
		}
		return null;
	}
	
	private static String abstractParser(URL url){
		try{
			Matcher m = ABSTRACT_PATTERN.matcher(WebUtils.getContentAsString(url));
			if(m.find()) {
				return m.group(1);
			}
		} catch (Exception e) {
			log.error("error getting abstract for " + url, e);
		}
		return null;
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

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.PostprocessingGenericURLScraper#postProcessScrapingResult(org.bibsonomy.scraper.ScrapingContext, java.lang.String)
	 */
	@Override
	protected String postProcessScrapingResult(ScrapingContext sc, String result) {
		return BibTexUtils.addFieldIfNotContained(result,"abstract",abstractParser(sc.getUrl()));
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.SimpleGenericURLScraper#getBibTeXURL(java.net.URL)
	 */
	@Override
	public String getDownloadURL(URL url, String cookies) throws ScrapingException {
		final String id = extractId(url.toString());
		if (!present(id)) {
			throw new ScrapingFailureException("can't extract publication id for " + url);
		}
		return ENDNOTE_URL + id + ".enw";
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.CitedbyScraper#scrapeCitedby(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeCitedby(ScrapingContext scrapingContext) throws ScrapingException {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext scrapingContext) throws ScrapingException {
		final URL urlToScrape = scrapingContext.getUrl();
		final String urlPath = urlToScrape.getPath();
		final String path = urlPath.replace(urlPath.split("/")[5],"ref");
		final String url = "http://" + urlToScrape.getHost().toString() + "/" + path;
		try {
			final String referencespage = WebUtils.getContentAsString(url);
			String references = "";
			
			Matcher m = REFERENCES_PATTERN.matcher(referencespage);
			if (m.find()) {
				references = m.group();
			}
			if (present(references)) {
				scrapingContext.setReferences(references);
				return true;
			}
		} catch(Exception e) {
			log.error("error while scraping references " + urlToScrape, e);
		}
		return false;
	}
	
}
