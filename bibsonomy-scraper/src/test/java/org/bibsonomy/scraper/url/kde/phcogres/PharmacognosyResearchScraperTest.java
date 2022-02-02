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
package org.bibsonomy.scraper.url.kde.phcogres;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author hagen
 *
 */
@Category(RemoteTest.class)
public class PharmacognosyResearchScraperTest {
	String resultDirectory = "pharmacognosyresearch/";
	
	/**
	 * starts URL test with id url_251
	 */
	@Test
	public void url1TestRun(){
		final String url = "https://www.phcogres.com/article/2009/1/4/nil-1";
		final String resultFile = resultDirectory + "PharmacognosyResearchScraperUnitURLTest1.bib";
		assertScraperResult(url, null, PharmacognosyResearchScraper.class, resultFile);
	}

	@Test
	public void url2TestRun(){
		final String url = "https://www.phcogres.com/article/2021/13/4/105530pres13410";
		final String resultFile = resultDirectory + "PharmacognosyResearchScraperUnitURLTest2.bib";
		assertScraperResult(url, null, PharmacognosyResearchScraper.class, resultFile);
	}

	@Test
	public void url3TestRun(){
		final String url = "https://phcogres.com/article/2016/8/5/1041030974-8490178646";
		final String resultFile = resultDirectory + "PharmacognosyResearchScraperUnitURLTest3.bib";
		assertScraperResult(url, null, PharmacognosyResearchScraper.class, resultFile);
	}

	@Test
	public void url4TestRun(){
		final String url = "https://phcogres.com/article/2009/1/6/nil-3";
		final String resultFile = resultDirectory + "PharmacognosyResearchScraperUnitURLTest4.bib";
		assertScraperResult(url, null, PharmacognosyResearchScraper.class, resultFile);
	}
}
