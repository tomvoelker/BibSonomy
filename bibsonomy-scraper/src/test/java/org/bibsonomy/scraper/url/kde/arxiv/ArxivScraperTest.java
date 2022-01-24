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
package org.bibsonomy.scraper.url.kde.arxiv;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #10 #126 #129 for ArxivScraper
 * @author tst
 */
@Category(RemoteTest.class)
public class ArxivScraperTest {
	String resultDirectory = "arxiv/";
	
	/**
	 * starts URL test with id url_10
	 */
	@Test
	public void urlTestRun1(){
		final String url = "https://arxiv.org/abs/0706.3639";
		final String resultFile = resultDirectory + "ArxivScraperUnitURLTest1.bib";
		assertScraperResult(url, ArxivScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_126
	 */
	@Test
	public void urlTestRun2(){
		final String url = "https://arxiv.org/abs/cond-mat/0508028";
		final String resultFile = resultDirectory + "ArxivScraperUnitURLTest2.bib";
		assertScraperResult(url, ArxivScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_129
	 */
	@Test
	public void urlTestRun3(){
		final String url = "https://arxiv.org/abs/0810.1951";
		final String resultFile = resultDirectory + "ArxivScraperUnitURLTest3.bib";
		assertScraperResult(url, ArxivScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_133
	 */
	@Test
	public void urlTestRun4(){
		final String url = "https://arxiv.org/abs/0805.2045";
		final String resultFile = resultDirectory + "ArxivScraperUnitURLTest4.bib";
		assertScraperResult(url, ArxivScraper.class, resultFile);
	}
}
