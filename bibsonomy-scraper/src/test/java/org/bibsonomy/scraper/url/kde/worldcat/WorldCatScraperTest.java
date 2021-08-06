/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
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
		assertScraperResult("http://www.worldcat.org/oclc/37212333", WorldCatScraper.class, "WorldCatScraperUnitURLTest1.bib");
	}

	/**
	 * starts URL test with id url_60
	 */
	@Test
	public void url2TestRun(){
		assertScraperResult("http://www.worldcat.org/oclc/85511690", WorldCatScraper.class, "WorldCatScraperUnitURLTest2.bib");
	}
	
	/**
	 * starts URL test with id url_163
	 */
	@Test
	public void url3TestRun(){
		assertScraperResult("http://www.worldcat.org/oclc/163641505", WorldCatScraper.class, "WorldCatScraperUnitURLTest3.bib");
	}
	
	/**
	 * starts URL test with id url_338
	 */
	@Test
	public void url4TestRun(){
		assertScraperResult("http://www.worldcat.org/oclc/254138269", WorldCatScraper.class, "WorldCatScraperUnitURLTest4.bib");
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
