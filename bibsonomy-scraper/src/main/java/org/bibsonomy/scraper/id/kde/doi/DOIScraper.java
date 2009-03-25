package org.bibsonomy.scraper.id.kde.doi;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.WebUtils;

/**
 * Checks, if the URL in the {@link ScrapingContext} points to 
 * dx.doi.org and if so, follows the redirect to get the "real" 
 * URL which is then passed to the next scrapers.
 * 
 * Should be one of the first scrapers in the chain!
 * 
 * @author rja
 * @author tst
 * @version $Id$
 */
public class DOIScraper extends AbstractUrlScraper {

	private static final String DX_DOI_ORG = "dx.doi.org";
	private static final String DX_DOI_ORG_URL = "http://" + DX_DOI_ORG + "/";
	private static final List<Tuple<Pattern, Pattern>> urlPatterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + DX_DOI_ORG), EMPTY_PATTERN)); 

	/**
	 * Resolves DOI to a URL
	 * @param doi DOI as String
	 * @return URL from the referenced DOI resource, null if resolve failed
	 * @throws IOException
	 */
	public static URL getUrlForDoi(final String doi) throws IOException {
		return WebUtils.getRedirectUrl(new URL(DX_DOI_ORG_URL + doi));
	}

	@Override
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return urlPatterns;
	}

	/**
	 * Checks the URL for dx.doi.org ... if contained, follows the redirect and
	 * exchanges the URL in the scraping context such that the following scrapers
	 * can check the "real" URL.
	 * 
	 * <p>NOTE: always returns false, such that the other scrapers have a chance :-)</p>
	 * 
	 * 
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#scrapeInternal(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		final URL redirectUrl = WebUtils.getRedirectUrl(scrapingContext.getUrl());
		if (ValidationUtils.present(redirectUrl)) {
			scrapingContext.setUrl(redirectUrl);
		}
		return false;
	}

	public String getInfo() {
		return "Scraper which follows redirects from " + href(DX_DOI_ORG_URL, DX_DOI_ORG) + " and passes the resulting URLs to the following scrapers.";
	}


}
