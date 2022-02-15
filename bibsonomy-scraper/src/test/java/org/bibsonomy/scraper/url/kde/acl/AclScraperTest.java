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
package org.bibsonomy.scraper.url.kde.acl;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #108 for AclScraper
 * @author tst
 */
@Category(RemoteTest.class)
public class AclScraperTest {
	String resultDirectory = "acl/";

	@Test
	public void testWebSiteLink() {
		final String url = "https://www.aclweb.org/anthology/W04-1806/";
		final String resultFile = resultDirectory + "AclScraperUnitURLTest1.bib";
		assertScraperResult(url, AclScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_108
	 */
	@Test
	public void testPDFLink() {
		final String url = "https://www.aclweb.org/anthology/W04-1806.pdf";
		final String resultFile = resultDirectory + "AclScraperUnitURLTest1.bib";
		assertScraperResult(url, AclScraper.class, resultFile);
	}
	
}
