package scraper;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Composite Scraper contains other scrapers and the scrape method calls them
 * until a scraper is successful.
 * 
 */
public class CompositeScraper implements Scraper {

	private List<Scraper> _scrapers = new LinkedList<Scraper>();

	/**
	 * Call scrapers until one is successful.
	 * 
	 * @see scraper.Scraper#scrape(scraper.ScrapingContext)
	 */
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		for (Scraper scraper : _scrapers) {
			if (scraper.scrape(sc)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Add scraper to list.
	 * 
	 * @param scraper
	 */
	public void addScraper(Scraper scraper) {
		_scrapers.add(scraper);
	}
	
	public String getInfo () {
		return "Generic Composite Scraper";
	}
	
	/** 
	 * Returns the collection of all the scrapers contained in the Composite Scraper
	 * 
	 */
	public Collection<Scraper> getScraper () {
		LinkedList<Scraper> scrapers = new LinkedList<Scraper>();
		for (Scraper scraper : _scrapers) {
			scrapers.addAll(scraper.getScraper());
		}
		return scrapers;
	}

}