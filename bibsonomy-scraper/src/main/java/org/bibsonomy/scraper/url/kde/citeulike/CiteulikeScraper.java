package org.bibsonomy.scraper.url.kde.citeulike;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * Scraper for citeulike.org
 * @author tst
 * @version $Id$
 */
public class CiteulikeScraper implements Scraper {

	private static final String INFO = "CiteULike Scraper: scrapes bibtex from <a href=\"http://www.citeulike.org/\">CiteUlike</a>. Author: KDE";
	
	private static final String HOST = "citeulike.org";
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(HOST)){
			sc.setScraper(this);
			
			// build bibtex download URL
			String downloadUrl = sc.getUrl().toString();
			downloadUrl = downloadUrl.replace("citeulike.org", "citeulike.org/bibtex");
			
			// download
			String bibtex = null;
			try {
				bibtex = sc.getContentAsString(new URL(downloadUrl));
			} catch (MalformedURLException ex) {
				throw new InternalFailureException(ex);
			}
			
			// set result
			if(bibtex != null){
				sc.setBibtexResult(bibtex);
				return true;
			}else
				throw new ScrapingFailureException("getting bibtex failed");
			
		}
		return false;
	}

}
