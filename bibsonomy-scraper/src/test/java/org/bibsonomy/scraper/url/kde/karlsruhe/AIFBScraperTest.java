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
package org.bibsonomy.scraper.url.kde.karlsruhe;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #2 #3 #4 #5 #6 #7 #72 for AIFBScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class AIFBScraperTest {
	String resultDirectory = "karlsruhe/aifb/";
	
	/**
	 * starts URL test with id url_2
	 */
	@Test
	public void url1TestRun() {
		final String url = "https://www.aifb.kit.edu/web/Article1764";
		final String resultFile = resultDirectory + "AIFBScraperUnitURLTest1.bib";
		assertScraperResult(url, null, AIFBScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_3
	 */
	@Test
	public void url2TestRun() {
		final String url = "https://www.aifb.kit.edu/web/Inproceedings867";
		final String resultFile = resultDirectory + "AIFBScraperUnitURLTest2.bib";
		assertScraperResult(url, null, AIFBScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_4
	 */
	@Test
	public void url3TestRun() {
		final String url = "https://www.aifb.kit.edu/web/Book2015";
		final String resultFile = resultDirectory + "AIFBScraperUnitURLTest3.bib";
		assertScraperResult(url, null, AIFBScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_5
	 */
	@Test
	public void url4TestRun(){
		final String url = "https://www.aifb.kit.edu/web/Incollection2044";
		final String resultFile = resultDirectory + "AIFBScraperUnitURLTest4.bib";
		assertScraperResult(url, null, AIFBScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_6
	 */
	@Test
	public void url5TestRun(){
		final String url = "https://www.aifb.kit.edu/web/Phdthesis74";
		final String resultFile = resultDirectory + "AIFBScraperUnitURLTest5.bib";
		assertScraperResult(url, null, AIFBScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_7
	 */
	@Test
	public void url6TestRun() {
		final String url = "https://www.aifb.kit.edu/web/Techreport2020";
		final String resultFile = resultDirectory + "AIFBScraperUnitURLTest6.bib";
		assertScraperResult(url, null, AIFBScraper.class, resultFile);
	}
}
