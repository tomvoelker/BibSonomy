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
package org.bibsonomy.scraper.url.kde.dblp;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for DBLPScraper
 * @author wbi
 *
 */
@Category(RemoteTest.class)
public class DBLPScraperTest {
	String resultDirectory = "dblp/";
	
	/**
	 * starts URL test with id url_56
	 */
	@Test
	public void urlTest1Run(){
		final String url = "https://dblp.uni-trier.de/rec/journals/ws/JaschkeHSGS08.html?view=bibtex";
		final String resultFile = resultDirectory +  "DBLPScraperUnitTest1.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_180
	 */
	@Test
	public void url1Test2Run(){
		final String url = "https://dblp.uni-trier.de/rec/conf/semweb/ChoudhuryBP09.html?view=bibtex";
		final String resultFile = resultDirectory + "DBLPScraperUnitTest2.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_316
	 */
	@Test
	public void url2Test2Run(){
		final String url = "https://dblp.uni-trier.de/rec/rdf/books/sp/stdesign14/AtzmuellerBHKM0SSS14.rdf";
		final String resultFile = resultDirectory + "DBLPScraperUnitTest3.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_317
	 */
	@Test
	public void url3Test2Run(){
		final String url = "https://dblp.uni-trier.de/rec/ris/books/sp/stdesign14/AtzmuellerBHKM0SSS14.ris";
		final String resultFile = resultDirectory + "DBLPScraperUnitTest3.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_314
	 */
	@Test
	public void url1Test3Run(){
		final String url = "https://dblp.uni-trier.de/rec/books/sp/stdesign14/AtzmuellerBHKM0SSS14.html?view=bibtex";
		final String resultFile = resultDirectory + "DBLPScraperUnitTest3.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_315
	 */
	@Test
	public void url2Test3Run(){
		final String url = "https://dblp.uni-trier.de/rec/books/sp/stdesign14/AtzmuellerBHKM0SSS14.xml";
		final String resultFile = resultDirectory + "DBLPScraperUnitTest3.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}

	
	/**
	 * starts URL test with id url_320
	 */
	@Test
	public void url3Test3Run(){
		final String url = "https://dblp.uni-trier.de/rec/books/sp/stdesign14/AtzmuellerBHKM0SSS14.html";
		final String resultFile = resultDirectory + "DBLPScraperUnitTest3.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_321
	 */
	@Test
	public void url1Test4Run(){
		final String url = "https://dblp.dagstuhl.de/rec/journals/logcom/BelohlavekV11.html";
		final String resultFile = resultDirectory + "DBLPScraperUnitTest4.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_322
	 */
	@Test
	public void url2Test4Run(){
		final String url = "https://dblp.dagstuhl.de/rec/journals/logcom/BelohlavekV11.html?view=bibtex";
		final String resultFile = resultDirectory + "DBLPScraperUnitTest4.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_323
	 */
	@Test
	public void url3Test4Run(){
		final String url = "https://dblp.dagstuhl.de/rec/journals/logcom/BelohlavekV11.xml";
		final String resultFile = resultDirectory + "DBLPScraperUnitTest4.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_319
	 */
	@Test
	public void urlTest5Run(){
		final String url = "https://dblp.uni-trier.de/rec/bib2/conf/gi/HothoJSS06.bib";
		final String resultFile = resultDirectory + "DBLPScraperUnitTest5.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_326
	 */
	@Test
	public void url1Test6Run(){
		final String url = "https://dblp.org/rec/conf/icassp/AlmeidaK14.html";
		final String resultFile = resultDirectory + "DBLPScraperUnitTest6.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_327
	 */
	@Test
	public void url2Test6Run(){
		final String url = "https://dblp.org/rec/conf/icassp/AlmeidaK14.html?view=bibtex";
		final String resultFile = resultDirectory + "DBLPScraperUnitTest6.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_328
	 */
	@Test
	public void url3Test6Run(){
		final String url = "https://dblp.org/rec/conf/icassp/AlmeidaK14.xml";
		final String resultFile = resultDirectory + "DBLPScraperUnitTest6.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_318
	 */
	@Test
	public void urlTest7Run(){
		final String url = "https://dblp.uni-trier.de/rec/bib1/conf/gi/HothoJSS06.bib";
		final String resultFile = resultDirectory + "DBLPScraperUnitTest7.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	@Test
	public void urlTest8Run(){
		final String url = "https://dblp2.uni-trier.de/rec/conf/jeri/2019.html";
		final String resultFile = resultDirectory + "DBLPScraperUnitTest8.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}

}