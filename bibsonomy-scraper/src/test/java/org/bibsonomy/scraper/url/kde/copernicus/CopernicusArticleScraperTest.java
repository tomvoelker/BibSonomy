package org.bibsonomy.scraper.url.kde.copernicus;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class CopernicusArticleScraperTest {
	String resultDirectory = "copernicus/article/";

	@Test
	public void url1TestRun(){
		final String url = "https://ascmo.copernicus.org/articles/7/35/2021/";
		final String resultFile = resultDirectory + "CopernicusArticleScraperUnitURLTest1.bib";
		assertScraperResult(url, null, CopernicusArticleScraper.class, resultFile);
	}

	@Test
	public void url2TestRun(){
		final String url = "https://angeo.copernicus.org/articles/40/55/2022/";
		final String resultFile = resultDirectory + "CopernicusArticleScraperUnitURLTest2.bib";
		assertScraperResult(url, null, CopernicusArticleScraper.class, resultFile);
	}

	@Test
	public void url3TestRun(){
		final String url = "https://acp.copernicus.org/articles/22/93/2022/";
		final String resultFile = resultDirectory + "CopernicusArticleScraperUnitURLTest3.bib";
		assertScraperResult(url, null, CopernicusArticleScraper.class, resultFile);
	}

	@Test
	public void url4TestRun(){
		final String url = "https://bg.copernicus.org/articles/19/517/2022/";
		final String resultFile = resultDirectory + "CopernicusArticleScraperUnitURLTest4.bib";
		assertScraperResult(url, null, CopernicusArticleScraper.class, resultFile);
	}

	@Test
	public void url5TestRun(){
		final String url = "https://amt.copernicus.org/articles/special_issue10_250.html";
		final String resultFile = resultDirectory + "CopernicusArticleScraperUnitURLTest5.bib";
		assertScraperResult(url, null, CopernicusArticleScraper.class, resultFile);
	}

	@Test
	public void url6TestRun(){
		final String url = "https://bg.copernicus.org/articles/special_issue190.html";
		final String resultFile = resultDirectory + "CopernicusArticleScraperUnitURLTest6.bib";
		assertScraperResult(url, null, CopernicusArticleScraper.class, resultFile);
	}

}
