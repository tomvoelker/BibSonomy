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
package org.bibsonomy.scraper.url.kde.spiedigitallibrary;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Mohammed abed
 */
@Category(RemoteTest.class)
public class SpieDigitalLibraryScraperTest {
	String resultDirectory = "spiedigitallibary/";
	/**
	 * starts URL test with id url_350
	 */
	@Test
	public void url1TestRun(){
		final String url = "https://www.spiedigitallibrary.org/journals/journal-of-applied-remote-sensing/volume-9/issue-01/097099/Economic-impacts-of-climate-change-on-agriculture--the-AgMIP/10.1117/1.JRS.9.097099.full?SSO=1";
		final String resultFile = resultDirectory + "SpieDigitalLibraryScraperUnitURLTest1.bib";
		assertScraperResult(url, SpieDigitalLibraryScraper.class, resultFile);
	}
	@Test
	public void url2TestRun(){
		final String url = "https://www.spiedigitallibrary.org/journals/journal-of-biomedical-optics/volume-26/issue-04/043001/Special-Section-Guest-Editorial--Advances-in-Terahertz-Biomedical-Science/10.1117/1.JBO.26.4.043001.full";
		final String resultFile = resultDirectory + "SpieDigitalLibraryScraperUnitURLTest2.bib";
		assertScraperResult(url, SpieDigitalLibraryScraper.class, resultFile);
	}
	@Test
	public void url3Test1Run(){
		final String url = "https://www.spiedigitallibrary.org/journals/optical-engineering/volume-61/issue-3/031205/Effect-of-humidity-on-the-performance-of-Al-LiF-eMgF2/10.1117/1.OE.61.3.031205.short";
		final String resultFile = resultDirectory + "SpieDigitalLibraryScraperUnitURLTest3.bib";
		assertScraperResult(url, SpieDigitalLibraryScraper.class, resultFile);
	}
	@Test
	public void url3Test2Run(){
		final String url = "https://www.spiedigitallibrary.org/journals/optical-engineering/volume-61/issue-3/031205/Effect-of-humidity-on-the-performance-of-Al-LiF-eMgF2/10.1117/1.OE.61.3.031205.short?tab=ArticleLinkReference";
		final String resultFile = resultDirectory + "SpieDigitalLibraryScraperUnitURLTest3.bib";
		assertScraperResult(url, SpieDigitalLibraryScraper.class, resultFile);
	}
}
