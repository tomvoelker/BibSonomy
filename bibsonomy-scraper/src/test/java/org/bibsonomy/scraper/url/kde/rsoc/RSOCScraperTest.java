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
package org.bibsonomy.scraper.url.kde.rsoc;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL test #96 #152 for RSOCScraper
 * @author wbi
 */
@Category(RemoteTest.class)
public class RSOCScraperTest {
	String resultDirectory = "rsoc/";
	
	/**
	 * starts URL test with id url_96
	 */
	@Test
	public void urlTest1Run(){
		final String url = "https://royalsocietypublishing.org/doi/abs/10.1098/rsta.1999.0319?sid=9d8827ba-af22-4f85-92b4-d65da59cf197";
		final String resultFile = resultDirectory + "RSOCScraperUnitURLTest1.bib";
		assertScraperResult(url, null, RSOCScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_152
	 */
	@Test
	public void urlTest2Run(){
		final String url = "https://royalsocietypublishing.org/doi/abs/10.1098/rspb.2000.0989?sid=93bbdbe3-dc3a-41e1-bd8d-b6c5af6ac3d8";
		final String resultFile = resultDirectory + "RSOCScraperUnitURLTest2.bib";
		assertScraperResult(url, null, RSOCScraper.class, resultFile);
	}

	@Test
	public void urlTest3Run(){
		final String url = "https://royalsocietypublishing.org/doi/10.1098/rspb.2016.1270";
		final String resultFile = resultDirectory + "RSOCScraperUnitURLTest3.bib";
		assertScraperResult(url, null, RSOCScraper.class, resultFile);
	}

	@Test
	public void urlTest4Run(){
		final String url = "https://royalsocietypublishing.org/doi/10.1098/rspb.2016.0847";
		final String resultFile = resultDirectory + "RSOCScraperUnitURLTest4.bib";
		assertScraperResult(url, null, RSOCScraper.class, resultFile);
	}
	
}
