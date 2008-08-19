package org.bibsonomy.scraper.url.kde.pion;

import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * Scraping Logger for access on http://pion.co.uk/
 * @author tst
 * @version $Id$
 */
public class PionScraper implements Scraper {
	
	private static final Logger log = Logger.getLogger(PionScraper.class);

	private static final String INFO = "Scraper for http://pion.co.uk/";
	
	private static final String HOST = "pion.co.uk";
	
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(HOST)){
			// log message
			log.debug("Observed Scraper called: PionScraper is called with " + sc.getUrl().toString());
			
			// TODO: throw exception or not?
			// throw new PageNotSupportedException("This Page is currently not supported");
		}
		return false;
	}

}
