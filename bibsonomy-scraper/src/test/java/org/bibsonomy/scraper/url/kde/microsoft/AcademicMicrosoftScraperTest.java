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
package org.bibsonomy.scraper.url.kde.microsoft;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class AcademicMicrosoftScraperTest {
	String resultDirectory = "microsoft/";

	@Test
	public void url1TestRun(){
		final String url = "https://academic.microsoft.com/paper/2109559765/reference/search?q=Academic%20publication%20management%20with%20PUMA%3A%20collect%2C%20organize%20and%20share%20publications&qe=Or(Id%253D1884402041)&f=&orderBy=0";
		final String resultFile = resultDirectory + "AcademicMicrosoftScraperUnitURLTest1.bib";
		assertScraperResult(url, null, AcademicMicrosoftScraper.class, resultFile);
	}

	@Test
	public void url2TestRun(){
		final String url = "https://academic.microsoft.com/paper/2165065735/reference/search?q=Policy%20Driven%20Management%20for%20Distributed%20Systems&qe=Or(Id%253D2152505375%252CId%253D2162171351%252CId%253D2102398950%252CId%253D2135665381%252CId%253D2063979829%252CId%253D2033646936%252CId%253D2077695843%252CId%253D2117973752%252CId%253D1586886200%252CId%253D2115555824%252CId%253D2106667508%252CId%253D1981685139%252CId%253D2039881836%252CId%253D2151018442%252CId%253D1963630936%252CId%253D1527079862%252CId%253D2019993175%252CId%253D2157879990%252CId%253D1958273492%252CId%253D1490465304%252CId%253D2315335624)&f=&orderBy=0";
		final String resultFile = resultDirectory + "AcademicMicrosoftScraperUnitURLTest2.bib";
		assertScraperResult(url, null, AcademicMicrosoftScraper.class, resultFile);
	}

	@Test
	public void url3TestRun(){
		final String url = "https://academic.microsoft.com/paper/2016449135/reference/search?q=Kernel%20estimators%20of%20extreme%20level%20curves&qe=Or(Id%253D2502254989%252CId%253D411746464%252CId%253D128533561%252CId%253D1495241956%252CId%253D2059507684%252CId%253D1976171518%252CId%253D2036983007%252CId%253D2048649579%252CId%253D2140327107%252CId%253D2725805405%252CId%253D2089690619%252CId%253D1820206506%252CId%253D1983608423%252CId%253D2164931226%252CId%253D2030753668%252CId%253D1963938936%252CId%253D2006832191%252CId%253D140872396%252CId%253D1991967095%252CId%253D2021823919%252CId%253D2053906871%252CId%253D3122357788%252CId%253D2011667907%252CId%253D2081521562%252CId%253D2057981600%252CId%253D2168727758%252CId%253D2136341770%252CId%253D1974274973%252CId%253D1983306107%252CId%253D96416428%252CId%253D2065905349%252CId%253D1964772236%252CId%253D2143429240%252CId%253D2467868261%252CId%253D1995926088%252CId%253D2132777113)&f=&orderBy=0";
		final String resultFile = resultDirectory + "AcademicMicrosoftScraperUnitURLTest3.bib";
		assertScraperResult(url, null, AcademicMicrosoftScraper.class, resultFile);
	}

	@Test
	public void url4TestRun(){
		final String url = "https://academic.microsoft.com/paper/2109559765/reference/search?q=Academic%20publication%20management%20with%20PUMA%3A%20collect%2C%20organize%20and%20share%20publications&qe=Or(Id%253D1884402041)&f=&orderBy=0";
		final String resultFile = resultDirectory + "AcademicMicrosoftScraperUnitURLTest4.bib";
		assertScraperResult(url, null, AcademicMicrosoftScraper.class, resultFile);
	}


}
