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
package org.bibsonomy.scraper.url.kde.jstor;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.bibsonomy.scraper.url.kde.biorxiv.BioRxivScraper;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #97 & #98 for JStorScraper
 * @author wbi
 */
@Category(RemoteTest.class)
public class JStorScraperTest {
	
	/**
	 * starts URL test with id url_97
	 */
	@Test
	public void urlTest1Run(){
//		UnitTestRunner.runSingleTest("url_97");
		final String url = "http://www.jstor.org/stable/4142852";
		final String resultFile = "JStorScraperUnitURLTest.bib";
		assertScraperResult(url, null, JStorScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_98
	 */
	@Test
	public void urlTest2Run(){
//		UnitTestRunner.runSingleTest("url_98");
		final String url = "http://www.jstor.org/stable/j.ctt7zv8mk?Search=yes&resultItemClick=true&searchText=clustering&searchUri=%2Faction%2FdoBasicSearch%3FQuery%3Dclustering%26amp%3Bacc%3Doff%26amp%3Bwc%3Don%26amp%3Bfc%3Doff%26amp%3Bgroup%3Dnone";
		final String resultFile = "JStorScraperUnitURLTest1.bib";
		assertScraperResult(url, null, JStorScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_222
	 */
	@Test
	public void urlTest3Run(){
//		UnitTestRunner.runSingleTest("url_222");
		final String url = "http://www.jstor.org/stable/20569359?seq=1#page_scan_tab_contents";
		final String resultFile = "JStorScraperUnitURLTest2.bib";
		assertScraperResult(url, null, JStorScraper.class, resultFile);
	}
}
