package org.bibsonomy.scraper.url.kde.eric;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;

/**
 * SCraper for papers from http://www.eric.ed.gov/
 * @author tst
 * @version $Id$
 */
public class EricScraper implements Scraper, UrlScraper {
	
	private static final String INFO = "ERIC Scraper: Scraper for publications from the <a href=\"http://www.eric.ed.gov/\">Education Resources Information Center</a>. Author: KDE";
	
	private static final String ERIC_HOST = "eric.ed.gov";
	
	private static final String ERIC_URL = "http://www.eric.ed.gov/";
	
	private static final String EXPORT_BASE_URL = "http://www.eric.ed.gov/ERICWebPortal/custom/portlets/clipboard/performExport.jsp?texttype=endnote&accno=";
	
	private static final String PATTERN_ACCNO = "accno=([^&]*)";

	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		// TODO Auto-generated method stub
		
		/*
		 * example:
		 * http://www.eric.ed.gov/ERICWebPortal/Home.portal?_nfpb=true&ERICExtSearch_SearchValue_0=star&searchtype=keyword&ERICExtSearch_SearchType_0=kw&_pageLabel=RecordDetails&objectId=0900019b802f2e44&accno=EJ786532&_nfls=false
		 * accno=EJ786532
		 * 
		 * texttype=endnote
		 * 
		 */
		
		if(sc != null && sc.getUrl() != null && supportsUrl(sc.getUrl())){
			sc.setScraper(this);
			
			//extract accno from url query
			String accno = null;
			
			Pattern accnoPattern = Pattern.compile(PATTERN_ACCNO);
			Matcher accnoMatcher = accnoPattern.matcher(sc.getUrl().getQuery());
			if(accnoMatcher.find())
				accno = accnoMatcher.group(1);
			
			// build download URL
			String downloadUrl = null;
			if(accno != null)
				downloadUrl = EXPORT_BASE_URL + accno;
			
			// download ris
			try {
				
				if(downloadUrl != null){
					String ris = sc.getContentAsString(new URL(downloadUrl));
					
					// convert to bibtex
					String bibtex = null;
					RisToBibtexConverter converter = new RisToBibtexConverter();
					
					bibtex = converter.RisToBibtex(ris);
				
					if(bibtex != null){
						sc.setBibtexResult(bibtex);
						return true;
					}else
						throw new ScrapingFailureException("getting bibtex failed");
					
				}else
					throw new PageNotSupportedException("Value for accno is missing.");
				
			} catch (MalformedURLException ex) {
				throw new InternalFailureException(ex);
			}
		}
		
		return false;
	}
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + ERIC_HOST), UrlScraper.EMPTY_PATTERN));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}

}
