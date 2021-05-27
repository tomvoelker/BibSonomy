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
package org.bibsonomy.scraper.url.kde.karlsruhe;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #2 #3 #4 #5 #6 #7 #72 for AIFBScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class AIFBScraperTest {
	
	/**
	 * starts URL test with id url_2
	 */
	@Test
	public void url1TestRun() {
		assertScraperResult("https://www.aifb.kit.edu/web/Article1764", AIFBScraper.class, "AIFBScraperUnitURLTest1.bib");
	}
	
	/**
	 * starts URL test with id url_3
	 */
	@Test
	public void url2TestRun() {
		assertScraperResult("https://www.aifb.kit.edu/web/Inproceedings867", AIFBScraper.class, "AIFBScraperUnitURLTest2.bib");
	}
	
	/**
	 * starts URL test with id url_4
	 */
	@Test
	public void url3TestRun() {
		assertScraperResult("https://www.aifb.kit.edu/web/Book2015", AIFBScraper.class, "AIFBScraperUnitURLTest3.bib");
	}
	
	/**
	 * starts URL test with id url_5
	 */
	@Test
	public void url4TestRun(){
		assertScraperResult("https://www.aifb.kit.edu/web/Incollection2044", AIFBScraper.class, "AIFBScraperUnitURLTest4.bib");
	}
	
	/**
	 * starts URL test with id url_6
	 */
	@Test
	public void url5TestRun(){
		assertScraperResult("https//www.aifb.kit.edu/web/Phdthesis74", AIFBScraper.class, "AIFBScraperUnitURLTest5.bib");
	}
	
	/**
	 * starts URL test with id url_7
	 */
	@Test
	public void url6TestRun() {
		assertScraperResult("https://www.aifb.kit.edu/web/Techreport2020", AIFBScraper.class, "AIFBScraperUnitURLTest6.bib");
	}
}
