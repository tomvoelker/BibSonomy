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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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
		final ZoteroTranslationServerScraper scraper = new ZoteroTranslationServerScraper(StubClient.withWebResult(new ZoteroTranslationResult(BIBTEX, true, 2), null));
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
		final StubClient client = StubClient.withWebResult(null, new ZoteroTranslationResult(BIBTEX, false, 0));
		final ZoteroTranslationServerScraper scraper = new ZoteroTranslationServerScraper(client);
		final ScrapingContext context = new ScrapingContext(null, "doi:10.1234/example");

		assertTrue(scraper.scrape(context));
		assertEquals("10.1234/example", client.searchQuery);
		assertEquals(BIBTEX, context.getBibtexResult());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testScrapeSelectedDoiWithUrlUsesSearchBeforeWeb() throws Exception {
		final StubClient client = StubClient.withWebResult(new ZoteroTranslationResult("@misc{generic}\n", false, 0), new ZoteroTranslationResult(BIBTEX, false, 0));
		final ZoteroTranslationServerScraper scraper = new ZoteroTranslationServerScraper(client);
		final ScrapingContext context = new ScrapingContext(new URL("https://example.org/generic"), "doi:10.1234/example");

		assertTrue(scraper.scrape(context));
		assertEquals("10.1234/example", client.searchQuery);
		assertNull(client.webUrl);
		assertEquals(BIBTEX, context.getBibtexResult());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testScrapeSelectedDoiFallsBackToUrlWhenSearchHasNoResult() throws Exception {
		final StubClient client = StubClient.withWebResult(new ZoteroTranslationResult(BIBTEX, false, 0), null);
		final ZoteroTranslationServerScraper scraper = new ZoteroTranslationServerScraper(client);
		final ScrapingContext context = new ScrapingContext(new URL("https://example.org/article"), "doi:10.1234/example");

		assertTrue(scraper.scrape(context));
		assertEquals("10.1234/example", client.searchQuery);
		assertEquals(1, client.webCalls);
		assertEquals("https://example.org/article", client.webUrl);
		assertEquals(BIBTEX, context.getBibtexResult());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testScrapeDoesNotInferBareArxivIdFromNonArxivUrl() throws Exception {
		final StubClient client = StubClient.withWebResult(null, new ZoteroTranslationResult(BIBTEX, false, 0));
		final ZoteroTranslationServerScraper scraper = new ZoteroTranslationServerScraper(client);
		final ScrapingContext context = new ScrapingContext(new URL("https://example.org/papers/2024.12345"));

		assertFalse(scraper.scrape(context));
		assertEquals(1, client.webCalls);
		assertNull(client.searchQuery);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testScrapeUsesArxivSearchFallbackForArxivUrl() throws Exception {
		final StubClient client = StubClient.withWebResult(null, new ZoteroTranslationResult(BIBTEX, false, 0));
		final ZoteroTranslationServerScraper scraper = new ZoteroTranslationServerScraper(client);
		final ScrapingContext context = new ScrapingContext(new URL("https://arxiv.org/abs/2401.12345"));

		assertTrue(scraper.scrape(context));
		assertEquals("arXiv:2401.12345", client.searchQuery);
		assertEquals(BIBTEX, context.getBibtexResult());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testScrapeDoesNotRetryNonAclPdfAsAclLandingPage() throws Exception {
		final StubClient client = StubClient.withWebResults(new ZoteroTranslationResult[] { null, new ZoteroTranslationResult(BIBTEX, false, 0) }, null);
		final ZoteroTranslationServerScraper scraper = new ZoteroTranslationServerScraper(client);
		final ScrapingContext context = new ScrapingContext(new URL("https://example.org/article.pdf"));

		assertFalse(scraper.scrape(context));
		assertEquals(1, client.webCalls);
		assertEquals("https://example.org/article.pdf", client.webUrl);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testScrapeRetriesAclPdfAsLandingPage() throws Exception {
		final StubClient client = StubClient.withWebResults(new ZoteroTranslationResult[] { null, new ZoteroTranslationResult(BIBTEX, false, 0) }, null);
		final ZoteroTranslationServerScraper scraper = new ZoteroTranslationServerScraper(client);
		final ScrapingContext context = new ScrapingContext(new URL("https://www.aclweb.org/anthology/W04-1806.pdf"));

		assertTrue(scraper.scrape(context));
		assertEquals(2, client.webCalls);
		assertEquals("https://www.aclweb.org/anthology/W04-1806.pdf", client.firstWebUrl);
		assertEquals("https://aclanthology.org/W04-1806/", client.webUrl);
		assertEquals(BIBTEX, context.getBibtexResult());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testScrapeRetriesDblpXmlAsCanonicalBibtexView() throws Exception {
		final StubClient client = StubClient.withWebResults(new ZoteroTranslationResult[] { null, new ZoteroTranslationResult(BIBTEX, false, 0) }, null);
		final ZoteroTranslationServerScraper scraper = new ZoteroTranslationServerScraper(client);
		final ScrapingContext context = new ScrapingContext(new URL("https://dblp.uni-trier.de/rec/books/sp/stdesign14/AtzmuellerBHKM0SSS14.xml"));

		assertTrue(scraper.scrape(context));
		assertEquals(2, client.webCalls);
		assertEquals("https://dblp.uni-trier.de/rec/books/sp/stdesign14/AtzmuellerBHKM0SSS14.xml", client.firstWebUrl);
		assertEquals("https://dblp.org/rec/books/sp/stdesign14/AtzmuellerBHKM0SSS14.html?view=bibtex", client.webUrl);
		assertEquals(BIBTEX, context.getBibtexResult());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testScrapeRetriesDblpBibAsCanonicalBibtexView() throws Exception {
		final StubClient client = StubClient.withWebResults(new ZoteroTranslationResult[] { null, new ZoteroTranslationResult(BIBTEX, false, 0) }, null);
		final ZoteroTranslationServerScraper scraper = new ZoteroTranslationServerScraper(client);
		final ScrapingContext context = new ScrapingContext(new URL("https://dblp.uni-trier.de/rec/bib2/conf/gi/HothoJSS06.bib"));

		assertTrue(scraper.scrape(context));
		assertEquals(2, client.webCalls);
		assertEquals("https://dblp.org/rec/conf/gi/HothoJSS06.html?view=bibtex", client.webUrl);
		assertEquals(BIBTEX, context.getBibtexResult());
	}

	private static class StubClient extends ZoteroTranslationServerClient {
		private final ZoteroTranslationResult[] webResults;
		private final ZoteroTranslationResult searchResult;
		private int webCalls;
		private String firstWebUrl;
		private String webUrl;
		private String searchQuery;

		private static StubClient withWebResult(final ZoteroTranslationResult webResult, final ZoteroTranslationResult searchResult) {
			return withWebResults(new ZoteroTranslationResult[] { webResult }, searchResult);
		}

		private static StubClient withWebResults(final ZoteroTranslationResult[] webResults, final ZoteroTranslationResult searchResult) {
			return new StubClient(webResults, searchResult);
		}

		private StubClient(final ZoteroTranslationResult[] webResults, final ZoteroTranslationResult searchResult) {
			super("http://127.0.0.1", 1, 1);
			this.webResults = webResults;
			this.searchResult = searchResult;
		}

		@Override
		public ZoteroTranslationResult translateWebPage(final String url) throws ScrapingException {
			this.webCalls++;
			if (this.firstWebUrl == null) {
				this.firstWebUrl = url;
			}
			this.webUrl = url;
			if (this.webCalls <= this.webResults.length) {
				return this.webResults[this.webCalls - 1];
			}
			return null;
		}

		@Override
		public ZoteroTranslationResult translateSearch(final String query) throws ScrapingException {
			this.searchQuery = query;
			return this.searchResult;
		}
	}
}
