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
package org.bibsonomy.scraper.url.kde.usenix;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #74 #75 #76 #79 #80 #81 #82 #83 #84 #85 for UsenixScraper
 * @author tst
 */
@Category(RemoteTest.class)
public class UsenixScraperTest {
	String resultDirectory = "usenix/";

	//legacy pages
	/**
	 * starts URL test with id url_74
	 */
	@Test
	public void url1TestRun() {
		final String url = "http://www.usenix.org/events/lisa2001/tech/apthorpe.html";
		final String resultFile = resultDirectory + "UsenixScraperUnitURLTest1.bib";
		assertScraperResult(url, null, UsenixScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_75
	 */
	@Test
	public void url2TestRun() {
		final String url = "http://usenix.org/events/usenix07/tech/kotla.html";
		final String resultFile = resultDirectory + "UsenixScraperUnitURLTest2.bib";
		assertScraperResult(url, null, UsenixScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_76
	 */
	@Test
	public void url3TestRun() {
		final String url = "http://usenix.org/events/sec07/tech/drimer.html";
		final String resultFile = resultDirectory + "UsenixScraperUnitURLTest3.bib";
		assertScraperResult(url, null, UsenixScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_79
	 */
	@Test
	public void url4TestRun() {
		final String url = "http://usenix.org/publications/library/proceedings/tcl97/libes_writing.html";
		final String resultFile = resultDirectory + "UsenixScraperUnitURLTest4.bib";
		assertScraperResult(url, null, UsenixScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_80
	 */
	@Test
	@Ignore
	public void url5TestRun() {
		final String url = "http://www.usenix.org/publications/library/proceedings/coots98/krishnaswamy.html";
		final String resultFile = resultDirectory + "UsenixScraperUnitURLTest5.bib";
		assertScraperResult(url, null, UsenixScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_81
	 */
	@Test
	public void url6TestRun() {
		final String url = "http://www.usenix.org/publications/library/proceedings/usenix98/sullivan.html";
		final String resultFile = resultDirectory + "UsenixScraperUnitURLTest6.bib";
		assertScraperResult(url, null, UsenixScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_82
	 */
	@Test
	public void url7TestRun() {
		final String url = "http://usenix.org/events/usenix06/tech/liu.html";
		final String resultFile = resultDirectory + "UsenixScraperUnitURLTest7.bib";
		assertScraperResult(url, null, UsenixScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_83
	 */
	@Test
	public void url8TestRun() {
		final String url = "http://usenix.org/publications/library/proceedings/ec96/geer.html";
		final String resultFile = resultDirectory + "UsenixScraperUnitURLTest8.bib";
		assertScraperResult(url, null, UsenixScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_84
	 */
	@Test
	public void url9TestRun() {
		final String url = "http://usenix.org/publications/library/proceedings/mob95/raja.html";
		final String resultFile = resultDirectory + "UsenixScraperUnitURLTest9.bib";
		assertScraperResult(url, null, UsenixScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_85
	 */
	@Test
	public void url10TestRun(){
		final String url = "http://usenix.org/publications/library/proceedings/sd96/wilkes.html";
		final String resultFile = resultDirectory + "UsenixScraperUnitURLTest10.bib";
		assertScraperResult(url, null, UsenixScraper.class, resultFile);
	}
	//non legacy pages
	@Test
	public void url11TestRun(){
		final String url = "https://www.usenix.org/conference/usenixsecurity19/presentation/lee";
		final String resultFile = resultDirectory + "UsenixScraperUnitURLTest11.bib";
		assertScraperResult(url, null, UsenixScraper.class, resultFile);
	}

	@Test
	public void url12TestRun(){
		final String url = "https://www.usenix.org/jesa/0101/hembroff";
		final String resultFile = resultDirectory + "UsenixScraperUnitURLTest12.bib";
		assertScraperResult(url, null, UsenixScraper.class, resultFile);
	}

}
