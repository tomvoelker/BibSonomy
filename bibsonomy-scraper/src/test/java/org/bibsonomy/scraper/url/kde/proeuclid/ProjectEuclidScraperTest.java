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
package org.bibsonomy.scraper.url.kde.proeuclid;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author rja
 */
@Category(RemoteTest.class)
public class ProjectEuclidScraperTest {
	String resultDirectory = "projecteuclid/";
	/**
	 * starts URL test with id url_359
	 */
	@Test
	public void url1TestRun(){
		final String url = "https://projecteuclid.org/journals/annals-of-mathematical-statistics/volume-33/issue-1/The-Future-of-Data-Analysis/10.1214/aoms/1177704711.full";
		final String resultFile = resultDirectory + "ProjectEuclidScraperUnitURLTest1.bib";
		assertScraperResult(url, null, ProjectEuclidScraper.class, resultFile);
	}
	@Test
	public void url2TestRun(){
		final String url = "https://projecteuclid.org/journals/experimental-mathematics/volume-21/issue-4/Twisted-Alexander-Polynomials-of-Hyperbolic-Knots/em/1356038817.full";
		final String resultFile = resultDirectory + "ProjectEuclidScraperUnitURLTest2.bib";
		assertScraperResult(url, null, ProjectEuclidScraper.class, resultFile);
	}

	@Test
	public void url3Test1Run(){
		final String url = "https://projecteuclid.org/journals/advances-in-operator-theory/volume-4/issue-1/The-Bishop-Phelps-Bollob%c3%a1s-modulus-for-functionals-on-classical-Banach/10.15352/aot.1712-1280.short";
		final String resultFile = resultDirectory + "ProjectEuclidScraperUnitURLTest3.bib";
		assertScraperResult(url, null, ProjectEuclidScraper.class, resultFile);
	}

	@Test
	public void url3Test2Run(){
		final String url = "https://projecteuclid.org/journals/advances-in-operator-theory/volume-4/issue-1/The-Bishop-Phelps-Bollob%c3%a1s-modulus-for-functionals-on-classical-Banach/10.15352/aot.1712-1280.short?tab=ArticleFirstPage";
		final String resultFile = resultDirectory + "ProjectEuclidScraperUnitURLTest3.bib";
		assertScraperResult(url, null, ProjectEuclidScraper.class, resultFile);
	}
}
