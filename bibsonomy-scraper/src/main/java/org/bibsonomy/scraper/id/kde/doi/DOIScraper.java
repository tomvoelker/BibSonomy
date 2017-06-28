/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.id.kde.doi;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.DOIUtils;

/**
 * Checks, if the URL or the selection in the {@link ScrapingContext} points to 
 * dx.doi.org OR is a DOI and if so, follows the redirect to get the "real" 
 * URL which is then passed to the next scrapers.
 * 
 * Should be one of the first scrapers in the chain!
 * 
 * @author rja
 * @author tst
 */
public class DOIScraper implements Scraper {
	private static final Log log = LogFactory.getLog(DOIScraper.class);
	
	private static final String SITE_NAME = "DOIScraper";
	private static final String INFO 	= "Scraper which follows redirects from " + AbstractUrlScraper.href(DOIUtils.DX_DOI_ORG_URL, DOIUtils.DX_DOI_ORG) + 
											" and passes the resulting URLs to the following scrapers. Additionally checks, if the given selection" +
											" text contains (almost only!) a DOI and basically does the same.";
	
	@Override
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper>singletonList(this);
	}

	/**
	 * First, checks the URL for dx.doi.org ... if contained, follows the redirect and
	 * exchanges the URL in the scraping context such that the following scrapers
	 * can check the "real" URL.
	 * 
	 * Second, if no matching URL found, but selection found which contains (almost only!) 
	 * a DOI, follows the redirects to the final URL and exchanges the URL in the context
	 * with it.
	 * 
	 * <p>NOTE: always returns false, such that the other scrapers have a chance :-)</p>
	 * 
	 * 
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#scrapeInternal(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrape(ScrapingContext scrapingContext) throws ScrapingException {
		/*
		 * first: check URL
		 */
		final URL url = scrapingContext.getUrl();
		final String selection = scrapingContext.getSelectedText();
		if (!present(selection) && DOIUtils.isDOIURL(url)) {
			//save the initial DOI URL for possible use in {@link ContentNegotiationDOIScraper}
			scrapingContext.setDoiURL(url);
			/*
			 * dx.doi.org URL found! --> resolve redirects
			 */
			final URL redirectUrl = WebUtils.getRedirectUrl(url);
			if (present(redirectUrl)) {
				scrapingContext.setUrl(redirectUrl);
			}
			
			/*
			 * remove text selection
			 */
			scrapingContext.setSelectedText(null);
		} else
			try {
				if (DOIUtils.isSupportedSelection(selection) || DOIUtils.isDOIURL(new URL(scrapingContext.getSelectedText()))){
					/*
					 * selection contains a DOI -> extract it
					 */
					final String doi = DOIUtils.extractDOI(selection);
					//save the initial DOI URL for possible use in {@link ContentNegotiationDOIScraper}
					try {
						scrapingContext.setDoiURL(DOIUtils.getURL(doi));
					} catch (MalformedURLException ex) {
						//scrape with other scrapers (without use of {@link ContentNegotiationDOIScraper}
						scrapingContext.setDoiURL(null);
					}
					
					final URL redirectUrl = DOIUtils.getUrlForDoi(doi);
					if (present(redirectUrl)) {
						scrapingContext.setUrl(redirectUrl);
					}
					
					/*
					 * remove text selection
					 */
					scrapingContext.setSelectedText(null);
				}
			} catch (MalformedURLException e) {
				log.info("selected text is not a url", e);
			}
		/*
		 * always return false, such that the "real" scrapers can do their work
		 */
		return false;
	}
	
	@Override
	public boolean supportsScrapingContext(ScrapingContext scrapingContext) {
		try {
			return DOIUtils.isDOIURL(scrapingContext.getUrl()) || DOIUtils.isSupportedSelection(scrapingContext.getSelectedText()) || DOIUtils.isDOIURL(new URL(scrapingContext.getSelectedText()));
		} catch (MalformedURLException e) {
			log.info("selected text is not a url", e);
		}
		return false;
	}
	
	@Override
	public String getInfo() {
		return INFO;
	}
	
	/**
	 * @return site name
	 */
	public String getSupportedSiteName(){
		return SITE_NAME;
	}
	
	
	/**
	 * @return site url
	 */
	public String getSupportedSiteURL(){
		return null;
	}

}
