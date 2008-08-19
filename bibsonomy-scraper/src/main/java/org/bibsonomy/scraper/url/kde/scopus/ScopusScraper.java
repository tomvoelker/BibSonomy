package org.bibsonomy.scraper.url.kde.scopus;

import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * Scraping Logger for access on http://www.scopus.com
 * @author tst
 * @version $Id$
 */
public class ScopusScraper implements Scraper {
	
	private static final Logger log = Logger.getLogger(ScopusScraper.class);
	
	private static final String INFO = "Scraper for scopus.com";

	private static final String HOST = "scopus.com";
	
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		/*
		 * Needs login to access the download area.
		 * 
		 * Two ids are needed for download: stateKey and eid. Both can be
		 * extracted from the download page. Other hidden values from the 
		 * form are: origin, sid, src, sort
		 * Download path: /scopus/citation/export.url
		 * Important exportFormat (radio select) is "RIS"
		 * Last input field is view an recommended value is "CiteOnly"
		 * 
		 */
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(HOST)){
			// log message
			log.debug("Observed Scraper called: ScopusScraper is called with " + sc.getUrl().toString());
			
			// TODO: throw exception or not?
			// throw new PageNotSupportedException("This Page is currently not supported");
		}
		return false;
	}

}
