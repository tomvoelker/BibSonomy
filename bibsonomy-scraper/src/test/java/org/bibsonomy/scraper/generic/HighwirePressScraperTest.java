/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.generic;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * tests for {@link HighwirePressScraper}
 *
 * @author Johannes
 */
@Category(RemoteTest.class)
public class HighwirePressScraperTest {

	@Test
	public void testHighwirePressScraper1() {
		final String url = "https://www.biorxiv.org/content/early/2017/10/06/199430";
		final String resultFile = "HighwirePressScraperTest1.bib";
		assertScraperResult(url, null, HighwirePressScraper.class, resultFile);
	}
	
	@Test
	public void testHighwirePressScraper2() {
		final String url = "http://onlinelibrary.wiley.com/doi/10.1002/scj.20874/full";
		final String resultFile = "HighwirePressScraperTest2.bib";
		assertScraperResult(url, null, HighwirePressScraper.class, resultFile);
	}
	
	@Test
	public void testHighwirePressScraper3() {
		final String url = "https://www.osapublishing.org/jlt/abstract.cfm?uri=jlt-35-20-4553";
		final String resultFile = "HighwirePressScraperTest3.bib";
		assertScraperResult(url, null, HighwirePressScraper.class, resultFile);
	}
	
	@Test
	public void testSupportsScrapingContext() throws MalformedURLException {
		ScrapingContext scrapingContext = new ScrapingContext(new URL("https://www.biorxiv.org/content/early/2017/10/06/199430"));
		assertTrue(new HighwirePressScraper().supportsScrapingContext(scrapingContext));
	}
}
