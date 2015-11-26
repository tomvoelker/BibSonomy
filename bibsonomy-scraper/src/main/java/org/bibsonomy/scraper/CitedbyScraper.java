/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * interface for scrapers that can scrape citedby data
 *
 * @author Haile
 */
public interface CitedbyScraper {

	 /**
	  * Tries to scrape the publications that cite the given publication.
	  * Note that this method should only be called <em>after</em> {@link Scraper#scrape(ScrapingContext)}.
	  * 
	 * @param scrapingContext
	 * @return <code>true</code>, if the citing papers were successfully be scraped
	 * @throws ScrapingException
	 */
	public boolean scrapeCitedby(final ScrapingContext scrapingContext) throws ScrapingException;
}
