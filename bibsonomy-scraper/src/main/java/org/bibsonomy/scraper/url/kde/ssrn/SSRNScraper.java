package org.bibsonomy.scraper.url.kde.ssrn;

import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * Scraping Logger for access on http://www.ssrn.com/
 * @author tst
 * @version $Id$
 */
public class SSRNScraper implements Scraper {
	
	private static final Logger log = Logger.getLogger(SSRNScraper.class);

	private static final String INFO = "Scraper for ssrn.com";
	
	private static final String HOST = "ssrn.com";
	
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		/*
		 * See alternative code in _SSRNScraper.java
		 */
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(HOST)){
			// log message
			log.debug("Observed Scraper called: SSRNScraper is called with " + sc.getUrl().toString());
			
			// TODO: throw exception or not?
			// throw new PageNotSupportedException("This Page is currently not supported");
		}
		return false;
	}

}
