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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Test;

/**
 * Tests {@link ZoteroFallbackScraper}.
 *
 * @author tvolker
 */
public class ZoteroFallbackScraperTest {

	/**
	 * @throws Exception
	 */
	@Test
	public void testDoesNotCallUnsupportedLegacyFallback() throws Exception {
		final RecordingScraper legacyScraper = new RecordingScraper(false, true);
		final ZoteroFallbackScraper scraper = new ZoteroFallbackScraper(new ZoteroTranslationServerConfig(null, false, true, 1, 1), legacyScraper);

		assertFalse(scraper.scrape(new ScrapingContext(new URL("https://example.org/article"))));
		assertTrue(legacyScraper.supportsCalled);
		assertFalse(legacyScraper.scrapeCalled);
	}

	/**
	 *
	 */
	@Test
	public void testGetScraperRespectsDisabledLegacyFallback() {
		final RecordingScraper legacyScraper = new RecordingScraper(true, true);
		final ZoteroFallbackScraper scraper = new ZoteroFallbackScraper(new ZoteroTranslationServerConfig("http://zotero.example", true, false, 1, 1), legacyScraper);
		final Collection<Scraper> scrapers = scraper.getScraper();

		assertFalse(scrapers.contains(legacyScraper));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testRethrowsZoteroFailureWhenLegacyFallbackDoesNotRun() throws Exception {
		final ScrapingException failure = new ScrapingException("zotero failed");
		final RecordingScraper legacyScraper = new RecordingScraper(false, true);
		final ZoteroTranslationServerConfig config = new ZoteroTranslationServerConfig("http://zotero.example", true, true, 1, 1);
		final ZoteroFallbackScraper scraper = new ZoteroFallbackScraper(config, new FailingZoteroScraper(failure), legacyScraper);

		try {
			scraper.scrape(new ScrapingContext(new URL("https://example.org/article")));
			fail("expected ScrapingException");
		} catch (final ScrapingException ex) {
			assertSame(failure, ex);
		}
		assertTrue(legacyScraper.supportsCalled);
		assertFalse(legacyScraper.scrapeCalled);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testRethrowsZoteroFailureWhenLegacyFallbackReturnsNoResult() throws Exception {
		final ScrapingException failure = new ScrapingException("zotero failed");
		final RecordingScraper legacyScraper = new RecordingScraper(true, false);
		final ZoteroTranslationServerConfig config = new ZoteroTranslationServerConfig("http://zotero.example", true, true, 1, 1);
		final ZoteroFallbackScraper scraper = new ZoteroFallbackScraper(config, new FailingZoteroScraper(failure), legacyScraper);

		try {
			scraper.scrape(new ScrapingContext(new URL("https://example.org/article")));
			fail("expected ScrapingException");
		} catch (final ScrapingException ex) {
			assertSame(failure, ex);
		}
		assertTrue(legacyScraper.scrapeCalled);
	}

	private static class RecordingScraper implements Scraper {
		private final boolean supports;
		private final boolean scrapeResult;
		private boolean supportsCalled;
		private boolean scrapeCalled;

		private RecordingScraper(final boolean supports, final boolean scrapeResult) {
			this.supports = supports;
			this.scrapeResult = scrapeResult;
		}

		@Override
		public boolean scrape(final ScrapingContext scrapingContext) throws ScrapingException {
			this.scrapeCalled = true;
			return this.scrapeResult;
		}

		@Override
		public String getInfo() {
			return "recording scraper";
		}

		@Override
		public Collection<Scraper> getScraper() {
			return Collections.<Scraper>singletonList(this);
		}

		@Override
		public boolean supportsScrapingContext(final ScrapingContext scrapingContext) {
			this.supportsCalled = true;
			return this.supports;
		}
	}

	private static class FailingZoteroScraper extends ZoteroTranslationServerScraper {
		private final ScrapingException failure;

		private FailingZoteroScraper(final ScrapingException failure) {
			super(new ZoteroTranslationServerConfig("http://zotero.example", true, false, 1, 1));
			this.failure = failure;
		}

		@Override
		public boolean scrape(final ScrapingContext scrapingContext) throws ScrapingException {
			throw this.failure;
		}

		@Override
		public boolean supportsScrapingContext(final ScrapingContext scrapingContext) {
			return true;
		}
	}
}
