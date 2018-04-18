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
package org.bibsonomy.scraper.url.kde.springer;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #58 for SpringerScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class SpringerScraperTest {
	
	/**
	 * starts URL test with id url_58
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://www.springer.com/computer/database+management+&amp;+information+retrieval/book/978-0-387-95433-2";
		final String resultFile = "SpringerScraperUnitURLTest.bib";
		assertScraperResult(url, null, SpringerScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_239
	 */
	@Test
	public void url8TestRun() {
		final String url = "http://link.springer.com/book/10.1007/978-0-387-85820-3/page/1";
		final String resultFile = "SpringerLinkScraperUnitURLTest9.bib";
		assertScraperResult(url, null, SpringerScraper.class, resultFile);
	}
}
