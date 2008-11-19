
package org.bibsonomy.scraper.url.kde.ieee;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.CompositeScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;


public class IEEEXploreScraper extends CompositeScraper implements UrlScraper{
	private static final String info = "IEEEXplore Scraper: This scraper creates a BibTeX entry for the media at " + 
	                                    "<a href=\"http://ieeexplore.ieee.org/\">IEEEXplore</a> . Author: KDE";
	
	private static final String HOST = "ieeexplore.ieee.org";
	private static final String XPLORE_PATH = "/Xplore";
	private static final String SEARCH_PATH = "/search/";

	public IEEEXploreScraper() {
		addScraper(new IEEEXploreJournalProceedingsScraper());
		addScraper(new IEEEXploreBookScraper());
		addScraper(new IEEEXploreStandardsScraper());
	}
	
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		
		
		/* if url includes search the arnumber will be 
		* extracted and a new URL will be formed ... else use oringinal SC
		*/
		if (sc != null && sc.getUrl() != null && supportsUrl(sc.getUrl())) {
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
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), Pattern.compile(XPLORE_PATH + ".*")));
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), Pattern.compile(SEARCH_PATH + ".*")));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}
}