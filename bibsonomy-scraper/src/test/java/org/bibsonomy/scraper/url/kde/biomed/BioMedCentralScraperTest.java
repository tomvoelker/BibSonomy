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
package org.bibsonomy.scraper.url.kde.biomed;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #58 & #59 for DBLPScraper
 * @author wbi
 *
 */
@Category(RemoteTest.class)
public class BioMedCentralScraperTest {
	
	/**
	 * starts URL test with id url_61
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://www.biomedcentral.com/1471-2326/6/7";
		final String resultFile = "BioMedCentralScraperUnitURLTest.bib";
		assertScraperResult(url, null, BioMedCentralScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_62
	 */
	@Test
	public void url2TestRun(){
		final String url = "http://www.biomedcentral.com/1471-2326/6/7/citation";
		final String resultFile = "BioMedCentralScraperUnitURLTest.bib";
		assertScraperResult(url, null, BioMedCentralScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_343
	 */
	@Test
	public void url3TestRun(){
		final String url = "http://jbiomedsem.biomedcentral.com/articles/10.1186/2041-1480-1-S1-S6";
		final String resultFile = "BioMedCentralScraperUnitURLTest2.bib";
		assertScraperResult(url, null, BioMedCentralScraper.class, resultFile);
	}
}
