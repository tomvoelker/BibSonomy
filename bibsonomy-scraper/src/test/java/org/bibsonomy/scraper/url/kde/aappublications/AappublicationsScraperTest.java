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
package org.bibsonomy.scraper.url.kde.aappublications;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Mohammed Abed
 */
@Category(RemoteTest.class)
public class AappublicationsScraperTest {
	String resultDirectory = "aappublications/";
	/**
	 * starts URL test with id url_356
	 */
	@Test
	public void url1TestRun() {
		final String url = "https://publications.aap.org/pediatrics/article-abstract/136/5/e1186/33840/Racial-and-Regional-Differences-in-Rates-of?redirectedFrom=fulltext";
		final String resultFile = resultDirectory + "AappublicationsScraperUnitURLTest1.bib";
		assertScraperResult(url, AappublicationsScraper.class, resultFile);
	}

	@Test
	public void url2TestRun() {
		final String url = "https://publications.aap.org/neoreviews/article-abstract/22/6/e360/180583/Powdered-to-Liquid-Human-Milk-Fortifiers-in-the?redirectedFrom=fulltext";
		final String resultFile = resultDirectory +  "AappublicationsScraperUnitURLTest2.bib";
		assertScraperResult(url, AappublicationsScraper.class, resultFile);
	}
}
