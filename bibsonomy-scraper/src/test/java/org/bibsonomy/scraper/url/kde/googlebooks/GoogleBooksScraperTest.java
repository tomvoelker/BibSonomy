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
package org.bibsonomy.scraper.url.kde.googlebooks;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.bibsonomy.scraper.url.kde.elsevier.ElsevierScraper;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #207, #208 for GoogleBooksScraper
 * @author clemens
 */
@Category(RemoteTest.class)
public class GoogleBooksScraperTest {

	/**
	 * starts URL test with id url_207
	 */
	@Test
	public void urlTestRun1(){
//		UnitTestRunner.runSingleTest("url_207");
		final String url = "http://books.google.com/books?id=OhstAAAAYAAJ&amp;source=gbs_slider_gbs_user_shelves_1040_homepage";
		final String resultFile = "GoogleBooksScraperUnitURLTest1.bib";
		assertScraperResult(url, null, GoogleBooksScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_208
	 */
	@Test
	public void urlTestRun2(){
//		UnitTestRunner.runSingleTest("url_208");
		final String url = "http://books.google.com/books?id=zZ3CAAAACAAJ&dq=deutscher+dokumentartag+1991&hl=en&ei=g1m6Tr71KObY4QT4v_SWCA&sa=X&oi=book_result&ct=result&resnum=1&ved=0CCwQ6AEwAA";
		final String resultFile = "GoogleBooksScraperUnitURLTest2.bib";
		assertScraperResult(url, null, GoogleBooksScraper.class, resultFile);
	}
}
