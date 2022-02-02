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
package org.bibsonomy.scraper.url.kde.thelancet;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 *
 * @author Haile
 */
@Category(RemoteTest.class)
public class TheLancetScraperTest {
	String resultDirectory = "thelancet/";
	/**
	 * url_278
	 */
	@Test
	public void urlTest1Run(){
		final String url = "https://www.thelancet.com/journals/lancet/article/PIIS0140-6736(14)60931-4/fulltext";
		final String resultFile = resultDirectory + "TheLancetScraperUnitURLTest1.bib";
		assertScraperResult(url, null, TheLancetScraper.class, resultFile);
	}

	@Test
	public void urlTest2Run(){
		final String url = "https://www.thelancet.com/journals/lancet/article/PIIS0140-6736(21)02344-8/fulltext";
		final String resultFile = resultDirectory + "TheLancetScraperUnitURLTest2.bib";
		assertScraperResult(url, null, TheLancetScraper.class, resultFile);
	}

	@Test
	public void urlTest3Run(){
		final String url = "https://www.thelancet.com/journals/lancet/article/PIIS0140-6736(21)02728-8/fulltext#section-7c530872-6235-4433-899c-b3f276970189";
		final String resultFile = resultDirectory + "TheLancetScraperUnitURLTest3.bib";
		assertScraperResult(url, null, TheLancetScraper.class, resultFile);
	}

	@Test
	public void urlTest4Run(){
		final String url = "https://www.thelancet.com/journals/eclinm/article/PIIS2589-5370(21)00467-3/fulltext#seccesectitle0010";
		final String resultFile = resultDirectory + "TheLancetScraperUnitURLTest4.bib";
		assertScraperResult(url, null, TheLancetScraper.class, resultFile);
	}
}
