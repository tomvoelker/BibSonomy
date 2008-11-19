package org.bibsonomy.scraper.url.kde.ssrn;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * Scraping Logger for access on http://www.ssrn.com/
 * @author tst
 * @version $Id$
 */
public class SSRNScraper extends UrlScraper {

	private static final Logger log = Logger.getLogger(SSRNScraper.class);

	private static final String INFO = "currently not available";

	private static final String HOST = "ssrn.com";
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), UrlScraper.EMPTY_PATTERN));
	
	public String getInfo() {
		return INFO;
	}

	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		/*
		 * See alternative code in _SSRNScraper.java
		 */
		// log message
		log.debug("Observed Scraper called: SSRNScraper is called with " + sc.getUrl().toString());

		// TODO: throw exception or not?
		// throw new PageNotSupportedException("This Page is currently not supported");
		return false;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
