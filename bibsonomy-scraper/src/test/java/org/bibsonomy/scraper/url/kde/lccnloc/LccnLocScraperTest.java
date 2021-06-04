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
package org.bibsonomy.scraper.url.kde.lccnloc;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Mohammed Abed
 */
@Category(RemoteTest.class)
public class LccnLocScraperTest {
	
	/**
	 * starts URL test with id url_360
	 */
	@Test
	public void url1TestRun() {
		assertScraperResult("https://lccn.loc.gov/2005929872", LccnLocScraper.class, "LccnLocScraperScraperUnitURLTest.bib");
	}
	
	/**
	 * starts URL test with id url_361
	 */
	@Test
	public void url2TestRun() {
		assertScraperResult("https://lccn.loc.gov/2005929872/dc", LccnLocScraper.class, "LccnLocScraperScraperUnitURLTest.bib");
	}
	
	/**
	 * starts URL test with id url_362
	 */
	@Test
	public void url3TestRun() {
		assertScraperResult("https://lccn.loc.gov/95790943", LccnLocScraper.class, "LccnLocScraperScraperUnitURLTest2.bib");
	}
	
	/**
	 * starts URL test with id url_363
	 */
	@Test
	public void url4TestRun() {
		assertScraperResult("https://lccn.loc.gov/77368709/dc", LccnLocScraper.class, "LccnLocScraperScraperUnitURLTest3.bib");
	}
	
	/**
	 * starts URL test with id url_364
	 */
	@Test
	public void url5TestRun() {
		assertScraperResult("https://catalog.loc.gov/vwebv/holdingsInfo?searchId=6242&recCount=25&recPointer=1&bibId=1781294", LccnLocScraper.class, "LccnLocScraperScraperUnitURLTest4.bib");
	}
}
