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
package org.bibsonomy.scraper.url.kde.worldscientific;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.junit.RemoteTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper test for url_279, references and cited by
 * @author Haile
 */
@Category(RemoteTest.class)
public class WorldScientificScraperTest {
	String resultDirectory = "worldscientific/";
	/**
	 * starts URL test with id url_279
	 */
	@Test
	public void urlTestRun1(){
		final String url = "https://www.worldscientific.com/doi/abs/10.1142/S0219622006002271";
		final String resultFile = resultDirectory + "WorldScientificScraperUnitURLTest.bib";
		assertScraperResult(url, null, WorldScientificScraper.class, resultFile);
	}
	@Ignore
	@Test
	public void testCitedby() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("https://www.worldscientific.com/doi/pdf/10.1142/S0219622006002271"));
		
		WorldScientificScraper ws = new WorldScientificScraper();
		assertTrue(ws.scrape(sc));
		assertTrue(ws.scrapeCitedby(sc));
		final String cby = sc.getCitedBy();
		assertNotNull(cby);
		assertTrue(cby.length() > 100);
		assertTrue(cby.contains("Application of Power Big Data in Targeted Poverty Alleviation"));
	}
	@Ignore
	@Test
	public void testReferences() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("https://www.worldscientific.com/doi/pdf/10.1142/S0219622006002271"));
		
		WorldScientificScraper ws = new WorldScientificScraper();
		assertTrue(ws.scrape(sc));
		assertTrue(ws.scrapeReferences(sc));
		final String references = sc.getReferences();
		assertNotNull(references);
		assertTrue(references.length() > 100);
		assertTrue(references.contains("Mining Newsgroups using networks arising from social behavior"));
	}
}