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
package org.bibsonomy.scraper.url.kde.akjournals;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class AKJournalsScraperTest {
	String resultDirectory = "akjournals/";

	@Test
	public void url1Test1Run() {
		final String url = "https://akjournals.com/view/journals/11192/52/2/article-p291.xml";
		final String resultFile = resultDirectory + "AKJournalsScraperUnitURLTest1.bib";
		assertScraperResult(url, AKJournalsScraper.class, resultFile);
	}

	@Test
	public void url2Test1Run() {
		final String url = "https://akjournals.com/view/journals/606/aop/article-10.1556-606.2021.00463/article-10.1556-606.2021.00463.xml?rskey=11F5qz&result=1";
		final String resultFile = resultDirectory + "AKJournalsScraperUnitURLTest2.bib";
		assertScraperResult(url, AKJournalsScraper.class, resultFile);
	}

	@Test
	public void url3Test1Run() {
		final String url = "https://akjournals.com/view/journals/0088/70/1/article-p65.xml?rskey=ibLcF7&result=6";
		final String resultFile = resultDirectory + "AKJournalsScraperUnitURLTest3.bib";
		assertScraperResult(url, AKJournalsScraper.class, resultFile);
	}

	@Test
	public void url4Test1Run() {
		final String url = "https://akjournals.com/view/journals/726/3/2/article-p171.xml?body=contentReferences-23894";
		final String resultFile = resultDirectory + "AKJournalsScraperUnitURLTest4.bib";
		assertScraperResult(url, AKJournalsScraper.class, resultFile);
	}
}
