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

@Category(RemoteTest.class)
public class IEEEComputerSocietyProceedingScraperTest {
	String resultDirectory = "ieee/computersociety/proceeding/";

	@Test
	public void url1TestRun() {
		final String url = "https://www.computer.org/csdl/proceedings-article/dexa/2006/26410603/12OmNxFJXTp";
		final String resultFile = resultDirectory + "IEEEComputerSocietyScraperUnitURLTest1.bib";
		assertScraperResult(url, IEEEComputerSocietyProceedingScraper.class, resultFile);
	}

	@Test
	public void url2TestRun() {
		final String url = "https://www.computer.org/csdl/proceedings-article/acomp/2019/472300a016/1ivu5t889ZS";
		final String resultFile = resultDirectory + "IEEEComputerSocietyScraperUnitURLTest2.bib";
		assertScraperResult(url, IEEEComputerSocietyProceedingScraper.class, resultFile);
	}

	@Test
	public void url3TestRun() {
		final String url = "https://www.computer.org/csdl/proceedings-article/afips/1982/50890081/12OmNqJ8tsQ";
		final String resultFile = resultDirectory + "IEEEComputerSocietyScraperUnitURLTest3.bib";
		assertScraperResult(url, IEEEComputerSocietyProceedingScraper.class, resultFile);
	}

	@Test
	public void url4TestRun() {
		final String url = "https://www.computer.org/csdl/proceedings-article/apwc-on-cse/2014/07053838/12OmNwIHorc";
		final String resultFile = resultDirectory + "IEEEComputerSocietyScraperUnitURLTest4.bib";
		assertScraperResult(url, IEEEComputerSocietyProceedingScraper.class, resultFile);
	}
}
