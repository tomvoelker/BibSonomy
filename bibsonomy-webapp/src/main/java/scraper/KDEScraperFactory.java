package scraper;

import scraper.InformationExtraction.IEScraper;
import scraper.snippet.SnippetScraper;
import scraper.url.URLCompositeScraper;
import scraper.url.kde.highwire.HighwireScraper;

public class KDEScraperFactory {

	public CompositeScraper getScraper () {
		CompositeScraper scraper = new CompositeScraper();
		scraper.addScraper(new URLCompositeScraper());
		
		//temporary solution to avoid manifold content download 
		scraper.addScraper(new HighwireScraper());
		
		scraper.addScraper(new SnippetScraper());		
		scraper.addScraper(new IEScraper());
		return scraper;
	}

}
