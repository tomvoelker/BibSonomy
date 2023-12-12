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
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * tests for {@link HighwirePressScraper}
 *
 * @author Johannes
 */
@Category(RemoteTest.class)
public class HighwirePressScraperTest {
	String resultDirectory = "highwire/press/";

	@Test
	public void testHighwirePressScraper1() {
		final String url = "https://www.biorxiv.org/content/early/2017/10/06/199430";
		final String resultFile = resultDirectory + "HighwirePressScraperTest1.bib";
		assertScraperResult(url, HighwirePressScraper.class, resultFile);
	}
	
	@Test
	public void testHighwirePressScraper2() {
		final String url = "http://onlinelibrary.wiley.com/doi/10.1002/scj.20874/full";
		final String resultFile = resultDirectory + "HighwirePressScraperTest2.bib";
		assertScraperResult(url, HighwirePressScraper.class, resultFile);
	}
	
	@Test
	public void testHighwirePressScraper3() {
		final String url = "https://www.osapublishing.org/jlt/abstract.cfm?uri=jlt-35-20-4553";
		final String resultFile = resultDirectory +  "HighwirePressScraperTest3.bib";
		assertScraperResult(url, HighwirePressScraper.class, resultFile);
	}
	
	@Test
	public void testSupportsScrapingContext() throws MalformedURLException {
		ScrapingContext scrapingContext = new ScrapingContext(new URL("https://www.biorxiv.org/content/early/2017/10/06/199430"));
		assertTrue(new HighwirePressScraper().supportsScrapingContext(scrapingContext));
	}
}
