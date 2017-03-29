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
package org.bibsonomy.scraper.id.kde.isbn;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.ReachabilityTestRunner;
import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.bibsonomy.scraper.url.kde.ieee.IEEEComputerSocietyScraper;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Tests for ISBNScraper class and #166 #167
 * @author tst
 */
@Category(RemoteTest.class)
public class ISBNScraperTest {

	/**
	 * starts URL test with id url_166
	 */
	@Test
	public void url1TestRun(){
		final String selection = "978-3404201600";
		final String resultFile = "ISBNScraperUnitURLTest1.bib";
		assertScraperResult(null, selection, ISBNScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_167
	 */
	@Test
	public void url2TestRun(){
		final String selection = "9780387485300";
		final String resultFile = "ISBNScraperUnitURLTest2.bib";
		assertScraperResult(null, selection, ISBNScraper.class, resultFile);
	}
	
	/**
	 * XXX: endnote export for this publication is broken
	 * starts URL test with id url_170
	 */
	@Test
	public void url3TestRun(){
		final String selection = "0025-5858";
		final String resultFile = "ISSNScraperUnitURLTest1.bib";
		assertScraperResult(null, selection, ISBNScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_171
	 */
	@Test
	public void url4TestRun(){
		final String selection = "9783921568705";
		final String resultFile = "ISSNScraperUnitURLTest2.bib";
		assertScraperResult(null, selection, ISBNScraper.class, resultFile);
	}

	/**
	 * Tests {@link ISBNScraper#supportsScrapingContext(org.bibsonomy.scraper.ScrapingContext)}
	 */
	@Test
	public void testSupportsScrapingContext() {
		final ISBNScraper scraper = new ISBNScraper();
		
		assertTrue(scraper.supportsScrapingContext(ReachabilityTestRunner.ISBN_SCRAPER_TEST_CONTEXT));
	}
}
