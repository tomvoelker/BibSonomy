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

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * tests the HighwireScraper
 *
 * @author rja
 */
@Category(RemoteTest.class)
public class HighwireScraperTest {

	/**
	 * Test method for {@link org.bibsonomy.scraper.generic.HighwireScraper#scrape(org.bibsonomy.scraper.ScrapingContext)}.
	 */
	@Test
	public void testScrape1() {
		assertScraperResult("http://www.pnas.org/content/115/4/E639", null, HighwireScraper.class, "HighwireScraperTest1.bib");
	}

	/**
	 * Test method for {@link org.bibsonomy.scraper.generic.HighwireScraper#scrape(org.bibsonomy.scraper.ScrapingContext)}.
	 */
	@Test
	public void testScrape2() {
		assertScraperResult("http://err.ersjournals.com/content/27/147/170106", null, HighwireScraper.class, "HighwireScraperTest2.bib");
	}

	/**
	 * Test method for {@link org.bibsonomy.scraper.generic.HighwireScraper#scrape(org.bibsonomy.scraper.ScrapingContext)}.
	 */
	@Test
	public void testScrape3() {
		assertScraperResult("http://eel.ecsdl.org/content/4/1/A4.abstract", null, HighwireScraper.class, "HighwireScraperTest3.bib");
	}

	/**
	 * Test method for {@link org.bibsonomy.scraper.generic.HighwireScraper#scrape(org.bibsonomy.scraper.ScrapingContext)}.
	 */
	@Test
	public void testScrape4() {
		assertScraperResult("http://horttech.ashspublications.org/content/28/1/10.abstract", null, HighwireScraper.class, "HighwireScraperTest4.bib");
	}

	/**
	 * Test method for {@link org.bibsonomy.scraper.generic.HighwireScraper#scrape(org.bibsonomy.scraper.ScrapingContext)}.
	 */
	@Test
	public void testScrape5() {
		assertScraperResult("https://pubs.geoscienceworld.org/paleobiol/article-abstract/43/4/620/520315/sexual-dimorphism-and-sexual-selection-in", null, HighwireScraper.class, "HighwireScraperTest5.bib");
	}

	/**
	 * Test method for {@link org.bibsonomy.scraper.generic.HighwireScraper#scrape(org.bibsonomy.scraper.ScrapingContext)}.
	 */
	@Test
	public void testScrape6() {
		assertScraperResult("http://circ.ahajournals.org/content/early/2015/11/08/CIRCULATIONAHA.115.019768.abstract", null, HighwireScraper.class, "AhaJournalsScraperUnitURLTest1.bib");
	} 
}
