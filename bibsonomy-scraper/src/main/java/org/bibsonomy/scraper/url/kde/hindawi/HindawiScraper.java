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
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.CitedbyScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.generic.GenericRISURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * @author Haile
 */
public class HindawiScraper extends GenericRISURLScraper implements ReferencesScraper, CitedbyScraper{
	private static final Log log = LogFactory.getLog(HindawiScraper.class);
	
	private static final String SITE_NAME = "Hindawi Publishing Corporation";
	private static final String SITE_URL = "http://hindawi.com";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<>(Pattern.compile(".*" + "hindawi.com"), AbstractUrlScraper.EMPTY_PATTERN));
	private static final String RIS_URL = "http://hindawi.com/journals/";
	private static final Pattern ID_PATTERN = Pattern.compile(".*/journals/(.*\\d+)");
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
	 * @see org.bibsonomy.scraper.generic.SimpleGenericURLScraper#getBibTeXURL(java.net.URL)
	 */
	@Override
	public String getDownloadURL(URL url, String cookies) throws ScrapingException {
		final String id = extractId(url.toString());
		if (!present(id)) {
			throw new ScrapingFailureException("can't extract publication id for " + url);
		}
		return RIS_URL + id + ".ris";
	}

	@Override
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		return bibtex.replaceAll("<.+?>", "");
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
