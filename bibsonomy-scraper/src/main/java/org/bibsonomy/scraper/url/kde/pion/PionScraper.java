package org.bibsonomy.scraper.url.kde.pion;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * Scraping Logger for access on http://pion.co.uk/
 * @author tst
 * @version $Id$
 */
public class PionScraper extends UrlScraper {

	private static final Logger log = Logger.getLogger(PionScraper.class);

	private static final String INFO = "Pion Scraper: Scraper for publications from " + href("http://pion.co.uk/", "Pion");

	private static final String HOST = "pion.co.uk";

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), UrlScraper.EMPTY_PATTERN));
	
	public String getInfo() {
		return INFO;
	}

	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		// log message
		log.debug("Observed Scraper called: PionScraper is called with " + sc.getUrl().toString());

		// TODO: throw exception or not?
		// throw new PageNotSupportedException("This Page is currently not supported");
		return false;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		
		return patterns;
	}
}
