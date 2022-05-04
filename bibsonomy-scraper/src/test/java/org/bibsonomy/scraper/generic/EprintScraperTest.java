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
package org.bibsonomy.scraper.generic;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Scraper url tests #147, #148, #149, #150 for EprintScraper
 * 
 * @author tst
 */
@Category(RemoteTest.class)
public class EprintScraperTest {
	String resultDirectory = "eprint/";
	
	/**
	 * starts URL test with id url_147
	 */
	@Test
	public void url1TestRun1(){
		final String url = "http://orca.cf.ac.uk/5213/";
		final String resultFile = resultDirectory + "EprintScraperUnitURLTest1.bib";
		assertScraperResult(url, EprintScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_148
	 */
	@Test
	public void url1TestRun2(){
		final String url = "http://eprints.bbk.ac.uk/442/";
		final String resultFile = resultDirectory + "EprintScraperUnitURLTest2.bib";
		assertScraperResult(url, EprintScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_149
	 */
	@Test
	public void url1TestRun3(){
		final String url = "http://eprints.bbk.ac.uk/589/";
		final String resultFile = resultDirectory + "EprintScraperUnitURLTest3.bib";
		assertScraperResult(url, EprintScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_150
	 */
	@Test
	public void url1TestRun4(){
		final String url = "http://orca.cf.ac.uk/2657/";
		final String resultFile = resultDirectory + "EprintScraperUnitURLTest4.bib";
		assertScraperResult(url, EprintScraper.class, resultFile);
	}
}
