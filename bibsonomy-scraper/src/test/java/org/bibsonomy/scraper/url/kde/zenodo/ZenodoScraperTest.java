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
package org.bibsonomy.scraper.url.kde.zenodo;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import static org.junit.Assert.assertEquals;
import java.net.URL;
import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.bibsonomy.scraper.exceptions.ScrapingException;
import java.net.MalformedURLException;
import java.io.IOException;

/**
 * Scraper URL tests for ZenodoScraper
 *
 * @author rja
 *
 */
@Category(RemoteTest.class)
public class ZenodoScraperTest {
	String resultDirectory = "zenodo/";

	/**
	 * starts URL test with id url_30
	 */
	@Test
	public void url1Test() throws ScrapingException, MalformedURLException, IOException {
	    assertEquals("https://zenodo.org/record/580587/export/hx", new ZenodoScraper().getDownloadURL(new URL("https://zenodo.org/record/580587"), null));
	}
    	@Test
	public void url1ScrapeTest() {
				final String url = "https://zenodo.org/record/580587";
				final String resultFile = resultDirectory + "ZenodoScraperUnitURLTest1.bib";
				assertScraperResult(url, ZenodoScraper.class, resultFile);
	}

    
}
