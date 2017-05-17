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
package org.bibsonomy.scraper.url.kde.aaai;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author hagen
 *
 */
@Category(RemoteTest.class)
public class AAAIScraperTest {
	/**
	 * starts URL test with id url_237
	 */
	@Test
	public void url1TestRun(){
		final String url = "https://www.aaai.org/ocs/index.php/ICWSM/ICWSM11/paper/view/3856";
		final String resultFile = "AAAIScraperUnitURLTest1.bib";
		assertScraperResult(url, null, AAAIScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_240
	 */
	@Test
	public void url2TestRun(){
		final String url = "https://www.aaai.org/ojs/index.php/aimagazine/article/view/2376";
		final String resultFile = "AAAIScraperUnitURLTest2.bib";
		assertScraperResult(url, null, AAAIScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_296
	 */
	@Test
	public void url3TestRun(){
		final String url = "https://www.aaai.org/ocs/index.php/IAAI/IAAI14/paper/view/8607";
		final String resultFile = "AAAIScraperUnitURLTest3.bib";
		assertScraperResult(url, null, AAAIScraper.class, resultFile);
	}
}
