package org.bibsonomy.scraper.url.kde.preprints;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * scraper for Preprints
 * checks for a doi and sets it as selectedText
 * then ContentNegotiationDOIScraper gets the bibtex
 *
 * @author Johannes
 */
public class PreprintsScraper extends AbstractUrlScraper{
	Log log = LogFactory.getLog(PreprintsScraper.class);
	
	private static final String SITE_NAME = "Preprints";
	private static final String SITE_HOST = "preprints.org";
	private static final String SITE_URL = "https://" + SITE_HOST;
	private static final String INFO = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME);
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(
			Pattern.compile(".*" + SITE_HOST), 
			EMPTY_PATTERN
			));
	
	private static final Pattern DOI_PATTERN = Pattern.compile("<meta name=\"citation_doi\" content=\"(.*?)\">");
	
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
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#getUrlPatterns()
	 */
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#scrapeInternal(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		try {
			/*
			 * find doi and set selectedText so ContentNegotiationDOIScraper can do its work
			 */
			String content = WebUtils.getContentAsString(scrapingContext.getUrl());
			final Matcher m = DOI_PATTERN.matcher(content);
			if (m.find()) {
				String doi = m.group(1);
				scrapingContext.setSelectedText(doi);
			}
		} catch (IOException e) {
			throw new ScrapingException(e);
		}
		
		/*
		 * always return false, such that ContentNegotiationDOIScraper gets a chance
		 */
		return false;
	}

}
