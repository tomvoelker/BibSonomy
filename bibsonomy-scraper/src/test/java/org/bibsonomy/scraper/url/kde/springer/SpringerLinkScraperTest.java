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
	String resultDirectory = "springer/springerlink/";

	/**
	 * starts URL test with id url_30
	 */
	@Test
	public void url1TestRun() {
		final String url = "https://link.springer.com/article/10.1140%2Fepje%2Fi2002-10160-7";
		final String resultFile = resultDirectory + "SpringerLinkScraperUnitURLTest1.bib";
		assertScraperResult(url, SpringerLinkScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_41
	 */
	@Test
	public void url2TestRun() {
		final String url = "https://link.springer.com/chapter/10.1007%2F3-540-34416-0_27";
		final String resultFile = resultDirectory + "SpringerLinkScraperUnitURLTest2.bib";
		assertScraperResult(url, SpringerLinkScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_183
	 */
	@Test
	public void url3TestRun() {
		final String url = "https://link.springer.com/article/10.1007%2Fs13222-010-0004-8";
		final String resultFile = resultDirectory + "SpringerLinkScraperUnitURLTest3.bib";
		assertScraperResult(url, SpringerLinkScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_235
	 */
	@Test
	public void url4TestRun() {
		final String url = "https://link.springer.com/chapter/10.1007%2F3-540-44527-7_10?LI=true";
		final String resultFile = resultDirectory + "SpringerLinkScraperUnitURLTest4.bib";
		assertScraperResult(url, SpringerLinkScraper.class, resultFile);
	}

	@Test
	public void url5TestRun() {
		final String url = "https://link.springer.com/article/10.1023%2FA%3A1008346807097?LI=true#page-1";
		final String resultFile = resultDirectory + "SpringerLinkScraperUnitURLTest5.bib";
		assertScraperResult(url, SpringerLinkScraper.class, resultFile);
	}

	@Test
	public void url6Test() {
		final String url = "https://link.springer.com/article/10.1007/s11192-014-1292-9/fulltext.html";
		final String resultFile = resultDirectory + "SpringerLinkScraperUnitURLTest6.bib";
		assertScraperResult(url, SpringerLinkScraper.class, resultFile);
	}


	@Test
	public void url7Test() {
		final String url = "https://link.springer.com/chapter/10.1007/978-3-540-46332-0_1";
		final String resultFile = resultDirectory + "SpringerLinkScraperUnitURLTest7.bib";
		assertScraperResult(url, SpringerLinkScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_238
	 */



}
