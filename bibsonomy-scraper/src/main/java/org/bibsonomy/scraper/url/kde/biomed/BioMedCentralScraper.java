package org.bibsonomy.scraper.url.kde.biomed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * @author wbi
 * @version $Id$
 */
public class BioMedCentralScraper implements Scraper {

	private static final String info = "BioMed Central Scraper: This Scraper parses a publication from http://www.biomedcentral.com/ "+
	"and extracts the adequate BibTeX entry. Author: KDE";

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
		if (sc.getUrl() != null) {
			/*
			 * extract URL and check against several (mirror) host names
			 */
			String url = sc.getUrl().toString();
			if(url.startsWith(BIOMEDCENTRAL_HOST_NAME)) {
				
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
