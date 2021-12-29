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
package org.bibsonomy.scraper.url.kde.rupress;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class RupressScraperTest {
	String resultDirectory = "rupress/";

	@Test
	public void url1TestRun(){
		final String url = "https://rupress.org/jcb/article/184/4/481/35229/One-dimensional-topography-underlies-three";
		final String resultFile = resultDirectory + "RupressScraperUnitURLTest1.bib";
		assertScraperResult(url, null, RupressScraper.class, resultFile);
	}

	@Test
	public void url2TestRun(){
		final String url = "https://rupress.org/jem/article-abstract/218/12/e20202012/212741/The-histone-demethylase-Lsd1-regulates-multiple?redirectedFrom=fulltext";
		final String resultFile = resultDirectory + "RupressScraperUnitURLTest2.bib";
		assertScraperResult(url, null, RupressScraper.class, resultFile);
	}

	@Test
	public void url3TestRun(){
		final String url = "https://rupress.org/jgp/article-abstract/153/12/e202012584/212725/Suppression-of-ventricular-arrhythmias-by?redirectedFrom=fulltext";
		final String resultFile = resultDirectory + "RupressScraperUnitURLTest3.bib";
		assertScraperResult(url, null, RupressScraper.class, resultFile);
	}

	@Test
	public void url4TestRun(){
		final String url = "https://rupress.org/jgp/article/153/12/e202113009/212726/Targeting-late-ICaL-to-close-the-window-to?searchresult=1";
		final String resultFile = resultDirectory + "RupressScraperUnitURLTest4.bib";
		assertScraperResult(url, null, RupressScraper.class, resultFile);
	}

}
