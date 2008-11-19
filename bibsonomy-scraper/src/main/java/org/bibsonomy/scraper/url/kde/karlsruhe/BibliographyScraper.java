package org.bibsonomy.scraper.url.kde.karlsruhe;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;

/**
 * Scraper for liinwww.ira.uka.de/bibliography
 * @author tst
 * @version $Id$
 */
public class BibliographyScraper implements Scraper, UrlScraper {

	private static final String INFO = "LIIN Scraper: Scrapes BibTeX refrences from <a href=\"http://liinwww.ira.uka.de/\">LIIN</a>. Author: KDE";
	
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
		if(sc != null && sc.getUrl() != null && supportsUrl(sc.getUrl())){
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

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), Pattern.compile(PATH + ".*")));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}
	
}
