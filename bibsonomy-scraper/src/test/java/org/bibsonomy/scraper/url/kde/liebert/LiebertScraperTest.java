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
package org.bibsonomy.scraper.url.kde.liebert;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * TODO: add documentation to this class
 *
 * @author rja
 */
@Category(RemoteTest.class)
public class LiebertScraperTest {

	/**
	 * starts URL test with id url_88
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://www.liebertonline.com/doi/abs/10.1089/152308604773934350";
		final String resultFile = "LiebertScraperUnitURLTest1.bib";
		assertScraperResult(url, null, LiebertScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_89
	 */
	@Test
	public void url2TestRun(){
		final String url = "http://www.liebertonline.com/action/showCitFormats?doi=10.1089%2F152308604773934350";
		final String resultFile = "LiebertScraperUnitURLTest3.bib";
		assertScraperResult(url, null, LiebertScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_248
	 */
	@Test
	public void url3TestRun(){
		final String url = "http://online.liebertpub.com/doi/abs/10.1089/teb.2007.0150";
		final String resultFile = "LiebertScraperUnitURLTest2.bib";
		assertScraperResult(url, null, LiebertScraper.class, resultFile);
	}

}
