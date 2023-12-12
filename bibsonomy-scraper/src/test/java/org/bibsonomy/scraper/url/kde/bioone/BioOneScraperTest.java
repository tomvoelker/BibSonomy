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
package org.bibsonomy.scraper.url.kde.bioone;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Mohammed Abed
 */
@Category(RemoteTest.class)
public class BioOneScraperTest {
	String resultDirectory = "bioone/";

	@Test
	public void url1TestRun() {
		final String url = "http://www.bioone.org/doi/abs/10.2113/gspalynol.32.1.1";
		final String resultFile = resultDirectory + "BioOneScraperScraperUnitURLTest1.bib";
		assertScraperResult(url, BioOneScraper.class, resultFile);
	}

	@Test
	public void url2TestRun() {
		final String url = "https://bioone.org/journals/african-journal-of-wildlife-research/volume-51/issue-1/056.051.0136/Emerging-HumanCarnivore-Conflict-Following-Large-Carnivore-Reintroductions-Highlights-the-Need/10.3957/056.051.0136.short";
		final String resultFile = resultDirectory + "BioOneScraperScraperUnitURLTest2.bib";
		assertScraperResult(url, BioOneScraper.class, resultFile);
	}

	@Test
	public void url3TestRun() {
		final String url = "https://bioone.org/journals/ambio-a-journal-of-the-human-environment/volume-31/issue-2/0044-7447-31.2.64/Reactive-Nitrogen-and-The-World-200-Years-of-Change/10.1579/0044-7447-31.2.64.short?tab=ArticleLinkCited";
		final String resultFile = resultDirectory + "BioOneScraperScraperUnitURLTest3.bib";
		assertScraperResult(url, BioOneScraper.class, resultFile);
	}

	@Test
	public void url4TestRun() {
		final String url = "https://bioone.org/journals/canadian-journal-of-soil-science/volume-101/issue-1/cjss-2019-0136/Species-specific-responses-to-targeted-fertilizer-application-on-reconstructed-soils/10.1139/cjss-2019-0136.full?tab=ArticleLinkReference";
		final String resultFile = resultDirectory + "BioOneScraperScraperUnitURLTest4.bib";
		assertScraperResult(url, BioOneScraper.class, resultFile);
	}
}
