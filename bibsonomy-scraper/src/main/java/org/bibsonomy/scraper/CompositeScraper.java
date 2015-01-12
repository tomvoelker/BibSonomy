/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scraper;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.exceptions.UsageFailureException;

/**
 * This scraper contains other scrapers and the scrape method calls them
 * until a scraper is successful.
 * 
 * @param <S> Type of scraper this scraper contains.
 * 
 */
public class CompositeScraper<S extends Scraper> implements Scraper {

	private final List<S> scrapers = new LinkedList<S>();
	private static final Log log = LogFactory.getLog(CompositeScraper.class);

	/**
	 * Call scrapers until one is successful.
	 * 
	 * @see org.bibsonomy.scraper.Scraper#scrape(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrape(final ScrapingContext scrapingContext) throws ScrapingException {
		try {
			for (final S scraper : this.scrapers) {
				if (scraper.scrape(scrapingContext)) {
					return true;
				}
			}
			
		} catch (final InternalFailureException ex) {
			log.fatal("Exception during scraping following url: " + scrapingContext.getUrl());
			// internal failure 
			log.fatal(ex, ex);			
			throw ex;
		} catch (final UsageFailureException ex) {
			log.info("Exception during scraping following url: " + scrapingContext.getUrl());
			// a user has used a scraper in a wrong way
			log.info(ex);
			throw ex;
		} catch (final PageNotSupportedException ex) {
			log.error("Exception during scraping following url: " + scrapingContext.getUrl());
			// a scraper can't scrape a page but the host is supported
			log.error(ex, ex);
			throw ex;
		} catch (final ScrapingFailureException ex) {
			log.fatal("Exception during scraping following url: " + scrapingContext.getUrl());
			// getting bibtex failed (conversion failed)
			log.fatal(ex,  ex);
			throw ex;
		} catch (final ScrapingException ex) {
			log.error("Exception during scraping following url: " + scrapingContext.getUrl());
			// something else
			log.error(ex, ex);
			throw ex;
		} catch (final Exception ex) {
			log.fatal("Exception during scraping following url: " + scrapingContext.getUrl());
			// unexpected internal failure 
			log.fatal(ex, ex);			
			throw (new InternalFailureException(ex));
		}
		return false;
	}

	/**
	 * Add a scraper to list.
	 * 
	 * @param scraper
	 */
	public void addScraper(final S scraper) {
		this.scrapers.add(scraper);
	}

	@Override
	public String getInfo () {
		return "Generic Composite Scraper";
	}

	/** 
	 * Returns the collection of all the scrapers contained in the Composite Scraper
	 * 
	 */
	@Override
	public Collection<Scraper> getScraper () {
		final LinkedList<Scraper> scrapers = new LinkedList<Scraper>();
		for (final S scraper : this.scrapers) {
			scrapers.addAll(scraper.getScraper());
		}
		return scrapers;
	}

	@Override
	public boolean supportsScrapingContext(final ScrapingContext scrapingContext){
		for (final S scraper : this.scrapers){
			if (scraper.supportsScrapingContext(scrapingContext)){
				return true;
			}
		}
		return false;
	}

}