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
package org.bibsonomy.scraper.url.kde.ieee;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for IEEEComputerSocietyScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class IEEEComputerSocietyScraperTest {
	
	/**
	 * starts URL test with id url_40
	 */
	@Test
	public void url1TestRun() {
		final String url = "https://www.computer.org/csdl/proceedings-article/dexa/2006/26410603/12OmNxFJXTp";
		final String resultFile = "IEEEComputerSocietyScraperUnitURLTest1.bib";
		assertScraperResult(url, IEEEComputerSocietyScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_137
	 */
	@Test
	public void url2TestRun() {
		final String url = "http://www.computer.org/csdl/proceedings/dexa/2006/2641/00/26410603-reference.bib";
		final String resultFile = "IEEEComputerSocietyScraperUnitURLTest2.bib";
		assertScraperResult(url, IEEEComputerSocietyScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_283
	 */
	@Test
	public void url3TestRun() {
		final String url = "http://www.computer.org/csdl/mags/co/2001/02/r2026.pdf";
		final String resultFile = "IEEEComputerSocietyScraperUnitURLTest3.bib";
		assertScraperResult(url, IEEEComputerSocietyScraper.class, resultFile);
	}
}
