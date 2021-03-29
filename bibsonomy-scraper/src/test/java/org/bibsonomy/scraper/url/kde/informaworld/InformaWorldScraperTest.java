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
package org.bibsonomy.scraper.url.kde.informaworld;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.bibsonomy.scraper.url.kde.ingenta.IngentaconnectScraper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #77 #78 #123 #135 for 
 * @author wbi
 * 
 * acquired by Taylor and Francis?
 * 
 */
@Category(RemoteTest.class)
@Ignore
@Deprecated
public class InformaWorldScraperTest {
	
	/**
	 * starts URL test with id url_77
	 */
	@Test
	public void url1TestRun(){
		assertScraperResult("http://www.informaworld.com/smpp/content~content=a779504357~db=all~order=page", null, InformaWorldScraper.class, "InformaworldUnitURLTest1.bib");
	}
	
	/**
	 * starts URL test with id url_78
	 */
	@Test
	public void url2TestRun(){
		assertScraperResult("http://www.informaworld.com/smpp/content~db=all~content=a779504357~tab=citation", null, InformaWorldScraper.class, "InformaworldUnitURLTest2.bib");
	}
	
	/**
	 * starts URL test with id url_123
	 */
	@Test
	public void url3TestRun(){
		assertScraperResult("http://prod.informaworld.com/smpp/title~content=t748423783~db=all", null, InformaWorldScraper.class, "InformaworldUnitURLTest3.bib");
	}
	
	/**
	 * starts URL test with id url_135
	 */
	@Test
	public void url4TestRun(){
		assertScraperResult("http://www.informaworld.com/smpp/content~content=a905990187~db=all~jumptype=rss", null, InformaWorldScraper.class, "InformaworldUnitURLTest4.bib");
	}
	
}
