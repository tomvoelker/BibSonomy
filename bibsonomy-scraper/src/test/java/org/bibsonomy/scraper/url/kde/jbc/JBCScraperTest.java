package org.bibsonomy.scraper.url.kde.jbc;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class JBCScraperTest {
	String resultDirectory = "jbc/";

	@Test
	public void url1TestRun() {
		final String url = "https://www.jbc.org/article/S0021-9258(19)62368-9/fulltext";
		final String resultFile = resultDirectory + "JBCScraperUnitURLTest1.bib";
		assertScraperResult(url, null, JBCScraper.class, resultFile);
	}

	@Test
	public void url2TestRun() {
		final String url = "https://www.jbc.org/article/S0021-9258(21)01205-9/fulltext#secsectitle0100";
		final String resultFile = resultDirectory + "JBCScraperUnitURLTest2.bib";
		assertScraperResult(url, null, JBCScraper.class, resultFile);
	}

	@Test
	public void url3TestRun() {
		final String url = "https://www.jbc.org/article/S0021-9258(21)01152-2/fulltext#secsectitle0060";
		final String resultFile = resultDirectory + "JBCScraperUnitURLTest3.bib";
		assertScraperResult(url, null, JBCScraper.class, resultFile);
	}

	@Test
	public void url4TestRun() {
		final String url = "https://www.jbc.org/article/S0021-9258(21)01143-1/fulltext#relatedArticles";
		final String resultFile = resultDirectory + "JBCScraperUnitURLTest4.bib";
		assertScraperResult(url, null, JBCScraper.class, resultFile);
	}

}
