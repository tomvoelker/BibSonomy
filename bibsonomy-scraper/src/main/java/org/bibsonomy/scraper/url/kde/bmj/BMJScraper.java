package org.bibsonomy.scraper.url.kde.bmj;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * @author wbi
 * @version $Id$
 */
public class BMJScraper implements Scraper {
	
	private static final String info = "BMJ Scraper: This Scraper parses a publication from http://www.bmj.com/ "+
	"and extracts the adequate BibTeX entry. Author: KDE";

	private static final String BMJ_HOST_NAME  = "http://www.bmj.com";
	private static final String BMJ_ABSTRACT_PATH = "/cgi/content/full/";
	private static final String BMJ_BIBTEX_PATH = "/cgi/citmgr?gca=";
	private static final String BMJ_BIBTEX_DOWNLOAD_PATH = "/cgi/citmgr?type=bibtex&gca=";
	
	public String getInfo() {
		return info;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)
			throws ScrapingException {
		/*
		 * check, if URL is not NULL 
		 */
		if (sc.getUrl() != null) {
			/*
			 * extract URL and check against several (mirror) host names
			 */
			String url = sc.getUrl().toString();
			
			if(url.startsWith(BMJ_HOST_NAME)) {
				sc.setScraper(this);
				String id = null;
				
				if(url.startsWith(BMJ_HOST_NAME + BMJ_ABSTRACT_PATH)) {
					id = "bmj;" + url.substring(url.indexOf("/full/") + 6);
				}
				
				if(url.startsWith(BMJ_HOST_NAME + BMJ_BIBTEX_PATH)) {
					id = url.substring(url.indexOf(BMJ_BIBTEX_PATH) + BMJ_BIBTEX_PATH.length());
				}
				
				String bibResult = null;
				
				try {
					URL citURL = new URL(BMJ_HOST_NAME + BMJ_BIBTEX_DOWNLOAD_PATH + id);
					bibResult = sc.getContentAsString(citURL);
				} catch (MalformedURLException ex) {
					throw new InternalFailureException(ex);
				}
				
				if(bibResult != null) {
					sc.setBibtexResult(bibResult);
					return true;
				}
			}
		}
		
		return false;
	}

}
