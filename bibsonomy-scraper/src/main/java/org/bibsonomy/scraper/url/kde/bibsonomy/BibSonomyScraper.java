package org.bibsonomy.scraper.url.kde.bibsonomy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.ScrapingException;


/**
 * Scraper for single publications from bibsonomy.org.
 * 
 * @author tst
 *
 */
public class BibSonomyScraper implements Scraper {
	
	private static final String INFO = "BibSonomyScraper: If you don't like the copy button from bibsonomy use your postBibtex bookmark.";
	
	private static final String BIBSONOMY_HOST = "bibsonomy.org";
	private static final String BIBSONOMY_BIB_PATH = "/bib/bibtex";
	private static final String BIBSONOMY_BIBTEX_PATH = "/bibtex";
	
	/**
	 * Scrapes only single publications from bibsonomy.org/bibtex and bibsonomy.org/bib/bibtex 
	 */
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(BIBSONOMY_HOST)){
			try {
				String bibResult = null;
				
				// if /bibtex page then change path from /bibtex to /bib/bibtex and download
				if(sc.getUrl().getPath().startsWith(BIBSONOMY_BIBTEX_PATH)){
					String url = sc.getUrl().toString();
					url = url.replace(BIBSONOMY_BIBTEX_PATH, BIBSONOMY_BIB_PATH);
					bibResult = sc.getContentAsString(new URL(url));
					
				// if /bib/bibtex page then download directly
				}else if(sc.getUrl().getPath().startsWith(BIBSONOMY_BIB_PATH)){
					bibResult = sc.getPageContent();
				}
				
				if(bibResult != null){
					sc.setBibtexResult(bibResult);
					sc.setScraper(this);
					return true;
				}
			} catch (MalformedURLException e) {
				throw new ScrapingException(e);
			}
		}
		return false;
	}

	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

}
