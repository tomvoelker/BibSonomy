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

import org.bibsonomy.scraper.junit.RemoteTest;
import org.bibsonomy.scraper.junit.RemoteTestAssert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for IEEEXploreJournalProceedingsScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class IEEEXploreJournalProceedingsScraperTest {
	String resultDirectory = "ieee/xplore/journalproceedings/";
	
	/**
	 * starts URL test with id url_13
	 */
	@Test
	public void urlTestRun1() {
		final String url = "http://ieeexplore.ieee.org/document/6136685/?tp=&arnumber=6136685&contentType=Conference%20Publications&searchField%3DSearch_All%26queryText%3DEnergy%20efficient%20hierarchical%20epidemics%20in%20peer-to-peer%20systems";
		final String resultFile = resultDirectory + "IEEEXploreJournalProceedingsScraperUnitURLTest1.bib";
		assertScraperResult(url, IEEEXploreJournalProceedingsScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_127
	 */
	@Test
	public void urlTestRun2() {
		final String url = "http://ieeexplore.ieee.org/document/4536262/?arnumber=4536262";
		final String resultFile = resultDirectory + "IEEEXploreJournalProceedingsScraperUnitURLTest2.bib";
		assertScraperResult(url, IEEEXploreJournalProceedingsScraper.class, resultFile);
	}
	
	/**
	 * starts URL test 3
	 */
	@Test
	public void urlTestRun3() {
		final String url = "https://ieeexplore.ieee.org/document/6189346";
		final String resultFile = resultDirectory + "IEEEXploreJournalProceedingsScraperUnitURLTest3.bib";
		assertScraperResult(url, IEEEXploreJournalProceedingsScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_157
	 */
	@Test
	public void urlTestRun4() {
		final String url = "https://ieeexplore.ieee.org/document/5286085";
		final String resultFile = resultDirectory + "IEEEXploreJournalProceedingsScraperUnitURLTest4.bib";
		assertScraperResult(url, IEEEXploreJournalProceedingsScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_158
	 */
	@Test
	public void urlTestRun5() {
		final String url = "https://ieeexplore.ieee.org/document/4383076";
		final String resultFile = resultDirectory + "IEEEXploreJournalProceedingsScraperUnitURLTest5.bib";
		assertScraperResult(url, IEEEXploreJournalProceedingsScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_14
	 */
	@Test
	public void urlTestRun() {
		final String url = "https://ieeexplore.ieee.org/document/982216?tp=&isnumber=21156&arnumber=982216&punumber=7718";
		final String resultFile = resultDirectory + "IEEEXploreJournalProceedingsScraperUnitURLTest6.bib";
		RemoteTestAssert.assertScraperResult(url, IEEEXploreJournalProceedingsScraper.class, resultFile);
	}
}
