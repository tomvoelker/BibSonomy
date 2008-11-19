package org.bibsonomy.scraper.url.kde.springer;

import java.net.MalformedURLException;
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
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;
import org.bibsonomy.scraper.url.kde.worldcat.WorldCatScraper;

/**
 * Scraper for springer.com
 * @author tst
 */
public class SpringerScraper implements Scraper, UrlScraper {

	private static final String INFO = "Springer Scraper: Scraper for books from <a herf=\"http://www.springer.com/?SGWID=1-102-0-0-0\">Springer</a>. Author: KDE";
	
	/**
	 * Host
	 */
	private static final String HOST = "springer.com";
	
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith("springer.com")){
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

			} catch (MalformedURLException ex) {
				throw new InternalFailureException(ex);
			}
		}
		return false;
	}
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), UrlScraper.EMPTY_PATTERN));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}

}
