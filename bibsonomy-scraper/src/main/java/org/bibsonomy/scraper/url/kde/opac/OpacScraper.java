package org.bibsonomy.scraper.url.kde.opac;

import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.url.PicaToBibtexConverter;

/**
 * @author C. Kramer
 * @version $Id$
 */
public class OpacScraper implements Scraper {
	private static final String info = "OPAC Scraper: This scraper parses a publication page from <a href=\"http://opac.bibliothek.uni-kassel.de/\">Bibliothek Kassel</a>  " +
	"and extracts the adequate BibTeX entry. Author: KDE";
	
	private static final Logger log = Logger.getLogger(OpacScraper.class);
	private static final String OPAC_URL ="http://opac.bibliothek.uni-kassel.de/";
	
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if (sc.getUrl() != null && (sc.getUrl().toString().startsWith(OPAC_URL))){
			String bibResult = null;
			
			try {
				// create a converter and start converting :)
				PicaToBibtexConverter converter = new PicaToBibtexConverter(sc.getPageContent(), "xml", sc.getUrl().toString());
				
				bibResult = converter.getBibResult();
				
				if(bibResult != null){
					sc.setBibtexResult(bibResult);
					sc.setScraper(this);
					return true;
				}
			} catch (Exception e){
				log.error(e + " on entry " + sc.getUrl());
			}
		}
		return false;
	}

	public String getInfo() {
		return info;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}
}
