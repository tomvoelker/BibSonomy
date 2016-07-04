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
package org.bibsonomy.scraper.url.kde.arxiv;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #10 #126 #129 for ArxivScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class ArxivScraperTest {
	
	/**
	 * starts URL test with id url_10
	 */
	@Test
	public void urlTestRun1(){
		UnitTestRunner.runSingleTest("url_10");
	}
	
	/**
	 * starts URL test with id url_126
	 */
	@Test
	public void urlTestRun2(){
		UnitTestRunner.runSingleTest("url_126");
	}
	
	/**
	 * starts URL test with id url_129
	 */
	@Test
	public void urlTestRun3(){
		UnitTestRunner.runSingleTest("url_129");
	}
	
	/**
	 * starts URL test with id url_133
	 */
	@Test
	public void urlTestRun4(){
		UnitTestRunner.runSingleTest("url_133");
	}
	
	/**
	 * starts URL test with id url_339
	 */
	@Test
	public void urlTestRun5() {
		UnitTestRunner.runSingleTest("url_339");
	}
}
