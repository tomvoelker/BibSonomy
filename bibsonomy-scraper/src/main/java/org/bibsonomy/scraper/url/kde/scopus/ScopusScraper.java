package org.bibsonomy.scraper.url.kde.scopus;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * Scraping Logger for access on http://www.scopus.com
 * @author tst
 * @version $Id$
 */
public class ScopusScraper extends AbstractUrlScraper {

	private static final Logger log = Logger.getLogger(ScopusScraper.class);

	private static final String INFO = "currently not available";

	private static final String HOST = "scopus.com";

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST + "$"), AbstractUrlScraper.EMPTY_PATTERN));


	public String getInfo() {
		return INFO;
	}

	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
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
		// log message
		log.debug("Observed Scraper called: ScopusScraper is called with " + sc.getUrl().toString());

		// TODO: throw exception or not?
		// throw new PageNotSupportedException("This Page is currently not supported");
		return false;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
}
