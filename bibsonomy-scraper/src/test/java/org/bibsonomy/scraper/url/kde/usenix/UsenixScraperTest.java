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

	/**
	 * starts URL test with id url_74
	 */
	@Test
	public void url1TestRun() {
		assertScraperResult("http://www.usenix.org/events/lisa2001/tech/apthorpe.html", UsenixScraper.class, "UsenixScraperUnitURLTest1.bib");
	}

	/**
	 * starts URL test with id url_75
	 */
	@Test
	public void url2TestRun() {
		assertScraperResult("http://usenix.org/events/usenix07/tech/kotla.html", UsenixScraper.class, "UsenixScraperUnitURLTest2.bib");
	}
	
	/**
	 * starts URL test with id url_76
	 */
	@Test
	public void url3TestRun() {
		assertScraperResult("http://usenix.org/events/sec07/tech/drimer.html", UsenixScraper.class, "UsenixScraperUnitURLTest3.bib");
	}

	/**
	 * starts URL test with id url_79
	 */
	@Test
	public void url4TestRun() {
		assertScraperResult("http://usenix.org/publications/library/proceedings/tcl97/libes_writing.html", UsenixScraper.class, "UsenixScraperUnitURLTest4.bib");
	}

	/**
	 * starts URL test with id url_80
	 */
	@Test
	@Ignore
	public void url5TestRun() {
		assertScraperResult("http://www.usenix.org/publications/library/proceedings/coots98/krishnaswamy.html", UsenixScraper.class, "UsenixScraperUnitURLTest5.bib");
	}
	
	/**
	 * starts URL test with id url_81
	 */
	@Test
	public void url6TestRun() {
		assertScraperResult("http://www.usenix.org/publications/library/proceedings/usenix98/sullivan.html", UsenixScraper.class, "UsenixScraperUnitURLTest6.bib");
	}
	
	/**
	 * starts URL test with id url_82
	 */
	@Test
	public void url7TestRun() {
		assertScraperResult("http://usenix.org/events/usenix06/tech/liu.html", UsenixScraper.class, "UsenixScraperUnitURLTest7.bib");
	}

	/**
	 * starts URL test with id url_83
	 */
	@Test
	public void url8TestRun() {
		assertScraperResult("http://usenix.org/publications/library/proceedings/ec96/geer.html", UsenixScraper.class, "UsenixScraperUnitURLTest8.bib");
	}

	/**
	 * starts URL test with id url_84
	 */
	@Test
	public void url9TestRun() {
		assertScraperResult("http://usenix.org/publications/library/proceedings/mob95/raja.html", UsenixScraper.class, "UsenixScraperUnitURLTest9.bib");
	}

	/**
	 * starts URL test with id url_85
	 */
	@Test
	public void url10TestRun(){
		assertScraperResult("http://usenix.org/publications/library/proceedings/sd96/wilkes.html", UsenixScraper.class, "UsenixScraperUnitURLTest10.bib");
	}
	
}
