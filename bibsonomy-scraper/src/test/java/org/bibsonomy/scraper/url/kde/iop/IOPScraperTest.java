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
package org.bibsonomy.scraper.url.kde.iop;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #16, #297 for IOPScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class IOPScraperTest {
	String resultDirectory = "iop/";
	
	/*
	 * starts URL test with id url_16
	 */
	@Test
	public void urlTestRun(){
		final String url = "https://iopscience.iop.org/article/10.1088/1742-5468/2008/10/P10008#artAbst";
		final String resultFile = resultDirectory + "IOPScraperUnitURLTest1.bib";
		assertScraperResult(url, null, IOPScraper.class, resultFile);
	}
	
	/*
	 * starts URL test with id url_297
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://iopscience.iop.org/article/10.1088/1742-5468/2008/10/P10008/meta";
		final String resultFile = resultDirectory + "IOPScraperUnitURLTest1.bib";
		assertScraperResult(url, null, IOPScraper.class, resultFile);
	}
	
	/*
	 * starts URL test with id url_334
	 */
	@Test
	public void url2TestRun(){
		final String url = "http://iopscience.iop.org/article/10.1088/2041-8205/730/1/L11";
		final String resultFile = resultDirectory + "IOPScraperUnitURLTest2.bib";
		assertScraperResult(url, null, IOPScraper.class, resultFile);
	}
	@Test
	public void url3TestRun(){
		final String url = "http://iopscience.iop.org/article/10.1088/1751-8121/aacaa3";
		final String resultFile = resultDirectory + "IOPScraperUnitURLTest3.bib";
		assertScraperResult(url, null, IOPScraper.class, resultFile);
	}
}
