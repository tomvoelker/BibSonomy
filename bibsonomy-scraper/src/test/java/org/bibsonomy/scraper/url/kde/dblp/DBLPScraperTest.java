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
package org.bibsonomy.scraper.url.kde.dblp;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.bibsonomy.scraper.url.kde.biorxiv.BioRxivScraper;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #56 for DBLPScraper
 * @author wbi
 *
 */
@Category(RemoteTest.class)
public class DBLPScraperTest {
	
	/**
	 * starts URL test with id url_56
	 */
	@Test
	public void urlTestRun(){
		final String url = "http://dblp.uni-trier.de/rec/bibtex/journals/ws/JaschkeHSGS08";
		final String resultFile = "DBLPScraperUnitTest.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_180
	 */
	@Test
	public void urlTest1Run(){
		final String url = "http://dblp.uni-trier.de/rec/bibtex/conf/semweb/ChoudhuryBP09";
		final String resultFile = "DBLPScraperUnitTest1.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_314
	 */
	@Test
	public void urlTest2Run(){
		final String url = "http://dblp.uni-trier.de/rec/bibtex/books/sp/stdesign14/AtzmuellerBHKM0SSS14";
		final String resultFile = "DBLPScraperUnitTest2.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_315
	 */
	@Test
	public void urlTest3Run(){
		final String url = "http://dblp.uni-trier.de/rec/xml/books/sp/stdesign14/AtzmuellerBHKM0SSS14.xml";
		final String resultFile = "DBLPScraperUnitTest2.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_316
	 */
	@Test
	public void urlTest4Run(){
		final String url = "http://dblp.uni-trier.de/rec/rdf/books/sp/stdesign14/AtzmuellerBHKM0SSS14.rdf";
		final String resultFile = "DBLPScraperUnitTest2.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_317
	 */
	@Test
	public void urlTest5Run(){
		final String url = "http://dblp.uni-trier.de/rec/ris/books/sp/stdesign14/AtzmuellerBHKM0SSS14.ris";
		final String resultFile = "DBLPScraperUnitTest2.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_318
	 */
	@Test
	public void urlTest6Run(){
		final String url = "http://dblp.uni-trier.de/rec/bib1/conf/gi/HothoJSS06.bib";
		final String resultFile = "DBLPScraperUnitTest3.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_319
	 */
	@Test
	public void urlTest7Run(){
		final String url = "http://dblp.uni-trier.de/rec/bib2/conf/gi/HothoJSS06.bib";
		final String resultFile = "DBLPScraperUnitTest4.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_320
	 */
	@Test
	public void urlTest8Run(){
		final String url = "http://dblp.uni-trier.de/rec/html/books/sp/stdesign14/AtzmuellerBHKM0SSS14";
		final String resultFile = "DBLPScraperUnitTest2.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_321
	 */
	@Test
	public void urlTest9Run(){
		final String url = "http://dblp.dagstuhl.de/rec/html/journals/logcom/BelohlavekV11";
		final String resultFile = "DBLPScraperUnitTest5.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_322
	 */
	@Test
	public void urlTest10Run(){
		final String url = "http://dblp.dagstuhl.de/rec/bibtex/journals/logcom/BelohlavekV11";
		final String resultFile = "DBLPScraperUnitTest5.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_323
	 */
	@Test
	public void urlTest11Run(){
		final String url = "http://dblp.dagstuhl.de/rec/xml/journals/logcom/BelohlavekV11.xml";
		final String resultFile = "DBLPScraperUnitTest5.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_324
	 */
	@Test
	public void urlTest12Run(){
		final String url = "http://dblp.dagstuhl.de/rec/ris/journals/logcom/BelohlavekV11.ris";
		final String resultFile = "DBLPScraperUnitTest5.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_325
	 */
	@Test
	public void urlTest13Run(){
		final String url = "http://dblp.dagstuhl.de/rec/rdf/journals/logcom/BelohlavekV11.rdf";
		final String resultFile = "DBLPScraperUnitTest5.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_326
	 */
	@Test
	public void urlTest14Run(){
		final String url = "http://www.dblp.org/rec/html/conf/icassp/AlmeidaK14";
		final String resultFile = "DBLPScraperUnitTest6.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_327
	 */
	@Test
	public void urlTest15Run(){
		final String url = "http://www.dblp.org/rec/bibtex/conf/icassp/AlmeidaK14";
		final String resultFile = "DBLPScraperUnitTest6.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_328
	 */
	@Test
	public void urlTest16Run(){
		final String url = "http://www.dblp.org/rec/xml/conf/icassp/AlmeidaK14.xml";
		final String resultFile = "DBLPScraperUnitTest6.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_329
	 */
	@Test
	public void urlTest17Run(){
		final String url = "http://www.dblp.org/rec/ris/conf/icassp/AlmeidaK14.ris";
		final String resultFile = "DBLPScraperUnitTest6.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_330
	 */
	@Test
	public void urlTest18Run(){
		final String url = "http://www.dblp.org/rec/rdf/conf/icassp/AlmeidaK14.rdf";
		final String resultFile = "DBLPScraperUnitTest6.bib";
		assertScraperResult(url, null, DBLPScraper.class, resultFile);
	}
}