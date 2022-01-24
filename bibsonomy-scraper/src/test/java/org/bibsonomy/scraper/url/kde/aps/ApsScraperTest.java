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
package org.bibsonomy.scraper.url.kde.aps;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Haile
 */
@Category(RemoteTest.class)
public class ApsScraperTest {
	String resultDirectory = "aps/";

	/**
	 * runs test with url 255  for {@link ApsScraper}
	 */
	@Test
	public void url1Test1Run(){
		final String url = "https://journals.physiology.org/doi/full/10.1152/physrev.00032.2010";
		final String resultFile = resultDirectory + "ApsScraperUnitURLTest1.bib";
		assertScraperResult(url, ApsScraper.class, resultFile);
	}

	@Test
	public void url2Test1Run(){
		final String url = "https://journals.physiology.org/doi/full/10.1152/physrev.00033.2021";
		final String resultFile = resultDirectory + "ApsScraperUnitURLTest2.bib";
		assertScraperResult(url, ApsScraper.class, resultFile);
	}

	@Test
	public void url3Test1Run(){
		final String url = "https://journals.physiology.org/doi/full/10.1152/physrev.00011.2010";
		final String resultFile = resultDirectory + "ApsScraperUnitURLTest3.bib";
		assertScraperResult(url, ApsScraper.class, resultFile);
	}

}
