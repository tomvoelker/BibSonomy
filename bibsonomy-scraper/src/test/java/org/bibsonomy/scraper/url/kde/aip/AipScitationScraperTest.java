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
package org.bibsonomy.scraper.url.kde.aip;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #8 #9 for AipScitationScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class AipScitationScraperTest {
	
	/**
	 * starts URL test with id url_8
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://aip.scitation.org/doi/abs/10.1063/1.3680558";
		final String resultFile = "AipScitationScraperUnitURLTest1.bib";
		assertScraperResult(url, null, AipScitationScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_9
	 */
	@Test
	public void url2TestRun(){
		final String url = "http://aip.scitation.org/doi/full/10.1063/1.4820139";
		final String resultFile = "AipScitationScraperUnitURLTest2.bib";
		assertScraperResult(url, null, AipScitationScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_174
	 */
	@Test
	public void url3TestRun(){
		final String url = "http://asa.scitation.org/doi/abs/10.1121/1.2144160";
		final String resultFile = "AipScitationScraperUnitURLTest3.bib";
		assertScraperResult(url, null, AipScitationScraper.class, resultFile);
	}

}
