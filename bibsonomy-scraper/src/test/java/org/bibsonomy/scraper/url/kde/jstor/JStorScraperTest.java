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
package org.bibsonomy.scraper.url.kde.jstor;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for JStorScraper
 * @author wbi
 */
@Category(RemoteTest.class)
public class JStorScraperTest {
	String resultDirectory = "jstor/";
	
	/**
	 * starts URL test with id url_97
	 */
	@Test
	public void urlTest1Run() {
		final String url = "http://www.jstor.org/stable/4142852";
		final String resultFile = resultDirectory + "JStorScraperUnitURLTest.bib";
		assertScraperResult(url, null, JStorScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_98
	 */
	@Test
	public void urlTest2Run() {
		final String url = "http://www.jstor.org/stable/j.ctt7zv8mk?Search=yes&resultItemClick=true&searchText=clustering&searchUri=%2Faction%2FdoBasicSearch%3FQuery%3Dclustering%26amp%3Bacc%3Doff%26amp%3Bwc%3Don%26amp%3Bfc%3Doff%26amp%3Bgroup%3Dnone";
		final String resultFile = resultDirectory + "JStorScraperUnitURLTest1.bib";
		assertScraperResult(url, null, JStorScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_222
	 */
	@Test
	public void urlTest3Run() {
		final String url = "http://www.jstor.org/stable/20569359";
		final String resultFile = resultDirectory + "JStorScraperUnitURLTest2.bib";
		assertScraperResult(url, null, JStorScraper.class, resultFile);
	}
	
	@Test
	public void urlTest4Run() {
		final String url = "http://www.jstor.org/stable/20015480";
		final String resultFile = resultDirectory + "JStorScraperUnitURLTest3.bib";
		assertScraperResult(url, null, JStorScraper.class, resultFile);
	}
	
	@Test
	public void urlTest5Run() {
		final String url = "http://www.jstor.org/stable/484241";
		final String resultFile = resultDirectory + "JStorScraperUnitURLTest4.bib";
		assertScraperResult(url, null, JStorScraper.class, resultFile);
	}
}
