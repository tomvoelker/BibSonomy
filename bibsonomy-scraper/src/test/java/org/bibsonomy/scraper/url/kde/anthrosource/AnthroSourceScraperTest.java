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
package org.bibsonomy.scraper.url.kde.anthrosource;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class AnthroSourceScraperTest {
	String resultDirectory = "anthrosource/";
	@Test
	public void url1TestRun(){
		final String url = "https://anthrosource.onlinelibrary.wiley.com/doi/full/10.1111/aman.13671";
		final String resultFile = resultDirectory + "AnthroSourceScraperUnitURLTest1.bib";
		assertScraperResult(url, null, AnthroSourceScraper.class, resultFile);
	}

	@Test
	public void url2TestRun(){
		final String url = "https://anthrosource.onlinelibrary.wiley.com/doi/abs/10.1111/epic.12052";
		final String resultFile = resultDirectory + "AnthroSourceScraperUnitURLTest2.bib";
		assertScraperResult(url, null, AnthroSourceScraper.class, resultFile);
	}

	@Test
	public void url3TestRun(){
		final String url = "https://anthrosource.onlinelibrary.wiley.com/doi/abs/10.1002/j.sda2.20120301.0007";
		final String resultFile = resultDirectory + "AnthroSourceScraperUnitURLTest3.bib";
		assertScraperResult(url, null, AnthroSourceScraper.class, resultFile);
	}

	@Test
	public void url4TestRun(){
		final String url = "https://anthrosource.onlinelibrary.wiley.com/doi/abs/10.1525/sol.1992.14.3.27";
		final String resultFile = resultDirectory + "AnthroSourceScraperUnitURLTest4.bib";
		assertScraperResult(url, null, AnthroSourceScraper.class, resultFile);
	}
}
