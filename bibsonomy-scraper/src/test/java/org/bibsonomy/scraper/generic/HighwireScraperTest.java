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
package org.bibsonomy.scraper.generic;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * tests the HighwireScraper
 *
 * @author rja
 */
@Category(RemoteTest.class)
public class HighwireScraperTest {
	String resultDirectory = "highwire/";

	/**
	 * Test method for {@link org.bibsonomy.scraper.generic.HighwireScraper#scrape(org.bibsonomy.scraper.ScrapingContext)}.
	 */
	@Test
	public void testScrape1() {
		final String url = "http://www.pnas.org/content/115/4/E639";
		final String resultFile = resultDirectory + "HighwireScraperTest1.bib";
		assertScraperResult(url, HighwireScraper.class, resultFile);
	}

	/**
	 * Test method for {@link org.bibsonomy.scraper.generic.HighwireScraper#scrape(org.bibsonomy.scraper.ScrapingContext)}.
	 */
	@Test
	public void testScrape2() {
		final String url = "http://err.ersjournals.com/content/27/147/170106";
		final String resultFile = resultDirectory + "HighwireScraperTest2.bib";
		assertScraperResult(url, HighwireScraper.class, resultFile);
	}

	/**
	 * Test method for {@link org.bibsonomy.scraper.generic.HighwireScraper#scrape(org.bibsonomy.scraper.ScrapingContext)}.
	 */
	@Test
	public void testScrape3() {
		final String url = "https://www.jneurosci.org/content/32/42/14465.short?rss=1";
		final String resultFile = resultDirectory + "HighwireScraperTest3.bib";
		assertScraperResult(url, HighwireScraper.class, resultFile);
	}

	/**
	 * Test method for {@link org.bibsonomy.scraper.generic.HighwireScraper#scrape(org.bibsonomy.scraper.ScrapingContext)}.
	 */
	@Test
	public void testScrape4() {
		final String url = "https://cshperspectives.cshlp.org/content/3/3/a004994.full";
		final String resultFile = resultDirectory + "HighwireScraperTest4.bib";
		assertScraperResult(url, HighwireScraper.class, resultFile);
	}

	/**
	 * Test method for {@link org.bibsonomy.scraper.generic.HighwireScraper#scrape(org.bibsonomy.scraper.ScrapingContext)}.
	 */
	@Test
	public void testScrape5() {
		final String url = "https://cancerres.aacrjournals.org/content/81/24/6259";
		final String resultFile = resultDirectory + "HighwireScraperTest5.bib";
		assertScraperResult(url, HighwireScraper.class, resultFile);
	}

	@Test
	public void testScrape6() {
		final String url = "http://www.jimmunol.org/content/183/11/7569.full.pdf+html";
		final String resultFile = resultDirectory + "HighwireScraperTest6.bib";
		assertScraperResult(url, null, HighwireScraper.class, resultFile);
	}
}
