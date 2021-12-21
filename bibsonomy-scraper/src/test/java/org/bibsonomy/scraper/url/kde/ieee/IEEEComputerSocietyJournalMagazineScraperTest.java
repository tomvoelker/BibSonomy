package org.bibsonomy.scraper.url.kde.ieee;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class IEEEComputerSocietyJournalMagazineScraperTest {
	String resultDirectory = "ieee/computersociety/journalmagazine/";

	@Test
	public void url1TestRun() {
		final String url = "https://www.computer.org/csdl/journal/oj/2021/01/09324944/1qnQbfZkuPu";
		final String resultFile = resultDirectory + "IEEEComputerSocietyScraperUnitURLTest1.bib";
		assertScraperResult(url, IEEEComputerSocietyJournalMagazineScraper.class, resultFile);
	}

	@Test
	public void url2TestRun() {
		final String url = "https://www.computer.org/csdl/journal/oj/2021/01/09351702/1r4ZMOauoQo";
		final String resultFile = resultDirectory + "IEEEComputerSocietyScraperUnitURLTest2.bib";
		assertScraperResult(url, IEEEComputerSocietyJournalMagazineScraper.class, resultFile);
	}

	@Test
	public void url3TestRun() {
		final String url = "https://www.computer.org/csdl/magazine/co/2021/12/09622296/1yEUnvSB8QM";
		final String resultFile = resultDirectory + "IEEEComputerSocietyScraperUnitURLTest3.bib";
		assertScraperResult(url, IEEEComputerSocietyJournalMagazineScraper.class, resultFile);
	}

	@Test
	public void url4TestRun() {
		final String url = "https://www.computer.org/csdl/magazine/sp/2021/06/09442838/1tWJmM8qGYw";
		final String resultFile = resultDirectory + "IEEEComputerSocietyScraperUnitURLTest4.bib";
		assertScraperResult(url, IEEEComputerSocietyJournalMagazineScraper.class, resultFile);
	}
}
