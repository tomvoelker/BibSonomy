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
package org.bibsonomy.scraper.url.kde.bibsonomy;

import org.bibsonomy.scraper.Scraper;
//import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.bibsonomy.scraper.junit.RemoteTestAssert;

/**
 * Scraper URL tests for BibSonomyScraper
 * @author tst
 */
@Category(RemoteTest.class)
public class BibSonomyScraperTest {
	
	/**
	 * starts URL test with id url_38
	 */
	@Test
	public void url1TestRun(){
//		UnitTestRunner.runSingleTest("url_38");
		
		final String url = "https://www.bibsonomy.org/bibtex/2101efca8c9368b56d680ce92329784e5/jaeschke";
		final String selection = null;
		final Class<? extends Scraper> scraperClass = BibSonomyScraper.class;
		final String resultFile = "BibSonomyScraperUnitURLTest.bib";
		RemoteTestAssert.assertScraperResult(url, selection, scraperClass, resultFile);
	}
	
	/**
	 * starts URL test with id url_39
	 */
	@Test
	public void url2TestRun(){
//		UnitTestRunner.runSingleTest("url_39");
		
		final String url = "https://www.bibsonomy.org/bib/bibtex/2101efca8c9368b56d680ce92329784e5/jaeschke";
		final String selection = null;
		final Class<? extends Scraper> scraperClass = BibSonomyScraper.class;
		final String resultFile = "BibSonomyScraperUnitURLTest.bib";
		RemoteTestAssert.assertScraperResult(url, selection, scraperClass, resultFile);
	}
	
	/**
	 * starts URL test with id url_209
	 */
	@Test
	public void url3TestRun(){
//		UnitTestRunner.runSingleTest("url_209");
		
		final String url = "https://www.bibsonomy.org/publication/2101efca8c9368b56d680ce92329784e5/jaeschke";
		final String selection = null;
		final Class<? extends Scraper> scraperClass = BibSonomyScraper.class;
		final String resultFile = "BibSonomyScraperUnitURLTest.bib";
		RemoteTestAssert.assertScraperResult(url, selection, scraperClass, resultFile);
	}

	/**
	 * starts URL test with id url_210
	 */
	@Test
	public void url4TestRun(){
//		UnitTestRunner.runSingleTest("url_210");
		
		final String url = "https://www.bibsonomy.org/bib/publication/2101efca8c9368b56d680ce92329784e5/jaeschke";
		final String selection = null;
		final Class<? extends Scraper> scraperClass = BibSonomyScraper.class;
		final String resultFile = "BibSonomyScraperUnitURLTest.bib";
		RemoteTestAssert.assertScraperResult(url, selection, scraperClass, resultFile);
	}
}
