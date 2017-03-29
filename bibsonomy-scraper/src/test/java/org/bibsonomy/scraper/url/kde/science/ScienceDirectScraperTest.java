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
package org.bibsonomy.scraper.url.kde.science;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper Tests for ScienceDirectScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class ScienceDirectScraperTest {

	/**
	 * starts URL test with id url_28
	 */
	@Test
	public void urlTestRun1(){
		assertScraperResult("http://www.sciencedirect.com/science/article/pii/S0004370207000471", null, ScienceDirectScraper.class, "sciencedirect/ScienceDirectScraperUnitURLTest1.bib");
	}

	/**
	 * starts URL test with id url_141
	 */
	@Test
	public void urlTestRun2() {
		assertScraperResult("http://www.sciencedirect.com/science/article/pii/S1570826806000084", null, ScienceDirectScraper.class, "sciencedirect/ScienceDirectScraperUnitURLTest2.bib");
	}

	/**
	 * starts URL test with id url_159
	 */
	@Test
	public void url3TestRun() {
		assertScraperResult("http://www.sciencedirect.com/science/article/pii/S138912860700179X", null, ScienceDirectScraper.class, "sciencedirect/ScienceDirectScraperUnitURLTest3.bib");
	}
	
	/**
	 * starts URL test with id url_162
	 */
	@Test
	public void url4TestRun(){
		assertScraperResult("http://www.sciencedirect.com/science/article/pii/S1389128602002116", null, ScienceDirectScraper.class, "sciencedirect/ScienceDirectScraperUnitURLTest4.bib");
	}
	
	/**
	 * starts URL test with id url_179
	 */
	@Test
	public void url5TestRun() {
		assertScraperResult("http://www.sciencedirect.com/science/article/pii/S0009261400002268", null, ScienceDirectScraper.class, "sciencedirect/ScienceDirectScraperUnitURLTest5.bib");
	}
	
	/**
	 * starts URL test with id url_185
	 */
	@Test
	public void url6TestRun() {
		assertScraperResult("http://www.sciencedirect.com/science/article/pii/S1570826810000326", null, ScienceDirectScraper.class, "sciencedirect/ScienceDirectScraperUnitURLTest6.bib");
	}
	
	/**
	 * tests another url
	 */
	@Test
	public void url7TestRun() {
		assertScraperResult("http://www.sciencedirect.com/science/article/pii/S0304397507000631", null, ScienceDirectScraper.class, "sciencedirect/ScienceDirectScraperUnitURLTest7.bib");
	}

}
