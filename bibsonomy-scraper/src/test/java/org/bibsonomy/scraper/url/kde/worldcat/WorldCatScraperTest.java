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
package org.bibsonomy.scraper.url.kde.worldcat;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #59 #60 #163 for WorldCatScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class WorldCatScraperTest {
	
	/**
	 * starts URL test with id url_59
	 */
	@Test
	public void url1TestRun(){
		UnitTestRunner.runSingleTest("url_59");
	}

	/**
	 * starts URL test with id url_60
	 */
	@Test
	public void url2TestRun(){
		UnitTestRunner.runSingleTest("url_60");
	}
	
	/**
	 * starts URL test with id url_163
	 */
	@Test
	public void url3TestRun(){
		UnitTestRunner.runSingleTest("url_163");
	}
	
	/**
	 * starts URL test with id url_332
	 */
	@Test
	public void url4TestRun(){
		UnitTestRunner.runSingleTest("url_338");
	}
	
	/**
	 * test getting URL 
	 */
	@Test
	public void getUrlForIsbnTest(){
		try {
			assertTrue(WorldCatScraper.getUrlForIsbn("0123456789").toString().equals("http://www.worldcat.org/search?qt=worldcat_org_all&q=0123456789"));
		} catch (MalformedURLException ex) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testScrape() throws MalformedURLException {
		final WorldCatScraper scraper = new WorldCatScraper();
		final URL urlForIsbn = new URL("http://www.worldcat.org/oclc/3119916&referer=brief_results");
		final ScrapingContext sc = new ScrapingContext(urlForIsbn);
		
		try {
			assertTrue(scraper.scrape(sc));
		} catch (ScrapingException ex) {
			Assert.fail(ex.getMessage());
		}
	}
}
