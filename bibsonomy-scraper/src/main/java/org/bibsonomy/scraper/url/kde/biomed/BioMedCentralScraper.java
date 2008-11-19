package org.bibsonomy.scraper.url.kde.biomed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
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
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;

/**
 * @author wbi
 * @version $Id$
 */
public class BioMedCentralScraper implements Scraper, UrlScraper {

	private static final String info = "BioMed Central Scraper: This Scraper parse a publication from <a herf=\"http://www.biomedcentral.com/\">BioMed Central</a>. "+
	"and extracts the adequate BibTeX entry. Author: KDE";

	private static final String BIOMEDCENTRAL_HOST  = "biomedcentral.com";
	private static final String BIOMEDCENTRAL_HOST_NAME  = "http://www.biomedcentral.com";
	private static final String BIOMEDCENTRAL_BIBTEX_PATH = "citation";
	private static final String BIOMEDCENTRAL_BIBTEX_PARAMS = "?format=bibtex&include=cit&direct=0&action=submit";
	
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
		if (sc != null && sc.getUrl() != null && supportsUrl(sc.getUrl())) {
				sc.setScraper(this);
				
				String url = sc.getUrl().toString();
				
				if(!(url.endsWith("/" + BIOMEDCENTRAL_BIBTEX_PATH + "/") || 
					 url.endsWith("/" + BIOMEDCENTRAL_BIBTEX_PATH) ||
					 url.endsWith(BIOMEDCENTRAL_BIBTEX_PATH))) {
					
					if(!url.endsWith("/")) {
						url += "/" + BIOMEDCENTRAL_BIBTEX_PATH;
					} else {
						url += BIOMEDCENTRAL_BIBTEX_PATH;
					}
				}
				
				if(!url.endsWith("/")) {
					url += "/" + BIOMEDCENTRAL_BIBTEX_PARAMS;
				} else {
					url += BIOMEDCENTRAL_BIBTEX_PARAMS;
				}			
				
				try {
					sc.setUrl(new URL(url));
				} catch (MalformedURLException ex) {
					throw new InternalFailureException(ex);
				}
				String bibResult = sc.getPageContent();
								
				if(bibResult != null) {
					sc.setBibtexResult(bibResult);
					return true;
				}
		}
		return false;
	}
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + BIOMEDCENTRAL_HOST), UrlScraper.EMPTY_PATTERN));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}
	
}
