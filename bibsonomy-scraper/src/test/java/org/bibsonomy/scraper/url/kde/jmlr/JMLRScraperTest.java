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
package org.bibsonomy.scraper.url.kde.jmlr;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.bibsonomy.scraper.url.kde.jeb.JEBScraper;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #101, #102, #103 for JMLRScraper
 * @author tst
 */
@Category(RemoteTest.class)
public class JMLRScraperTest {
	String resultDirectory = "jmlr/";
	
	/**
	 * starts URL test with id url_101
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://jmlr.csail.mit.edu/papers/v4/petridis03a.html";
		final String resultFile = resultDirectory + "JMLRScraperUnitURLTest1.bib";
		assertScraperResult(url, JMLRScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_102
	 */
	@Test
	public void url2TestRun(){
		final String url = "http://jmlr.csail.mit.edu/papers/v9/henrich08a.html";
		final String resultFile = resultDirectory + "JMLRScraperUnitURLTest2.bib";
		assertScraperResult(url, JMLRScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_103
	 */
	@Test
	public void url3TestRun(){
		final String url = "http://jmlr.csail.mit.edu/papers/v8/list07a.html";
		final String resultFile = resultDirectory + "JMLRScraperUnitURLTest3.bib";
		assertScraperResult(url, JMLRScraper.class, resultFile);
	}
	
}
