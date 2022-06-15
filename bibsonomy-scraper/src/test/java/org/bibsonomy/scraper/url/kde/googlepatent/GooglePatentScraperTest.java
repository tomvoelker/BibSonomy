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
package org.bibsonomy.scraper.url.kde.googlepatent;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Mohammed Abed
 */
@Category(RemoteTest.class)
public class GooglePatentScraperTest {
	String resultDirectory = "googlepatent/";
	/**
	 * starts URL test with id url_336
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://www.google.com/patents/DE102009031804A1?cl=de";
		final String resultFile = resultDirectory + "GooglePatentScraperUnitURLTest1.bib";
		assertScraperResult(url, null, GooglePatentScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_337
	 */
	@Test
	public void url2TestRun(){
		final String url = "http://www.google.com/patents/DE102009031804A1?cl=en&hl=de";
		final String resultFile = resultDirectory + "GooglePatentScraperUnitURLTest2.bib";
		assertScraperResult(url, null, GooglePatentScraper.class, resultFile);
	}

	@Test
	public void url3TestRun(){
		final String url = "https://patents.google.com/patent/DE102009031804A1/en";
		final String resultFile = resultDirectory + "GooglePatentScraperUnitURLTest2.bib";
		assertScraperResult(url, null, GooglePatentScraper.class, resultFile);
	}
}
