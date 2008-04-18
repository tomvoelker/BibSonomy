package org.bibsonomy.scraper.url.kde.springer;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.ScrapingException;
import org.bibsonomy.scraper.url.kde.worldcat.WorldCatScraper;

/**
 * Scraper for springer.com
 * @author tst
 */
public class SpringerScraper implements Scraper {

	private static final String INFO = "Scraper for books from springer.com with worldcat.org";
	
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith("springer.com")){
			try {
				String url = sc.getUrl().toString();
				String isbn = url.substring(url.lastIndexOf("/")+1);
				isbn = isbn.replace("-", "");
				
				WorldCatScraper worldCat = new WorldCatScraper();
				String bibtex = worldCat.getBibtexByISBN(isbn, sc);
				
				if(bibtex != null){
					sc.setBibtexResult(bibtex);
					sc.setScraper(this);
					return true;
				}
			} catch (MalformedURLException ex) {
				throw new ScrapingException(ex);
			}
		}
		return false;
	}

}
