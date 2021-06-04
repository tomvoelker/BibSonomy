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
package org.bibsonomy.scraper.url.kde.iop;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #16, #297 for IOPScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class IOPScraperTest {
	
	/*
	 * starts URL test with id url_16
	 */
	@Test
	public void urlTestRun(){
		assertScraperResult("http://iopscience.iop.org/article/10.1088/1742-5468/2008/10/P10008", IOPScraper.class, "IOPScraperUnitURLTest1.bib");
	}
	
	/*
	 * starts URL test with id url_297
	 */
	@Test
	public void url1TestRun(){
		assertScraperResult("http://iopscience.iop.org/article/10.1088/1742-5468/2008/10/P10008/meta", IOPScraper.class, "IOPScraperUnitURLTest1.bib");
	}
	
	/*
	 * starts URL test with id url_334
	 */
	@Test
	public void url2TestRun(){
		assertScraperResult("http://iopscience.iop.org/article/10.1088/2041-8205/730/1/L11", IOPScraper.class, "IOPScraperUnitURLTest2.bib");
	}
}
