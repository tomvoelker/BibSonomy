package org.bibsonomy.scraper;

import org.bibsonomy.scraper.InformationExtraction.IEScraper;
import org.bibsonomy.scraper.generic.UnAPIScraper;
import org.bibsonomy.scraper.snippet.SnippetScraper;
import org.bibsonomy.scraper.url.URLCompositeScraper;
import org.bibsonomy.scraper.url.kde.highwire.HighwireScraper;

public class KDEScraperFactory {

	public CompositeScraper getScraper () {
		final CompositeScraper scraper = new CompositeScraper();
		scraper.addScraper(new URLCompositeScraper());
		
		// this scraper always crawls the content and thus accepts ALL URLs!
		scraper.addScraper(new UnAPIScraper());
		
		//temporary solution to avoid manifold content download 
		scraper.addScraper(new HighwireScraper());
		
		scraper.addScraper(new SnippetScraper());

		// TODO: ISBNScraper can be used as a snippet scraper 
		//scraper.addScraper(new ISBNScraper());
		
		// TODO: Scraper for searching bibtex in HTML-Sourcecode 
		//scraper.addScraper(new BibtexScraper());

		
		scraper.addScraper(new IEScraper());
		return scraper;
	}

}
