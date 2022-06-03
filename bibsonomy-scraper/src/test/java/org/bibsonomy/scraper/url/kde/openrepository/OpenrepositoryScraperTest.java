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
package org.bibsonomy.scraper.url.kde.openrepository;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #117, #118, #199, #120 for OpenrepositoryScraper
 * 
 * @author tst
 */
@Category(RemoteTest.class)
public class OpenrepositoryScraperTest {
	String resultDirectory = "openrepository/";

	@Test
	public void url1TestRun(){
		final String url = "https://repository.arizona.edu/handle/10150/611457";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest1.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);

	}

	@Test
	public void url2TestRun(){
		final String url = "https://uobrep.openrepository.com/handle/10547/228716";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest2.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url3TestRun(){
		final String url = "https://chesterrep.openrepository.com/handle/10034/624904";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest3.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url4TestRun(){
		final String url = "https://augusta.openrepository.com/handle/10675.2/393";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest4.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url5TestRun(){
		final String url = "https://repository.vlerick.com/handle/20.500.12127/6360";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest5.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url6TestRun(){
		final String url = "https://repository.helmholtz-hzi.de/handle/10033/623124";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest6.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url7TestRun(){
		final String url = "https://christie.openrepository.com/handle/10541/108078";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest7.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url8TestRun(){
		final String url = "https://nhm.openrepository.com/handle/10141/622830";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest8.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url9TestRun(){
		final String url = "https://www.stor.scot.nhs.uk/handle/11289/579563";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest9.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url10TestRun(){
		final String url = "https://oxfamilibrary.openrepository.com/handle/10546/344070";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest10.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url11TestRun(){
		final String url = "https://fieldresearch.msf.org/handle/10144/95708";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest11.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url12TestRun(){
		final String url = "https://t-stor.teagasc.ie/handle/11019/1399";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest12.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url13TestRun(){
		final String url = "https://wlv.openrepository.com/handle/2436/621203";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest13.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url14TestRun(){
		final String url = "https://soar.usi.edu/handle/20.500.12419/571";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest14.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url15TestRun(){
		final String url = "https://repository.globethics.net/handle/20.500.12424/1935990";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest15.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url16TestRun(){
		final String url = "https://amber.openrepository.com/handle/20.500.12417/177";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest16.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url17TestRun(){
		final String url = "https://oar.marine.ie/handle/10793/644";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest17.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url18TestRun(){
		final String url = "https://scholarworks.alaska.edu/handle/11122/6306";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest18.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url19TestRun(){
		final String url = "https://ir.icscanada.edu/handle/10756/344273";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest19.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url20TestRun(){
		final String url = "https://www.lenus.ie/handle/10147/624020";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest20.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url21TestRun(){
		final String url = "https://repositorioacademico.upc.edu.pe/handle/10757/656239";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest21.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url22TestRun(){
		final String url = "https://www.hirsla.lsh.is/handle/2336/620276";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest22.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

	@Test
	public void url23TestRun(){
		final String url = "https://derby.openrepository.com/handle/10545/625628";
		final String resultFile = resultDirectory + "OpenrepositoryScraperUnitURLTest23.bib";
		assertScraperResult(url, null, OpenrepositoryScraper.class, resultFile);
	}

}
