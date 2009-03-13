package org.bibsonomy.scraper.url.kde.springer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.kde.worldcat.WorldCatScraper;

/**
 * Scraper for springer.com
 * @author tst
 */
public class SpringerScraper extends AbstractUrlScraper {

	private static final String INFO = "Springer Scraper: Scraper for books from " + href("http://www.springer.com/?SGWID=1-102-0-0-0", "Springer");
	
	/**
	 * Host
	 */
	private static final String HOST = "springer.com";
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST + "$"), AbstractUrlScraper.EMPTY_PATTERN));
	
	public String getInfo() {
		return INFO;
	}

	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
			sc.setScraper(this);
			
			try {
				String url = sc.getUrl().toString();
				String isbn = url.substring(url.lastIndexOf("/")+1);
				isbn = isbn.replace("-", "");
				
				WorldCatScraper worldCat = new WorldCatScraper();
				String bibtex = worldCat.getBibtexByISBN(isbn, sc);
				
				if(bibtex != null){
					sc.setBibtexResult(bibtex);
					return true;
				}else
					throw new ScrapingFailureException("getting bibtex failed");

			} catch (IOException ex) {
				throw new InternalFailureException(ex);
			}
	}
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
