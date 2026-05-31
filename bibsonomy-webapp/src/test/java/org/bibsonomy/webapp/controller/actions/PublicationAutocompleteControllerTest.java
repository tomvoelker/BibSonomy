/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Wuerzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universitaet zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.scraper.CompositeScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.webapp.command.actions.PublicationAutocompleteCommand;
import org.bibsonomy.webapp.view.Views;
import org.junit.Test;

/**
 * Tests {@link PublicationAutocompleteController}.
 *
 * @author tvolker
 */
public class PublicationAutocompleteControllerTest {

	private static final String BIBTEX = "@misc{test,\n"
					+ "  author = {Doe, Jane},\n"
					+ "  title = {Scraped Publication},\n"
					+ "  year = {2025}\n"
					+ "}\n";

	/**
	 *
	 */
	@Test
	public void testArxivUrlUsesInjectedScraperChain() {
		final RecordingScraper scraper = runAutocomplete("https://arxiv.org/abs/2411.11171");

		assertEquals(1, scraper.calls);
		assertEquals("https://arxiv.org/abs/2411.11171", scraper.lastContext.getUrl().toString());
		assertEquals("https://arxiv.org/abs/2411.11171", scraper.lastContext.getSelectedText());
	}

	/**
	 *
	 */
	@Test
	public void testArxivIdentifierUsesInjectedScraperChain() {
		final RecordingScraper scraper = runAutocomplete("arXiv:2411.11171");

		assertEquals(1, scraper.calls);
		assertNull(scraper.lastContext.getUrl());
		assertEquals("arxiv:2411.11171", scraper.lastContext.getSelectedText());
	}

	/**
	 *
	 */
	@Test
	public void testIsbnUsesInjectedScraperChain() {
		final RecordingScraper scraper = runAutocomplete("9780262033848");

		assertEquals(1, scraper.calls);
		assertNull(scraper.lastContext.getUrl());
		assertEquals("9780262033848", scraper.lastContext.getSelectedText());
	}

	/**
	 *
	 */
	@Test
	public void testIsbnSkipsUnsupportedScraperLeaves() {
		final RecordingScraper unsupported = new RecordingScraper(false, true);
		final RecordingScraper supported = new RecordingScraper(true, true);
		final CompositeScraper<Scraper> scraper = new CompositeScraper<Scraper>();
		scraper.addScraper(unsupported);
		scraper.addScraper(supported);

		runAutocomplete("9780262033848", scraper);

		assertEquals(0, unsupported.calls);
		assertEquals(1, supported.calls);
		assertEquals("9780262033848", supported.lastContext.getSelectedText());
	}

	/**
	 *
	 */
	@Test
	public void testIsbnFallsBackAfterSupportedScraperFailure() {
		final FailingScraper failing = new FailingScraper();
		final RecordingScraper supported = new RecordingScraper(true, true);
		final CompositeScraper<Scraper> scraper = new CompositeScraper<Scraper>();
		scraper.addScraper(failing);
		scraper.addScraper(supported);

		runAutocomplete("9780262033848", scraper);

		assertEquals(1, failing.calls);
		assertEquals(1, supported.calls);
		assertEquals("9780262033848", supported.lastContext.getSelectedText());
	}

	/**
	 *
	 */
	@Test
	public void testDoiUsesInjectedScraperChain() {
		final RecordingScraper scraper = runAutocomplete("10.1038/nature12373");

		assertEquals(1, scraper.calls);
		assertNull(scraper.lastContext.getUrl());
		assertEquals("10.1038/nature12373", scraper.lastContext.getSelectedText());
	}

	private static RecordingScraper runAutocomplete(final String search) {
		final RecordingScraper scraper = new RecordingScraper();
		runAutocomplete(search, scraper);
		return scraper;
	}

	private static void runAutocomplete(final String search, final Scraper scraper) {
		final PublicationAutocompleteController controller = new PublicationAutocompleteController();
		controller.setScrapers(scraper);

		final PublicationAutocompleteCommand command = controller.instantiateCommand();
		command.setFormat(Views.FORMAT_STRING_BIBTEX);
		command.setSearch(search);

		assertEquals(Views.BIBTEX, controller.workOn(command));
		assertEquals(1, command.getBibtex().getList().size());
		assertEquals("Scraped Publication", command.getBibtex().getList().get(0).getResource().getTitle());
	}

	private static final class RecordingScraper implements Scraper {
		private final boolean supports;
		private final boolean scrapeResult;
		private int calls;
		private ScrapingContext lastContext;

		private RecordingScraper() {
			this(true, true);
		}

		private RecordingScraper(final boolean supports, final boolean scrapeResult) {
			this.supports = supports;
			this.scrapeResult = scrapeResult;
		}

		@Override
		public boolean scrape(final ScrapingContext scrapingContext) throws ScrapingException {
			this.calls++;
			this.lastContext = scrapingContext;
			scrapingContext.setBibtexResult(BIBTEX);
			scrapingContext.setScraper(this);
			return this.scrapeResult;
		}

		@Override
		public String getInfo() {
			return "recording scraper";
		}

		@Override
		public Collection<Scraper> getScraper() {
			return Collections.<Scraper> singleton(this);
		}

		@Override
		public boolean supportsScrapingContext(final ScrapingContext scrapingContext) {
			return this.supports;
		}
	}

	private static final class FailingScraper implements Scraper {
		private int calls;

		@Override
		public boolean scrape(final ScrapingContext scrapingContext) throws ScrapingException {
			this.calls++;
			throw new ScrapingException("simulated scraper failure");
		}

		@Override
		public String getInfo() {
			return "failing scraper";
		}

		@Override
		public Collection<Scraper> getScraper() {
			return Collections.<Scraper> singleton(this);
		}

		@Override
		public boolean supportsScrapingContext(final ScrapingContext scrapingContext) {
			return true;
		}
	}
}
