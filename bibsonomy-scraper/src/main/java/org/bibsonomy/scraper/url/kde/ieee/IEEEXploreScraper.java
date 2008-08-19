
package org.bibsonomy.scraper.url.kde.ieee;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.CompositeScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;


public class IEEEXploreScraper extends CompositeScraper {
	private static final String info = "IEEEXplore Scraper: This scraper creates a BibTeX entry for the media at " + 
	                                    "<a href=\"http://ieeexplore.ieee.org/\">IEEEXplore</a> . Author: KDE";
	
	public IEEEXploreScraper() {
		addScraper(new IEEEXploreJournalProceedingsScraper());
		addScraper(new IEEEXploreBookScraper());
		addScraper(new IEEEXploreStandardsScraper());
	}
	
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		
		
		/* if url includes search the arnumber will be 
		* extracted and a new URL will be formed ... else use oringinal SC
		*/
		if (sc.getUrl() != null && sc.getUrl().toString().startsWith("http://ieeexplore.ieee.org/Xplore")) {
			String query = sc.getUrl().getQuery();
			if (query.indexOf("arnumber") != -1) {
				String arnumber = query.substring(query.indexOf("arnumber"));
				if (arnumber.indexOf("&") != -1){
					arnumber = arnumber.substring(arnumber.indexOf("arnumber")+9,arnumber.indexOf("&"));	
				} else {
					arnumber = arnumber.substring(arnumber.indexOf("arnumber")+9);
				}
				URL xplUrl = null;
				try {
					xplUrl = new URL(("http://ieeexplore.ieee.org/xpl/freeabs_all.jsp?arnumber=" + arnumber));
				} catch (MalformedURLException e) {
					throw new InternalFailureException(e);
				}
				sc.setUrl(xplUrl);
			}
		} 
		
		return super.scrape(sc);
	}
	
	public String getInfo() {
		return info;
	}
}