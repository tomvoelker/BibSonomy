package org.bibsonomy.scraper;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.exceptions.UseageFailureException;

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
			
		} catch (final InternalFailureException e) {
			// internal failure 
			log.fatal(e);			
			throw (e);
		} catch (final UseageFailureException e) {
			// a user has used a scraper in a wrong way
			log.info(e);
			throw (e);
		} catch (final PageNotSupportedException e) {
			// a scraper can't scrape a page but the host is supported
			log.error(e);
			throw (e);
		} catch (final ScrapingFailureException e) {
			// getting bibtex failed (conversion failed)
			log.fatal(e);
			throw (e);
		} catch (final ScrapingException e) {
			// something else
			log.error(e);
			throw (e);
		} catch (final Exception e) {
			// unexpected internal failure 
			log.fatal(e);			
			throw (new InternalFailureException(e));
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