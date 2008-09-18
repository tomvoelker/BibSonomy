package org.bibsonomy.scraper.url.kde.wormbase;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.EndnoteToBibtexConverter;

/**
 * Scraper for http://www.wormbase.org
 * @author tst
 * @version $Id$
 */
public class WormbaseScraper implements Scraper {
	
	private static final String INFO = "Scraper for papers from http://www.wormbase.org";
	
	private static final String HOST = "wormbase.org";

	private static final String PATTER_NAME = "name=([^;]*);";
	
	private static final String DOWNLOAD_URL = "http://www.textpresso.org/cgi-bin/wb/exportendnote?mode=singleentry&lit=C.%20elegans&id=";
	
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(HOST)){
			sc.setScraper(this);
			
			String name = null;
			
			// get id
			Pattern patternName = Pattern.compile(PATTER_NAME);
			Matcher matcherName = patternName.matcher(sc.getUrl().toString());
			if(matcherName.find())
				name = matcherName.group(1);
			
			if(name != null){
				
				// get endnote
				try {
					String endnote = sc.getContentAsString(new URL(DOWNLOAD_URL + name));
					
					// convert bibtex
					EndnoteToBibtexConverter converter = new EndnoteToBibtexConverter();
					String bibtex = converter.processEntry(endnote);
					
					if(bibtex != null){
						sc.setBibtexResult(bibtex);
						return true;
					}else
						throw new ScrapingFailureException("generating bibtex failed");
					
				} catch (MalformedURLException ex) {
					throw new InternalFailureException(ex);
				}
				
			}else
				throw new PageNotSupportedException("no paper ID available");
		}
		return false;
	}

}
