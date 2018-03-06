/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class IngentaconnectScraperTest {
	
	/**
	 * starts URL test with id url_15
	 */
	@Test
	public void urlTestRun1(){
		assertScraperResult("http://www.ingentaconnect.com/search/article?option1=tka&value1=ArticleRank%3a+a+PageRank-based+alternative+to+numbers+of+citations+for+analysing+citation+networks&pageSize=10&index=1", null, IngentaconnectScraper.class, "IngentaconnectScraperUnitURLTest1.bib");
	}
	
	/**
	 * starts URL test with id url_169
	 */
	@Test
	public void urlTestRun2(){
		assertScraperResult("http://www.ingentaconnect.com/content/mcb/026/2007/00000026/00000004/art00005", null, IngentaconnectScraper.class, "IngentaconnectScraperUnitURLTest2.bib");
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
