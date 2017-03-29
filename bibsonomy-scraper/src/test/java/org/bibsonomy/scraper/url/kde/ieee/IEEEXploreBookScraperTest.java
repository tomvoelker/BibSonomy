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
package org.bibsonomy.scraper.url.kde.ieee;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.bibsonomy.scraper.junit.RemoteTestAssert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for {@link IEEEXploreBookScraper}
 * @author tst
 *
 *
 * Sometime tests are failing though the output and the string in the test file seems to be the same.
 * In that case, check if the scraped context has dos line endings.
 */
@Category(RemoteTest.class)
public class IEEEXploreBookScraperTest {
	
	/**
	 * starts URL test with id url_36
	 */
	@Test
	public void urlTestRun1(){
//		UnitTestRunner.runSingleTest("url_36");
		
		final String url = "http://ieeexplore.ieee.org/xpl/bkabstractplus.jsp?bkn=5263132";
		final String selection = null;
		final Class<? extends Scraper> scraperClass = org.bibsonomy.scraper.url.kde.ieee.IEEEXploreBookScraper.class;
		final String resultFile = "IEEEXploreBookScraperUnitURLTest.bib";
		RemoteTestAssert.assertScraperResult(url, selection, scraperClass, resultFile);
	}
	
	/**
	 * starts URL test with id url_157
	 */
	@Test
	public void urlTestRun2(){
//		UnitTestRunner.runSingleTest("url_157");
		
		final String url = "http://ieeexplore.ieee.org/search/freesrchabstract.jsp?arnumber=5286085&isnumber=5284878&punumber=5284806&k2dockey=5286085@ieecnfs&query=%28limpens+freddy%3Cin%3Eau%29&pos=0&access=no";
		final String selection = null;
		final Class<? extends Scraper> scraperClass = org.bibsonomy.scraper.url.kde.ieee.IEEEXploreBookScraper.class;
		final String resultFile = "IEEEXploreBookScraperUnitURLTest1.bib";
		RemoteTestAssert.assertScraperResult(url, selection, scraperClass, resultFile);
	}
	
	/**
	 * starts URL test with id url_158
	 */
	@Test
	public void urlTestRun3(){
//		UnitTestRunner.runSingleTest("url_158");
		
		final String url = "http://ieeexplore.ieee.org/search/srchabstract.jsp?arnumber=4383076&isnumber=4407525&punumber=10376&k2dockey=4383076@ieeejrns&query=%28%28hotho%29%3Cin%3Eau+%29&pos=0&access=n0";
		final String selection = null;
		final Class<? extends Scraper> scraperClass = org.bibsonomy.scraper.url.kde.ieee.IEEEXploreBookScraper.class;
		final String resultFile = "IEEEXploreBookScraperUnitURLTest2.bib";
		RemoteTestAssert.assertScraperResult(url, selection, scraperClass, resultFile);
	}
	@Test
	public void testCitedby() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=5286085"));
		
		IEEEXploreBookScraper book = new IEEEXploreBookScraper();

		assertTrue(book.scrape(sc));

		assertTrue(book.scrapeCitedby(sc));

		final String cby = sc.getCitedBy();
		assertNotNull(cby);

		assertTrue(cby.length() > 100);

		assertEquals("{\"formulaStrippedArticleTitle\":\"Collaborative Semantic Structuring of Folksonomies\",\"title\":\"Collaborative".trim(), cby.substring(0, 106).trim());

		assertTrue(cby.contains("S. Beldjoudi,"));
	}
	@Test
	public void testReferences() throws Exception{
		final ScrapingContext sc = new ScrapingContext(new URL("http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=5286085"));
		
		final IEEEXploreBookScraper book = new IEEEXploreBookScraper();
		
		assertTrue(book.scrape(sc));
		
		assertTrue(book.scrapeReferences(sc));
		
		final String reference = sc.getReferences();
		
		assertNotNull(reference);
		
		assertTrue(reference.length() > 100);
		
		assertEquals("1.", reference.trim().substring(0, 2));
		
		assertTrue(reference.contains("U. Bojars"));
	}
}
