package org.bibsonomy.scraper.url.kde.editlib;

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
public class EditLibScraper implements Scraper {

	private static final String info = "Ed/ITLib Scraper: This Scraper parses a publication from http://www.bmj.com/ "+
	"and extracts the adequate BibTeX entry. Author: KDE";

	private static final String EDITLIB_HOST_NAME  = "http://www.editlib.org";
	private static final String EDITLIB_ABSTRACT_PATH = "/index.cfm?fuseaction=Reader.ViewAbstract&paper_id=";
	private static final String EDITLIB_BIBTEX_PATH = "/index.cfm?fuseaction=Reader.ChooseCitationFormat&paper_id=";
	private static final String EDITLIB_BIBTEX_DOWNLOAD_PATH = "/index.cfm/files/citation_{id}.bib?fuseaction=Reader.ExportAbstract&format=BibTex&paper_id=";
	
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
			
			if(url.startsWith(EDITLIB_HOST_NAME)) {
				String id = null;
				
				if(url.startsWith(EDITLIB_HOST_NAME + EDITLIB_ABSTRACT_PATH)) {
					id = url.substring(url.indexOf(EDITLIB_ABSTRACT_PATH) + EDITLIB_ABSTRACT_PATH.length());
				}
				
				if(url.startsWith(EDITLIB_HOST_NAME + EDITLIB_BIBTEX_PATH)) {
					id = url.substring(url.indexOf(EDITLIB_BIBTEX_PATH) + EDITLIB_BIBTEX_PATH.length());
				}
				
				String bibResult = null;
				
				try {
					URL citURL = new URL(EDITLIB_HOST_NAME + EDITLIB_BIBTEX_DOWNLOAD_PATH.replace("{id}", id) + id);
					bibResult = sc.getContentAsString(citURL);
				} catch (MalformedURLException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				
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
