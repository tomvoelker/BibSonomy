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
package org.bibsonomy.scraper.url.kde.karlsruhe;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for UBKAScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class UBKAScraperTest {
	String resultDirectory = "karlsruhe/ubka/";
	
	/**
	 * starts URL test with id url_31
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://primo.bibliothek.kit.edu/primo_library/libweb/action/display.do?tabs=detailsTab&amp;ct=display&amp;fn=search&amp;doc=KITSRC087550059&amp;indx=3&amp;recIds=KITSRC087550059&amp;recIdxs=2&amp;elementId=&amp;renderMode=poppedOut&amp;displayMode=full";
		final String resultFile = resultDirectory + "UBKAScraperUnitURLTest1.bib";
		assertScraperResult(url, null, UBKAScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_32
	 */
	@Test
	public void url2TestRun(){
		final String url = "http://primo.bibliothek.kit.edu/primo_library/libweb/action/display.do?tabs=detailsTab&amp;ct=display&amp;fn=search&amp;doc=KITSRC349648727&amp;indx=5&amp;recIds=KITSRC349648727&amp;recIdxs=4&amp;elementId=&amp;renderMode=poppedOut&amp;displayMode=full";
		final String resultFile = resultDirectory + "UBKAScraperUnitURLTest2.bib";
		assertScraperResult(url, null, UBKAScraper.class, resultFile);
	}

	@Test
	public void url3TestRun(){
		final String url = "https://primo.bibliothek.kit.edu/primo_library/libweb/action/display.do?tabs=detailsTab&ct=display&fn=search&doc=KITSRC395813166&indx=1&recIds=KITSRC395813166&recIdxs=0&elementId=0&renderMode=poppedOut&displayMode=full&frbrVersion=&frbg=&&dscnt=0&scp.scps=scope%3A%28HSKA%29%2Cscope%3A%28KIT%29%2Cscope%3A%28%22PRIMO%22%29%2Cscope%3A%28KIT_CS%29%2Cscope%3A%28KIT_CN%29%2Cscope%3A%28TRAINING3%29%2Cscope%3A%28INF%29%2Cscope%3A%28Trainining4%29%2Cscope%3A%28TRAINING3A%29&mode=Basic&vid=default&srt=rank&tab=default_tab&vl(freeText0)=Bibsonomy&dum=true&dstmp=1640787683628";
		final String resultFile = resultDirectory + "UBKAScraperUnitURLTest3.bib";
		assertScraperResult(url, null, UBKAScraper.class, resultFile);
	}

	@Test
	public void url4TestRun(){
		final String url = "https://primo.bibliothek.kit.edu/primo_library/libweb/action/display.do?tabs=detailsTab&ct=display&fn=search&doc=KITSRC31812243X&indx=1&recIds=KITSRC31812243X&recIdxs=0&elementId=0&renderMode=poppedOut&displayMode=full&frbrVersion=&frbg=&&dscnt=0&scp.scps=scope%3A%28HSKA%29%2Cscope%3A%28KIT%29%2Cscope%3A%28%22PRIMO%22%29%2Cscope%3A%28KIT_CS%29%2Cscope%3A%28KIT_CN%29%2Cscope%3A%28TRAINING3%29%2Cscope%3A%28INF%29%2Cscope%3A%28Trainining4%29%2Cscope%3A%28TRAINING3A%29&mode=Basic&vid=default&srt=rank&tab=default_tab&vl(freeText0)=web%203.0&dum=true&dstmp=1640787721677";
		final String resultFile = resultDirectory + "UBKAScraperUnitURLTest4.bib";
		assertScraperResult(url, null, UBKAScraper.class, resultFile);
	}

}
