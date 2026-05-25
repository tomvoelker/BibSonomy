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

import static org.bibsonomy.util.ValidationUtils.present;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.junit.RemoteTest;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.util.StringUtils;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import bibtex.dom.BibtexAbstractEntry;
import bibtex.dom.BibtexAbstractValue;
import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.dom.BibtexString;
import bibtex.parser.BibtexParser;

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
		assertSemanticMatch(NATURE_EXPECTED, context.getBibtexResult());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testNatureArticleDoiSelection() throws Exception {
		final ScrapingContext context = new ScrapingContext(null, NATURE_DOI);

		assertTrue(scraper.scrape(context));
		assertSemanticMatch(NATURE_EXPECTED, context.getBibtexResult());
	}

	private static void assertSemanticMatch(final String expectedResource, final String actualBibTeX) throws Exception {
		final BibtexEntry expected = parseFirst(getExpectedBibTeX(expectedResource));
		final List<BibtexEntry> actualEntries = parse(actualBibTeX);
		assertFalse("Zotero did not return any BibTeX entries", actualEntries.isEmpty());

		final BibtexEntry actual = findMatchingEntry(expected, actualEntries);
		assertEquals(normalize(field(expected, "doi")), normalize(field(actual, "doi")));
		assertEquals(normalize(field(expected, "title")), normalize(field(actual, "title")));
		assertEquals(normalize(field(expected, "year")), normalize(field(actual, "year")));

		final String expectedAuthor = field(expected, "author");
		final String actualAuthor = field(actual, "author");
		if (present(expectedAuthor)) {
			final String firstExpectedLastName = expectedAuthor.split(",")[0];
			assertTrue("Actual author list does not contain " + firstExpectedLastName, normalize(actualAuthor).contains(normalize(firstExpectedLastName)));
		}
	}

	private static BibtexEntry findMatchingEntry(final BibtexEntry expected, final List<BibtexEntry> actualEntries) {
		final String expectedDoi = normalize(field(expected, "doi"));
		if (present(expectedDoi)) {
			for (final BibtexEntry actualEntry : actualEntries) {
				if (expectedDoi.equals(normalize(field(actualEntry, "doi")))) {
					return actualEntry;
				}
			}
		}
		return actualEntries.get(0);
	}

	private static BibtexEntry parseFirst(final String bibTeX) throws Exception {
		return parse(bibTeX).get(0);
	}

	private static List<BibtexEntry> parse(final String bibTeX) throws Exception {
		final BibtexFile bibtexFile = new BibtexFile();
		final BibtexParser parser = new BibtexParser(true);
		parser.parse(bibtexFile, new BufferedReader(new StringReader(bibTeX)));

		final List<BibtexEntry> entries = new ArrayList<BibtexEntry>();
		for (final BibtexAbstractEntry entry : bibtexFile.getEntries()) {
			if (entry instanceof BibtexEntry) {
				entries.add((BibtexEntry) entry);
			}
		}
		return entries;
	}

	private static String field(final BibtexEntry entry, final String name) {
		for (final Map.Entry<String, BibtexAbstractValue> field : entry.getFields().entrySet()) {
			if (field.getKey().equalsIgnoreCase(name)) {
				if (field.getValue() instanceof BibtexString) {
					return ((BibtexString) field.getValue()).getContent();
				}
				return field.getValue().toString();
			}
		}
		return null;
	}

	private static String normalize(final String value) {
		if (!present(value)) {
			return value;
		}
		return value.toLowerCase()
						.replaceAll("[{}\"']", "")
						.replaceAll("\\s+", " ")
						.trim();
	}

	private static String getExpectedBibTeX(final String resultFile) throws IOException {
		try (final InputStream in = ZoteroTranslationServerScraperRemoteTest.class.getClassLoader().getResourceAsStream("org/bibsonomy/scraper/data/" + resultFile)) {
			return StringUtils.getStringFromReader(new BufferedReader(new InputStreamReader(in, StringUtils.DEFAULT_CHARSET)));
		}
	}
}
