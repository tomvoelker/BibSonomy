package org.bibsonomy.scraper;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * This scraper contains other scrapers and the scrape method calls them
 * until a scraper is successful.
 * 
 */
public class CompositeScraper implements Scraper {

	private List<Scraper> _scrapers = new LinkedList<Scraper>();
	private static final Logger log = Logger.getLogger(CompositeScraper.class);

	/**
	 * Call scrapers until one is successful.
	 * 
	 * @see org.bibsonomy.scraper.Scraper#scrape(org.bibsonomy.scraper.ScrapingContext)
	 */
	public boolean scrape(final ScrapingContext scrapingContext) throws ScrapingException {
		try {
			for (final Scraper scraper : _scrapers) {
				if (scraper.scrape(scrapingContext)) {
					return true;
				}
			}
		} catch (final ScrapingException e) {
			log.error(e);
			throw (e);
		}
		return false;
	}

	/**
	 * Add a scraper to list.
	 * 
	 * @param scraper
	 */
	public void addScraper(final Scraper scraper) {
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
		final LinkedList<Scraper> scrapers = new LinkedList<Scraper>();
		for (final Scraper scraper : _scrapers) {
			scrapers.addAll(scraper.getScraper());
		}
		return scrapers;
	}

}