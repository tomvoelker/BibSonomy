package org.bibsonomy.scraper.url.kde.ieee;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class IEEEComputerSocietyProceedingScraperTest {
	String resultDirectory = "ieee/computersociety/proceeding/";

	@Test
	public void url1TestRun() {
		final String url = "https://www.computer.org/csdl/proceedings-article/dexa/2006/26410603/12OmNxFJXTp";
		final String resultFile = resultDirectory + "IEEEComputerSocietyScraperUnitURLTest1.bib";
		assertScraperResult(url, IEEEComputerSocietyProceedingScraper.class, resultFile);
	}

	@Test
	public void url2TestRun() {
		final String url = "https://www.computer.org/csdl/proceedings-article/acomp/2019/472300a016/1ivu5t889ZS";
		final String resultFile = resultDirectory + "IEEEComputerSocietyScraperUnitURLTest2.bib";
		assertScraperResult(url, IEEEComputerSocietyProceedingScraper.class, resultFile);
	}

	@Test
	public void url3TestRun() {
		final String url = "https://www.computer.org/csdl/proceedings-article/afips/1982/50890081/12OmNqJ8tsQ";
		final String resultFile = resultDirectory + "IEEEComputerSocietyScraperUnitURLTest3.bib";
		assertScraperResult(url, IEEEComputerSocietyProceedingScraper.class, resultFile);
	}

	@Test
	public void url4TestRun() {
		final String url = "https://www.computer.org/csdl/proceedings-article/apwc-on-cse/2014/07053838/12OmNwIHorc";
		final String resultFile = resultDirectory + "IEEEComputerSocietyScraperUnitURLTest4.bib";
		assertScraperResult(url, IEEEComputerSocietyProceedingScraper.class, resultFile);
	}
}
