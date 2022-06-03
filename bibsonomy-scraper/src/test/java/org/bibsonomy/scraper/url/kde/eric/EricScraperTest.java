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
package org.bibsonomy.scraper.url.kde.eric;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #99, #136 for EricScraper
 * 
 * @author tst
 */
@Category(RemoteTest.class)
public class EricScraperTest {
	String resultDirectory = "eric/";
	
	/**
	 * starts URL test with id url_99
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://www.eric.ed.gov/ERICWebPortal/Home.portal?_nfpb=true&ERICExtSearch_SearchValue_0=star&searchtype=keyword&ERICExtSearch_SearchType_0=kw&_pageLabel=RecordDetails&objectId=0900019b802f2e44&accno=EJ786532&_nfls=false";
		final String resultFile = resultDirectory + "EricScraperUnitURLTest1.bib";
		assertScraperResult(url, null, EricScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_136
	 */
	@Test
	public void url2TestRun(){
		final String url = "http://eric.ed.gov/ERICWebPortal/custom/portlets/recordDetails/detailmini.jsp?_nfpb=true&_&ERICExtSearch_SearchValue_0=EJ523959&ERICExtSearch_SearchType_0=no&accno=EJ523959";
		final String resultFile = resultDirectory + "EricScraperUnitURLTest2.bib";
		assertScraperResult(url, null, EricScraper.class, resultFile);
	}

}
