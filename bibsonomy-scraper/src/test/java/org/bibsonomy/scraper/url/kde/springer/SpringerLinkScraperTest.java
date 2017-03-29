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
		final String url = "http://link.springer.com/article/10.1140%2Fepje%2Fi2002-10160-7";
		final String resultFile = "SpringerLinkScraperUnitURLTest1.bib";
		assertScraperResult(url, null, SpringerLinkScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_41
	 */
	@Test
	public void url2TestRun() {
		final String url = "http://link.springer.com/chapter/10.1007%2F3-540-34416-0_27";
		final String resultFile = "SpringerLinkScraperUnitURLTest2.bib";
		assertScraperResult(url, null, SpringerLinkScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_183
	 */
	@Test
	public void url4TestRun() {
		final String url = "http://link.springer.com/article/10.1007%2Fs13222-010-0004-8";
		final String resultFile = "SpringerLinkScraperUnitURLTest5.bib";
		assertScraperResult(url, null, SpringerLinkScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_235
	 */
	@Test
	public void url5TestRun() {
		final String url = "http://link.springer.com/chapter/10.1007%2F3-540-44527-7_10?LI=true";
		final String resultFile = "SpringerLinkScraperUnitURLTest7.bib";
		assertScraperResult(url, null, SpringerLinkScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_238
	 */
	@Test
	public void url6TestRun() {
		final String url = "http://link.springer.com/article/10.1023%2FA%3A1008346807097?LI=true#page-1";
		final String resultFile = "SpringerLinkScraperUnitURLTest8.bib";
		assertScraperResult(url, null, SpringerLinkScraper.class, resultFile);
	}
	
	@Test
	public void url7Test() {
		assertScraperResult("https://link.springer.com/chapter/10.1007/978-3-540-46332-0_1", null, SpringerLinkScraper.class, "springerlink/SpringerLinkScraperUnitURLTest7.bib");
	}
}
