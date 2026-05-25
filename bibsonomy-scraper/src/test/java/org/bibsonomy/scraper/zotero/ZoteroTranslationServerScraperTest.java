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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Test;

/**
 * Tests {@link ZoteroTranslationServerScraper}.
 *
 * @author tvolker
 */
public class ZoteroTranslationServerScraperTest {

	private static final String BIBTEX = "@article{key,\n  title = {Title}\n}\n";

	/**
	 * @throws Exception
	 */
	@Test
	public void testScrapeWebPageSetsContext() throws Exception {
		final ZoteroTranslationServerScraper scraper = new ZoteroTranslationServerScraper(new StubClient(new ZoteroTranslationResult(BIBTEX, true, 2), null));
		final ScrapingContext context = new ScrapingContext(new URL("https://example.org/article"));

		assertTrue(scraper.scrape(context));
		assertEquals(BIBTEX, context.getBibtexResult());
		assertSame(scraper, context.getScraper());
		assertEquals(2, context.getTmpMetadata().getZoteroMultipleChoiceCount());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testScrapeSelectedDoiUsesSearch() throws Exception {
		final StubClient client = new StubClient(null, new ZoteroTranslationResult(BIBTEX, false, 0));
		final ZoteroTranslationServerScraper scraper = new ZoteroTranslationServerScraper(client);
		final ScrapingContext context = new ScrapingContext(null, "doi:10.1234/example");

		assertTrue(scraper.scrape(context));
		assertEquals("10.1234/example", client.searchQuery);
		assertEquals(BIBTEX, context.getBibtexResult());
	}

	private static class StubClient extends ZoteroTranslationServerClient {
		private final ZoteroTranslationResult webResult;
		private final ZoteroTranslationResult searchResult;
		private String searchQuery;

		private StubClient(final ZoteroTranslationResult webResult, final ZoteroTranslationResult searchResult) {
			super("http://127.0.0.1", 1, 1);
			this.webResult = webResult;
			this.searchResult = searchResult;
		}

		@Override
		public ZoteroTranslationResult translateWebPage(final String url) throws ScrapingException {
			return this.webResult;
		}

		@Override
		public ZoteroTranslationResult translateSearch(final String query) throws ScrapingException {
			this.searchQuery = query;
			return this.searchResult;
		}
	}
}
