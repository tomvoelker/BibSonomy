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
