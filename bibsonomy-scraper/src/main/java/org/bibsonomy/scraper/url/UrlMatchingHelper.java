package org.bibsonomy.scraper.url;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;

/**
 * Little helper for Uerl matching against a given Scraper. 
 * 
 * @author tst
 * @version $Id$
 */
public class UrlMatchingHelper {
	
	/**
	 * Checks if a url is supported by a scraper. Default behaviour for many scrapers.
	 * @param url
	 * @param scraper
	 * @return true, if url is supported
	 */
	public static boolean isUrlMatch(URL url, UrlScraper scraper) {
		boolean match = false;
		
		for(Tuple<Pattern, Pattern> tuple: scraper.getUrlPatterns()){
			if(tuple.getFirst() != null){
				Matcher matcher = tuple.getFirst().matcher(url.getHost());
				if(matcher.find())
					match = true;
				else
					match = false;
			}
			
			if(tuple.getSecond() != null){
				Matcher matcher = tuple.getSecond().matcher(url.getPath());
				if(matcher.find())
					match = true;
				else
					match = false;
			}

			if(match)
				break;

		}
		
		return match;
	}

}
