package org.bibsonomy.scraper.url.kde.informaworld;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.url.EndnoteToBibtexConverter;

/**
 * @author wbi
 * @version $Id$
 */
public class InformaWorldScraper implements Scraper {

	private static final String info = "Informaworld Scraper: This Scraper parses a publication from http://www.informaworld.com/ "+
	"and extracts the adequate BibTeX entry. Author: KDE";

	private static final String INFORMAWORLD_HOST_NAME  = "http://www.informaworld.com";
	private static final String INFORMAWORLD_ABSTRACT_PATH = "/smpp/content~content=";
	private static final String INFORMAWORLD_BIBTEX_PATH = "/smpp/content~db=all";
	private static final String INFORMAWORLD_BIBTEX_DOWNLOAD_PATH = "/smpp/content?file.txt&tab=citation&popup=&group=&expanded=&mode=&maction=&backurl=&citstyle=endnote&showabs=false&format=file&toemail=&subject=&fromname=&fromemail=&content={id}&selecteditems={sid}";
	
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
			if(url.startsWith(INFORMAWORLD_HOST_NAME)) {
				
				String id = null;
				
				if(url.startsWith(INFORMAWORLD_HOST_NAME + INFORMAWORLD_ABSTRACT_PATH)) {
					id = url.substring(url.indexOf("~content=") + 9, url.indexOf("~db="));
				}
				
				if(url.startsWith(INFORMAWORLD_HOST_NAME + INFORMAWORLD_BIBTEX_PATH)) {
					id = url.substring(url.indexOf("~content=") + 9, url.indexOf("~tab="));
				}
				
				String citUrl = INFORMAWORLD_HOST_NAME + (INFORMAWORLD_BIBTEX_DOWNLOAD_PATH.replace("{id}", id)).replace("{sid}", id.substring(1));
				
				try {
					sc.setUrl(new URL(citUrl));
				} catch (MalformedURLException ex) {
					ex.printStackTrace();
				}
				
				EndnoteToBibtexConverter bib = new EndnoteToBibtexConverter();
				String bibResult = bib.processEntry(sc.getPageContent());
								
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
