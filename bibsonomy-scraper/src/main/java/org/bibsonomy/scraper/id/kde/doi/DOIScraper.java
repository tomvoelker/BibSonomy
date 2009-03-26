package org.bibsonomy.scraper.id.kde.doi;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.ValidationUtils;
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
 * @version $Id$
 */
public class DOIScraper implements Scraper {

	public String getInfo() {
		return "Scraper which follows redirects from " + AbstractUrlScraper.href(DOIUtils.DX_DOI_ORG_URL, DOIUtils.DX_DOI_ORG) + 
		" and passes the resulting URLs to the following scrapers. Additionally checks, if the given selection" +
		" text contains (almost only!) a DOI and basically does the same.";
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
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
	public boolean scrape(ScrapingContext scrapingContext) throws ScrapingException {
		/*
		 * first: check URL
		 */
		final URL url = scrapingContext.getUrl();
		final String selection = scrapingContext.getSelectedText();
		if (!ValidationUtils.present(selection) && DOIUtils.isDOIURL(url)) {
			/*
			 * dx.doi.org URL found! --> resolve redirects
			 */
			final URL redirectUrl = WebUtils.getRedirectUrl(url);
			if (ValidationUtils.present(redirectUrl)) {
				scrapingContext.setUrl(redirectUrl);
			}
		} else if (isSupportedSelection(selection)) {
			/*
			 * selection contains a DOI -> extract it
			 */
			final String doi = DOIUtils.extractDOI(selection);
			final URL redirectUrl = WebUtils.getRedirectUrl(DOIUtils.getUrlForDoi(doi));
			if (ValidationUtils.present(redirectUrl)) {
				scrapingContext.setUrl(redirectUrl);
			}
		}
		/*
		 * always return false, such that the "real" scrapers can do their work
		 */
		return false;
	}

	
	/**
	 * Checks, whether the selection contains a DOI and is not too long (i.e., 
	 * hopefully only contains the DOI and nothing else. 
	 * 
	 * @param selection
	 * @return
	 */
	private static boolean isSupportedSelection(final String selection) {
		return selection != null && selection.length() < 10 && DOIUtils.containsOnlyDOI(selection);
	}

	public boolean supportsScrapingContext(ScrapingContext scrapingContext) {
		return DOIUtils.isDOIURL(scrapingContext.getUrl()) || isSupportedSelection(scrapingContext.getSelectedText());
	}


}
