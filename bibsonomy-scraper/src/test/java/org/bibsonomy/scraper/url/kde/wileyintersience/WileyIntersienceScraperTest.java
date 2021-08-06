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
package org.bibsonomy.scraper.url.kde.wileyintersience;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for WileyIntersienceScraper
 *
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class WileyIntersienceScraperTest {

	@Test
	public void url1TestRun(){
		assertScraperResult("http://onlinelibrary.wiley.com/doi/abs/10.1002/jemt.10338", WileyIntersienceScraper.class, "WileyIntersienceScraperUnitURLTest1.bib");
	}

	@Test
	public void url2TestRun(){
		assertScraperResult("http://rmets.onlinelibrary.wiley.com/doi/abs/10.1002/qj.3384", WileyIntersienceScraper.class, "WileyIntersienceScraperUnitURLTest2.bib");
	}
	
	/**
	 * starts URL test with id url_109
	 */
	@Test
	public void url3TestRun(){
		assertScraperResult("http://onlinelibrary.wiley.com/doi/abs/10.1111/j.1365-2575.2007.00275.x", WileyIntersienceScraper.class, "WileyIntersienceScraperUnitURLTest3.bib");
	}
	
	/**
	 * starts URL test with id url_189
	 */
	@Test
	public void url4TestRun(){
		assertScraperResult("https://onlinelibrary.wiley.com/doi/abs/10.1002/1521-4095%28200011%2912%3A22%3C1655%3A%3AAID-ADMA1655%3E3.0.CO%3B2-2", WileyIntersienceScraper.class, "WileyIntersienceScraperUnitURLTest4.bib");
	}	
	
	/**
	 * starts URL test with id url_189
	 */
	@Test
	public void url5TestRun(){
		assertScraperResult("https://agupubs.onlinelibrary.wiley.com/doi/book/10.1029/AR071", WileyIntersienceScraper.class, "WileyIntersienceScraperUnitURLTest5.bib");

	}	
}
