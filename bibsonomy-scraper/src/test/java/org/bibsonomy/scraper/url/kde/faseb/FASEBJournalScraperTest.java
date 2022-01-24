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
package org.bibsonomy.scraper.url.kde.faseb;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author wla
 */
@Category(RemoteTest.class)
public class FASEBJournalScraperTest {
	String resultDirectory = "faseb/";

	/**
	 * starts URL test with id url_224
	 */
	@Test
	public void urlTestRun1() {
		assertScraperResult("https://faseb.onlinelibrary.wiley.com/doi/10.1096/fj.12-211441", FASEBJournalScraper.class, resultDirectory + "FASEBJournalScraperUnitTest1.bib");
	}
	
	/**
	 * starts URL test with id url_225
	 */
	@Test
	public void urlTestRun2() {
		assertScraperResult("https://faseb.onlinelibrary.wiley.com/doi/full/10.1096/fj.12-0802ufm", FASEBJournalScraper.class, resultDirectory +"FASEBJournalScraperUnitTest2.bib");
	}
	
	/**
	 * starts URL test with id url_227
	 */
	@Test
	public void urlTestRun3() {
		assertScraperResult("https://faseb.onlinelibrary.wiley.com/doi/full/10.1096/fj.01-0431rev", FASEBJournalScraper.class, resultDirectory + "FASEBJournalScraperUnitTest3.bib");
	}

}
