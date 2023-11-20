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
package org.bibsonomy.scraper.url.kde.ieee;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for {@link IEEEXploreBookScraper}
 * @author tst
 *
 *
 * Sometime tests are failing though the output and the string in the test file seems to be the same.
 * In that case, check if the scraped context has dos line endings.
 */
@Category(RemoteTest.class)
public class IEEEXploreBookScraperTest {
	String resultDirectory = "ieee/xplore/book/";
	
	/**
	 * starts URL test with id url_36
	 */
	@Test
	public void urlTest1Run() {
		final String url = "https://ieeexplore.ieee.org/book/5263132";
		final String resultFile = resultDirectory + "IEEEXploreBookScraperUnitURLTest1.bib";
		assertScraperResult(url, IEEEXploreBookScraper.class, resultFile);
	}

	@Test
	public void urlTest2Run() {
		final String url = "https://ieeexplore.ieee.org/book/9127393";
		final String resultFile = resultDirectory + "IEEEXploreBookScraperUnitURLTest2.bib";
		assertScraperResult(url, IEEEXploreBookScraper.class, resultFile);
	}

	@Test
	public void urlTest3Run() {
		final String url = "https://ieeexplore.ieee.org/book/8503971";
		final String resultFile = resultDirectory + "IEEEXploreBookScraperUnitURLTest3.bib";
		assertScraperResult(url, IEEEXploreBookScraper.class, resultFile);
	}

	@Test
	public void urlTest4Run() {
		final String url = "https://ieeexplore.ieee.org/book/6267205";
		final String resultFile = resultDirectory + "IEEEXploreBookScraperUnitURLTest4.bib";
		assertScraperResult(url, IEEEXploreBookScraper.class, resultFile);
	}
}
