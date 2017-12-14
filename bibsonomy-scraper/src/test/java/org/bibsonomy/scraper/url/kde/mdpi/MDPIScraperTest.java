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
package org.bibsonomy.scraper.url.kde.mdpi;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for MDPIScraper
 *
 * @author Haile
 */
@Category(RemoteTest.class)
public class MDPIScraperTest {
	/**
	 * starts URL test with id url_275
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://www.mdpi.com/2072-4292/5/10/5122";
		final String resultFile = "MDPIScraperUnitURLTest.bib";
		assertScraperResult(url, null, MDPIScraper.class, resultFile);
	}
	
	@Test
	public void url2TestRun(){
		final String url = "http://www.mdpi.com/2220-9964/6/9/272";
		final String resultFile = "MDPIScraperUnitURLTest2.bib";
		assertScraperResult(url, null, MDPIScraper.class, resultFile);
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testCitedBy() throws Exception{
		final ScrapingContext sc = new ScrapingContext(new URL("http://www.mdpi.com/2072-4292/5/10/5122"));
		MDPIScraper ms = new MDPIScraper();
		assertTrue(ms.scrape(sc));
		assertTrue(ms.scrapeCitedby(sc));
		final String cby = sc.getCitedBy();
		assertNotNull(cby);
		assertTrue(cby.length() > 100);
		assertEquals("<em>Citations registered in CrossRef as of".trim(), cby.substring(0, 42).trim());
		assertTrue(cby.contains("Zhang, L."));
	}
}
