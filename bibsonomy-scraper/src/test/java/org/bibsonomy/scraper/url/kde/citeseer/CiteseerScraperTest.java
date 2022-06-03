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
package org.bibsonomy.scraper.url.kde.citeseer;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #112 for CiteseerxScraperTest
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class CiteseerScraperTest {
	String resultDirectory = "citeseer/";

	@Test
	public void url1TestRun(){
		final String url = "https://citeseer.ist.psu.edu/viewdoc/summary?doi=10.1.1.1064.4804&rank=1&q=The%20anatomy%20of%20a%20large-scale%20hypertextual%20{Web}%20search%20engine&osm=&ossid=";
		final String resultFile = resultDirectory + "CiteseerScraperUnitURLTest1.bib";
		assertScraperResult(url, CiteseerScraper.class, resultFile);
	}

	@Test
	public void url2TestRun(){
		final String url = "http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.14.7185";
		final String resultFile = resultDirectory + "CiteseerScraperUnitURLTest2.bib";
		assertScraperResult(url, CiteseerScraper.class, resultFile);
	}
	
	@Test
	public void test1() throws MalformedURLException {
		final String url = "http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.14.7185";
		final ScrapingContext sc = new ScrapingContext(new URL(url));
		
		final CiteseerScraper scraper = new CiteseerScraper();
		
		try {
			final boolean scrape = scraper.scrape(sc);
			assertTrue(scrape);
		} catch (final ScrapingException ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	public void testTemporaryMalformed() throws MalformedURLException {
		final String url = "http://citeseerx.ist.psu.edu/viewdoc/summary10.1.1.14.7185&description=Conceptual+Clustering+of+Text+Clusters";
		final ScrapingContext sc = new ScrapingContext(new URL(url));
		final CiteseerScraper scraper = new CiteseerScraper();
		
		try {
			final boolean scrape = scraper.scrape(sc);
			assertTrue(scrape);
		} catch (final ScrapingException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void runTest1() throws MalformedURLException {
		final String url = "http://citeseerx.ist.psu.edu/viewdoc/summary;jsessionid=352C9BD0F67928E2EDAFA8B58ACFBFB9?doi=10.1.1.110.903";
		final CiteseerScraper scraper = new CiteseerScraper();
		final ScrapingContext sc = new ScrapingContext(new URL(url));
		
		try {
			scraper.scrape(sc);
		} catch (final ScrapingException ex) {
			fail(ex.getMessage());
		}
	}
}
