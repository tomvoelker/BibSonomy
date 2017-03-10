package org.bibsonomy.scraper.url.kde.nature;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericRISURLScraper;

/**
 * Scraper for Nature
 *
 * @author Johannes
 */
public class NatureArticleScraper extends GenericRISURLScraper{	
	
	private static final String SITE_NAME = "Nature";
	private static final String SITE_URL = "http://www.nature.com/";
	private static final String INFO = "This Scraper parses a publication from " + href(SITE_URL, SITE_NAME)+".";

	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = new LinkedList<Pair<Pattern,Pattern>>();
	static {
		URL_PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*?" + "www.nature.com"), Pattern.compile("articles/.*")));
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.UrlScraper#getSupportedSiteName()
	 */
	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.UrlScraper#getSupportedSiteURL()
	 */
	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#getInfo()
	 */
	@Override
	public String getInfo() {
		return INFO;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#getDownloadURL(java.net.URL, java.lang.String)
	 */
	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		return url.toString() + ".ris";
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#getUrlPatterns()
	 */
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}

}
