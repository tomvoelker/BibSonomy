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
package org.bibsonomy.scraper.url.kde.iwap;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #100 for IWAPonlineScraper
 * @author tst
 */
@Category(RemoteTest.class)
public class IWAPonlineScraperTest {
	String resultDirectory = "iwap/";


	@Test
	public void url1TestRun(){
		final String url = "https://iwaponline.com/jwh/article/1/3/101/1793/The-effect-of-container-biofilm-on-the";
		final String resultFile = resultDirectory + "IWAPonlineScraperUnitURLTest1.bib";
		assertScraperResult(url, null, IWAPonlineScraper.class, resultFile);
	}

	@Test
	public void url2TestRun(){
		final String url = "https://iwaponline.com/jwh/article/19/5/872/84208/Identifying-challenges-in-drinking-water-supplies";
		final String resultFile = resultDirectory + "IWAPonlineScraperUnitURLTest2.bib";
		assertScraperResult(url, null, IWAPonlineScraper.class, resultFile);
	}

	@Test
	public void url3TestRun(){
		final String url = "https://iwaponline.com/jwh/article/doi/10.2166/wh.2021.171/84531/Short-term-exposure-to-benzalkonium-chloride-in";
		final String resultFile = resultDirectory + "IWAPonlineScraperUnitURLTest3.bib";
		assertScraperResult(url, null, IWAPonlineScraper.class, resultFile);
	}

	@Test
	public void url4TestRun(){
		final String url = "https://iwaponline.com/jwh/article/doi/10.2166/wh.2021.140/84676/Determinants-of-hand-hygiene-practices-in-India";
		final String resultFile = resultDirectory + "IWAPonlineScraperUnitURLTest4.bib";
		assertScraperResult(url, null, IWAPonlineScraper.class, resultFile);
	}
	
}
