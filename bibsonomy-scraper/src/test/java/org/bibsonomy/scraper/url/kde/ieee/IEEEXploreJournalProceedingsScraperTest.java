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
package org.bibsonomy.scraper.url.kde.ieee;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.bibsonomy.scraper.junit.RemoteTestAssert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #13 #127 for IEEEXploreJournalProceedingsScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class IEEEXploreJournalProceedingsScraperTest {
	
	/**
	 * starts URL test with id url_13
	 */
	@Test
	public void urlTestRun1(){
//		UnitTestRunner.runSingleTest("url_13");
		
		final String url = "http://ieeexplore.ieee.org/xpl/articleDetails.jsp?tp=&arnumber=6136685&contentType=Conference+Publications&searchField%3DSearch_All%26queryText%3DEnergy+efficient+hierarchical+epidemics+in+peer-to-peer+systems";
		final String selection = null;
		final Class<? extends Scraper> scraperClass = org.bibsonomy.scraper.url.kde.ieee.IEEEXploreJournalProceedingsScraper.class;
		final String resultFile = "IEEEXploreJournalProceedingsScraperUnitURLTest1.bib";
		RemoteTestAssert.assertScraperResult(url, selection, scraperClass, resultFile);
	}

	/**
	 * starts URL test with id url_127
	 */
	@Test
	public void urlTestRun2(){
//		UnitTestRunner.runSingleTest("url_127");
		
		final String url = "http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=4536262";
		final String selection = null;
		final Class<? extends Scraper> scraperClass = org.bibsonomy.scraper.url.kde.ieee.IEEEXploreJournalProceedingsScraper.class;
		final String resultFile = "IEEEXploreJournalProceedingsScraperUnitURLTest2.bib";
		RemoteTestAssert.assertScraperResult(url, selection, scraperClass, resultFile);
	}
}
