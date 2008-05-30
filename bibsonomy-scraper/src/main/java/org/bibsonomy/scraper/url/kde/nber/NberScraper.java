package org.bibsonomy.scraper.url.kde.nber;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * @author wbi
 * @version $Id$
 */
public class NberScraper implements Scraper {

	private static final String info = "Nber Scraper: This Scraper parses a publication from http://www.nber.org/ "+
	"and extracts the adequate BibTeX entry. Author: KDE";

	private static final String NBER_HOST_NAME  = "http://www.nber.org";
	private static final String NBER_BIBTEX_PATH = "/papers/";
	
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
			if(url.startsWith(NBER_HOST_NAME)) {
				
				//here we just need to append .bib to the url and we got the bibtex file
				try {
					sc.setUrl(new URL(url + ".bib"));
				} catch (MalformedURLException ex) {
					ex.printStackTrace();
				}
				
				String bibResult = sc.getPageContent();
								
				if(bibResult != null) {
					sc.setBibtexResult(bibResult);
					/*
					 * returns itself to know, which scraper scraped this
					 */
					sc.setScraper(this);
	
					return true;
				}
			}
		}
		return false;
	}

}
