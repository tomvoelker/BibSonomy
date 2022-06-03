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
package org.bibsonomy.scraper.url.kde.jeb;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for {@link JEBScraper}
 *
 * @author Johannes
 */
@Category(RemoteTest.class)
public class JEBScraperTest {
	String resultDirectory = "jeb/";

	@Test
	public void url1TestRun(){
		final String url = "https://journals.biologists.com/jeb/article/219/19/3137/15588/Ontogeny-of-learning-walks-and-the-acquisition-of";
		final String resultFile = resultDirectory + "JEBScraperUnitURLTest1.bib";
		assertScraperResult(url, null, JEBScraper.class, resultFile);
	}
	
	@Test
	public void url2TestRun(){
		final String url = "https://journals.biologists.com/jeb/article/220/12/2236/34091/Interactive-effects-of-oxygen-carbon-dioxide-and";
		final String resultFile = resultDirectory + "JEBScraperUnitURLTest2.bib";
		assertScraperResult(url, null, JEBScraper.class, resultFile);
	}

}
