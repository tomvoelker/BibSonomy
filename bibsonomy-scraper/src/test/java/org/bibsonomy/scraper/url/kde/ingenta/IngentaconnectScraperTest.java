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
package org.bibsonomy.scraper.url.kde.ingenta;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import static org.junit.Assert.assertEquals;
import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class IngentaconnectScraperTest {

	String resultDirectory = "ingenta/";

	/**
	 * starts URL test with id url_15
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://www.ingentaconnect.com/search/article?option1=tka&value1=ArticleRank%3a+a+PageRank-based+alternative+to+numbers+of+citations+for+analysing+citation+networks&pageSize=10&index=1";
		final String resultFile = resultDirectory + "IngentaconnectScraperUnitURLTest1.bib";
		assertScraperResult(url, IngentaconnectScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_169
	 */
	@Test
	public void url2TestRun(){
		final String url = "http://www.ingentaconnect.com/content/mcb/026/2007/00000026/00000004/art00005";
		final String resultFile = resultDirectory + "IngentaconnectScraperUnitURLTest2.bib";
		assertScraperResult(url, IngentaconnectScraper.class, resultFile);
	}

	@Test
	public void url3TestRun(){
		final String url = "https://www.ingentaconnect.com/content/doaj/24121908/2016/00000002/00000004/art00001";
		final String resultFile = resultDirectory + "IngentaconnectScraperUnitURLTest3.bib";
		assertScraperResult(url, IngentaconnectScraper.class, resultFile);
	}

	@Test
	public void url4TestRun(){
		final String url = "https://www.ingentaconnect.com/content/bpl/test/2015/00000037/00000003/art00001";
		final String resultFile = resultDirectory + "IngentaconnectScraperUnitURLTest4.bib";
		assertScraperResult(url, IngentaconnectScraper.class, resultFile);
	}

	@Test
	public void testFixSpaceInKey() {
		final String b = "number = \"4\",\n" + 
				"publication date =\"2007-04-17T00:00:00\",\n" + 
				"pages = \"370-380\","; 
		final String r = IngentaconnectScraper.fixSpaceInKey(b);
		final String should = "number = \"4\",\n" + 
				"publicationdate =\"2007-04-17T00:00:00\",\n" + 
				"pages = \"370-380\",";
		assertEquals(should, r);
	}
	
}
