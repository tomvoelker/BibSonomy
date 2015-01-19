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
package org.bibsonomy.scraper.url.kde.citeseer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #112 for CiteseerxScraperTest
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class CiteseerxScraperTest {
	
	/**
	 * starts URL test with id url_112
	 */
	@Test
	public void url1TestRun(){
		UnitTestRunner.runSingleTest("url_112");
	}
	
	@Test
	public void test1() throws MalformedURLException {
		final String url = "http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.14.7185";
		final ScrapingContext sc = new ScrapingContext(new URL(url));
		
		final CiteseerxScraper scraper = new CiteseerxScraper();
		
		try {
			final boolean scrape = scraper.scrape(sc);
			System.out.println(sc.getBibtexResult());
			assertTrue(scrape);
		} catch (final ScrapingException ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	public void testTemporaryMalformed() throws MalformedURLException {
		final String url = "http://citeseerx.ist.psu.edu/viewdoc/summary10.1.1.14.7185&description=Conceptual+Clustering+of+Text+Clusters";
		final ScrapingContext sc = new ScrapingContext(new URL(url));
		final CiteseerxScraper scraper = new CiteseerxScraper();
		
		try {
			final boolean scrape = scraper.scrape(sc);
			System.out.println(sc.getBibtexResult());
			assertTrue(scrape);
		} catch (final ScrapingException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void runTest1() throws MalformedURLException {
		final String url = "http://citeseerx.ist.psu.edu/viewdoc/summary;jsessionid=352C9BD0F67928E2EDAFA8B58ACFBFB9?doi=10.1.1.110.903";
		final CiteseerxScraper scraper = new CiteseerxScraper();
		final ScrapingContext sc = new ScrapingContext(new URL(url));
		
		try {
			scraper.scrape(sc);
			System.out.println(sc.getBibtexResult());
		} catch (final ScrapingException ex) {
			fail(ex.getMessage());
		}
	}
}
