package org.bibsonomy.scraper.url.kde.karlsruhe;

import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * Scraper for liinwww.ira.uka.de/bibliography
 * @author tst
 * @version $Id$
 */
public class BibliographyScraper implements Scraper {

	private static final String INFO = "scrapes BibTeX refrences from liinwww.ira.uka.de (with path /cgi-bin/bibshow)";
	
	private static final String HOST = "liinwww.ira.uka.de";
	private static final String PATH = "/cgi-bin/bibshow";
	
	private static final String BIBTEX_START_BLOCK = "<pre class=\"bibtex\">";
	private static final String BIBTEX_END_BLOCK = "</pre>";
	
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(HOST) && sc.getUrl().getPath().startsWith(PATH)){
			sc.setScraper(this);
			
			String page = sc.getPageContent();
			
			if(page.indexOf(BIBTEX_START_BLOCK) > -1){
				// cut off first part
				page = page.substring(page.indexOf(BIBTEX_START_BLOCK)+20);
				
				// cut off end
				page = page.substring(0, page.indexOf(BIBTEX_END_BLOCK));
				
				// clean up - links and span
				page = page.replaceAll("<[^>]*>", "");
				
				sc.setBibtexResult(page);
				return true;
			}else
				throw new ScrapingException("Can't find bibtex in scraped page.");			

		}
		return false;
	}

}
