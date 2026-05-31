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
package org.bibsonomy.scraper.zotero;

import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.bibsonomy.junit.RemoteTest;
import org.bibsonomy.scraper.ScrapingContext;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Remote semantic tests for the Zotero scraper.
 *
 * @author tvolker
 */
@Category(RemoteTest.class)
public class ZoteroTranslationServerScraperRemoteTest {

	private static final String NATURE_URL = "https://www.nature.com/articles/nenergy201741";
	private static final String NATURE_DOI = "10.1038/nenergy.2017.41";
	private static final String NATURE_EXPECTED = "nature/article/NatureArticleScraperUnitURLTest2.bib";

	private static ZoteroTranslationServerScraper scraper;

	/**
	 *
	 */
	@BeforeClass
	public static void setUpClass() {
		final ZoteroTranslationServerConfig config = ZoteroTranslationServerConfig.fromEnvironment();
		Assume.assumeTrue("Set " + ZoteroTranslationServerConfig.ENV_URL + " to run Zotero remote scraper tests.", config.isZoteroEnabled());
		scraper = new ZoteroTranslationServerScraper(config);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testNatureArticleUrl() throws Exception {
		final ScrapingContext context = new ScrapingContext(new URL(NATURE_URL));

		assertTrue(scraper.scrape(context));
		ZoteroSemanticBibTeXAssert.assertSemanticMatch(NATURE_EXPECTED, context.getBibtexResult());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testNatureArticleDoiSelection() throws Exception {
		final ScrapingContext context = new ScrapingContext(null, NATURE_DOI);

		assertTrue(scraper.scrape(context));
		ZoteroSemanticBibTeXAssert.assertSemanticMatch(NATURE_EXPECTED, context.getBibtexResult());
	}
}
