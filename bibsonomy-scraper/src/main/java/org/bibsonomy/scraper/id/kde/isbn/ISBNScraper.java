package org.bibsonomy.scraper.id.kde.isbn;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.kde.worldcat.WorldCatScraper;
import org.bibsonomy.util.id.ISBNUtils;

/**
 * Scraper for ISBN support. Searchs for ISBN in snippet and uses WorldcatScraper for download.
 *  
 * @author tst
 * @version $Id$
 */
public class ISBNScraper implements Scraper {

	private static final String INFO = "ISBN support in scraped snippet";

	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(final ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getSelectedText() != null){
			final String isbn = ISBNUtils.extractISBN(sc.getSelectedText());

			if (isbn != null) {
				try {
					final String bibtex = WorldCatScraper.getBibtexByISBN(isbn);

					if(bibtex != null) {
						sc.setBibtexResult(bibtex);
						sc.setScraper(this);
						return true;
					} else
						throw new ScrapingFailureException("bibtex download from worldcat failed");
				} catch (final IOException ex) {
					throw new InternalFailureException(ex);
				}

			}
		}
		return false;
	}



	public boolean supportsScrapingContext(ScrapingContext sc) {
		if(sc.getSelectedText() != null){
			final String isbn = ISBNUtils.extractISBN(sc.getSelectedText());
			if (isbn != null)
				return true;
		}
		return false;
	}
	
	public static ScrapingContext getTestContext(){
		final ScrapingContext context = new ScrapingContext(null);
		context.setSelectedText("9783608935448");
		return context;
	}

}
