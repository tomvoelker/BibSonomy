package org.bibsonomy.scraper;

import org.bibsonomy.scraper.InformationExtraction.IEScraper;
import org.bibsonomy.scraper.generic.BibtexScraper;
import org.bibsonomy.scraper.generic.CoinsScraper;
import org.bibsonomy.scraper.generic.HighwireScraper;
import org.bibsonomy.scraper.generic.UnAPIScraper;
import org.bibsonomy.scraper.id.kde.isbn.ISBNScraper;
import org.bibsonomy.scraper.snippet.SnippetScraper;

/**  
 * Configures the scrapers used by BibSonomy.
 * 
 * @author rja
 *
 */
public class KDEScraperFactory {

	/**
	 * @return The scrapers produced by this factory.
	 */
	public CompositeScraper getScraper () {
		final CompositeScraper scraper = new CompositeScraper();
		scraper.addScraper(new UrlCompositeScraper());
		
		// this scraper always crawls the content and thus accepts ALL URLs!
		scraper.addScraper(new UnAPIScraper());
		
		//temporary solution to avoid manifold content download 
		scraper.addScraper(new HighwireScraper());
		
		scraper.addScraper(new SnippetScraper());

		scraper.addScraper(new CoinsScraper());
		
		// TODO: ISBNScraper can be used as a snippet scraper 
		scraper.addScraper(new ISBNScraper());
		
		// TODO: Scraper for searching bibtex in HTML-Sourcecode 
		scraper.addScraper(new BibtexScraper());

		/*
		 * If nothing works: do information extraction using MALLET.
		 */
		scraper.addScraper(new IEScraper());
		return scraper;
	}

}
