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
package org.bibsonomy.scraper.url.kde.springer;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #30 #41 for SpringerLinkScraper
 * 
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class SpringerLinkScraperTest {

	/**
	 * starts URL test with id url_30
	 */
	@Test
	public void url1TestRun() {
		UnitTestRunner.runSingleTest("url_30");
	}

	/**
	 * starts URL test with id url_41
	 */
	@Test
	public void url2TestRun() {
		UnitTestRunner.runSingleTest("url_41");
	}

	/**
	 * starts URL test with id url_183
	 */
	@Test
	public void url4TestRun() {
		UnitTestRunner.runSingleTest("url_183");
	}

	/**
	 * starts URL test with id url_235
	 */
	@Test
	public void url5TestRun() {
		UnitTestRunner.runSingleTest("url_235");
	}

	/**
	 * starts URL test with id url_238
	 */
	@Test
	public void url6TestRun() {
		UnitTestRunner.runSingleTest("url_238");
	}
	
	@Test
	public void url7Test() {
		assertScraperResult("https://link.springer.com/chapter/10.1007/978-3-540-46332-0_1", null, SpringerLinkScraper.class, "springerlink/SpringerLinkScraperUnitURLTest7.bib");
	}
}
