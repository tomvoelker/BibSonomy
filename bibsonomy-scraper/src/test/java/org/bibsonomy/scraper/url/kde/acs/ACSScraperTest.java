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
package org.bibsonomy.scraper.url.kde.acs;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

/**
 * Scraper URL tests #63 & #64 for DBLPScraper
 * @author wbi
 *
 */
@Category(RemoteTest.class)
public class ACSScraperTest {
	
	/**
	 * starts URL test with id url_63
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://pubs.acs.org/doi/abs/10.1021/ci049894n";
		final String resultFile = "ACSScraperUnitURLTest.bib";
		assertScraperResult(url, null, ACSScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_64
	 */
	@Test
	public void url2TestRun(){
		final String url = "http://pubs.acs.org/action/downloadCitation?doi=10.1021%2Fci049894n&amp;include=abs&amp;format=bibtex";
		final String resultFile = "ACSScraperUnitURLTest.bib";
		assertScraperResult(url, null, ACSScraper.class, resultFile);
	}
	/**
	 * starts URL test with id url_262
	 */
	@Test
	public void url3TestRun(){
		final String url = "http://pubs.acs.org/doi/pdf/10.1021/nn103618d";
		final String resultFile = "ACSScraperUnitURLTest3.bib";
		assertScraperResult(url, null, ACSScraper.class, resultFile);
	}
	
}
