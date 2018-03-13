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
package org.bibsonomy.scraper.url.kde.wileyintersience;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #33 #34 #109 for WileyIntersienceScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class WileyIntersienceScraperTest {
	
	/**
	 * starts URL test with id url_33
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://onlinelibrary.wiley.com/doi/10.1002/jemt.10338/abstract";
		final String resultFile = "WileyIntersienceScraperUnitURLTest1.bib";
		assertScraperResult(url, null, WileyIntersienceScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_109
	 */
	@Test
	public void url3TestRun(){
		final String url = "http://onlinelibrary.wiley.com/doi/10.1111/j.1365-2575.2007.00275.x/abstract";
		final String resultFile = "WileyIntersienceScraperUnitURLTest3.bib";
		assertScraperResult(url, null, WileyIntersienceScraper.class, resultFile);
	}
	/**
	 * starts URL test with id url_189
	 */
	@Test
	public void url4TestRun(){
		final String url = "http://onlinelibrary.wiley.com/doi/10.1002/1521-4095(200011)12:22%3C1655::AID-ADMA1655%3E3.0.CO;2-2/abstract";
		final String resultFile = "WileyIntersienceScraperUnitURLTest4.bib";
		assertScraperResult(url, null, WileyIntersienceScraper.class, resultFile);
	}
	/**
	 * starts URL test with id url_189
	 * Scraping books is currently not supported, as we can not find the BibTeX for the book, only for individual chapters.
	 */
	@Test
	@Ignore
	public void url5TestRun(){
		final String url = "http://onlinelibrary.wiley.com/book/10.1029/AR071";
		final String resultFile = "WileyIntersienceScraperUnitURLTest5.bib";
		assertScraperResult(url, null, WileyIntersienceScraper.class, resultFile);
	}
}
