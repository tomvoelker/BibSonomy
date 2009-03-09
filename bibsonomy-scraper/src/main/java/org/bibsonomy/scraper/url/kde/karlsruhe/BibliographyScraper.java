package org.bibsonomy.scraper.url.kde.karlsruhe;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * Scraper for liinwww.ira.uka.de/bibliography
 * @author tst
 * @version $Id$
 */
public class BibliographyScraper extends AbstractUrlScraper {

	private static final String INFO = "LIIN Scraper: Scrapes BibTeX refrences from " + href("http://liinwww.ira.uka.de/", "LIIN");
	
	private static final String HOST = "liinwww.ira.uka.de";
	private static final String PATH = "/cgi-bin/bibshow";
	
	private static final String BIBTEX_START_BLOCK = "<pre class=\"bibtex\">";
	private static final String BIBTEX_END_BLOCK = "</pre>";
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), Pattern.compile(PATH + ".*")));

	
	public String getInfo() {
		return INFO;
	}

	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
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

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
}
