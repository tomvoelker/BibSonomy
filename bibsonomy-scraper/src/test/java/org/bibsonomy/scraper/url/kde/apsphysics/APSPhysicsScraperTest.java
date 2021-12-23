package org.bibsonomy.scraper.url.kde.apsphysics;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class APSPhysicsScraperTest {
	String resultDirectory = "apsphysics/";

	@Test
	public void urlTest1Run(){
		final String url = "https://journals.aps.org/prxquantum/abstract/10.1103/PRXQuantum.2.040357";
		final String resultFile = resultDirectory + "APSPhysicsScraperUnitURLTest1.bib";
		assertScraperResult(url, APSPhysicsScraper.class, resultFile);
	}

	@Test
	public void urlTest2Run(){
		final String url = "https://journals.aps.org/prper/cited-by/10.1103/PhysRevPhysEducRes.17.020132";
		final String resultFile = resultDirectory + "APSPhysicsScraperUnitURLTest2.bib";
		assertScraperResult(url, APSPhysicsScraper.class, resultFile);
	}

	@Test
	public void urlTest3Run(){
		final String url = "https://journals.aps.org/prapplied/supplemental/10.1103/PhysRevApplied.16.064045";
		final String resultFile = resultDirectory + "APSPhysicsScraperUnitURLTest3.bib";
		assertScraperResult(url, APSPhysicsScraper.class, resultFile);
	}
}
