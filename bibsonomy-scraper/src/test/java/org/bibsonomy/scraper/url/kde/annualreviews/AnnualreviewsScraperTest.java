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
package org.bibsonomy.scraper.url.kde.annualreviews;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #106 #107 for AnnualreviewsScraper
 * @author tst
 */
@Category(RemoteTest.class)
public class AnnualreviewsScraperTest {
	
	/**
	 * starts URL test with id url_106
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://www.annualreviews.org/doi/abs/10.1146/annurev.bioeng.6.040803.140250";
		final String resultFile = "AnnualreviewsScraperUnitURLTest1.bib";
		assertScraperResult(url, null, AnnualreviewsScraper.class, resultFile);
	}
	

	/**
	 * url_107
	 */
	@Test
	public void url2TestRun(){
		final String url = "http://www.annualreviews.org/action/showCitFormats?doi=10.1146/annurev.bioeng.6.040803.140250";
		final String resultFile = "AnnualreviewsScraperUnitURLTest1.bib";
		assertScraperResult(url, null, AnnualreviewsScraper.class, resultFile);
	}
	
	
	/**
	 * url_228
	 */
	@Test
	public void url3TestRun(){
		final String url = "http://www.annualreviews.org/doi/full/10.1146/annurev-bioeng-071811-150124";
		final String resultFile = "AnnualreviewsScraperUnitURLTest3.bib";
		assertScraperResult(url, null, AnnualreviewsScraper.class, resultFile);
	}
	
}
