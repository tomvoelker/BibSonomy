/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.util.StringUtils;

import bibtex.dom.BibtexAbstractEntry;
import bibtex.dom.BibtexAbstractValue;
import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.dom.BibtexPerson;
import bibtex.dom.BibtexPersonList;
import bibtex.dom.BibtexString;
import bibtex.expansions.MacroReferenceExpander;
import bibtex.expansions.PersonListExpander;
import bibtex.parser.BibtexParser;

/**
 * Semantic BibTeX comparison for Zotero migration tests.
 *
 * @author tvolker
 */
final class ZoteroSemanticBibTeXAssert {

	private ZoteroSemanticBibTeXAssert() {
		// utility class
	}

	static void assertSemanticMatch(final String expectedResource, final String actualBibTeX) throws Exception {
		assertSemanticMatch(expectedResource, actualBibTeX, expectedResource);
	}

	static void assertSemanticMatch(final String expectedResource, final String actualBibTeX, final String label) throws Exception {
		final List<BibtexEntry> expectedEntries = parse(getExpectedBibTeX(expectedResource));
		final List<BibtexEntry> actualEntries = parse(actualBibTeX);
		assertFalse(label + ": expected fixture has no BibTeX entries", expectedEntries.isEmpty());
		assertFalse(label + ": Zotero did not return any BibTeX entries", actualEntries.isEmpty());

		final List<BibtexEntry> remainingActualEntries = new ArrayList<BibtexEntry>(actualEntries);
		for (final BibtexEntry expected : expectedEntries) {
			final BibtexEntry matchingEntry = findBestMatchingEntry(expected, remainingActualEntries);
			assertSemanticEntryMatch(expected, matchingEntry, label);
			remainingActualEntries.remove(matchingEntry);
		}
	}

	private static void assertSemanticEntryMatch(final BibtexEntry expected, final BibtexEntry actual, final String label) {
		final String expectedDoi = normalizeDoi(field(expected, "doi"));
		if (present(expectedDoi)) {
			assertTrue(label + ": expected DOI " + expectedDoi + " not found in actual entry", expectedDoi.equals(normalizeDoi(field(actual, "doi"))));
		}

		final String expectedIsbn = normalizeIsbn(field(expected, "isbn"));
		if (present(expectedIsbn)) {
			assertTrue(label + ": expected ISBN " + expectedIsbn + " not found in actual entry", isbnMatches(expectedIsbn, normalizeIsbn(field(actual, "isbn"))));
		}

		final String expectedTitle = canonicalTitle(field(expected, "title"));
		if (present(expectedTitle)) {
			assertTrue(label + ": title mismatch\nExpected: " + field(expected, "title") + "\nActual  : " + field(actual, "title"), titleMatches(expectedTitle, canonicalTitle(field(actual, "title"))));
		}

		final String expectedYear = normalizeSimple(field(expected, "year"));
		if (present(expectedYear)) {
			assertTrue(label + ": year mismatch\nExpected: " + field(expected, "year") + "\nActual  : " + field(actual, "year"), expectedYear.equals(normalizeSimple(field(actual, "year"))));
		}

		final String expectedContributor = firstContributor(expected);
		if (present(expectedContributor)) {
			assertTrue(label + ": contributor mismatch\nExpected: " + expectedContributor + "\nActual  : " + contributors(actual), contributors(actual).contains(normalizeSimple(expectedContributor)));
		}
	}

	private static BibtexEntry findBestMatchingEntry(final BibtexEntry expected, final List<BibtexEntry> actualEntries) {
		BibtexEntry best = actualEntries.get(0);
		int bestScore = -1;
		for (final BibtexEntry actual : actualEntries) {
			final int score = score(expected, actual);
			if (score > bestScore) {
				best = actual;
				bestScore = score;
			}
		}
		return best;
	}

	private static int score(final BibtexEntry expected, final BibtexEntry actual) {
		int score = 0;
		final String expectedDoi = normalizeDoi(field(expected, "doi"));
		if (present(expectedDoi) && expectedDoi.equals(normalizeDoi(field(actual, "doi")))) {
			score += 4;
		}
		final String expectedIsbn = normalizeIsbn(field(expected, "isbn"));
		if (present(expectedIsbn) && isbnMatches(expectedIsbn, normalizeIsbn(field(actual, "isbn")))) {
			score += 3;
		}
		if (titleMatches(canonicalTitle(field(expected, "title")), canonicalTitle(field(actual, "title")))) {
			score += 2;
		}
		if (normalizeSimple(field(expected, "year")).equals(normalizeSimple(field(actual, "year")))) {
			score++;
		}
		final String expectedContributor = firstContributor(expected);
		if (present(expectedContributor) && contributors(actual).contains(normalizeSimple(expectedContributor))) {
			score++;
		}
		return score;
	}

	static List<BibtexEntry> parse(final String bibTeX) throws Exception {
		final BibtexFile bibtexFile = new BibtexFile();
		final BibtexParser parser = new BibtexParser(true);
		parser.parse(bibtexFile, new BufferedReader(new StringReader(bibTeX)));

		final MacroReferenceExpander macroReferenceExpander = new MacroReferenceExpander(true, true, true);
		macroReferenceExpander.expand(bibtexFile);
		final PersonListExpander personListExpander = new PersonListExpander(true, true);
		personListExpander.expand(bibtexFile);

		final List<BibtexEntry> entries = new ArrayList<BibtexEntry>();
		for (final BibtexAbstractEntry entry : bibtexFile.getEntries()) {
			if (entry instanceof BibtexEntry) {
				entries.add((BibtexEntry) entry);
			}
		}
		return entries;
	}

	private static String field(final BibtexEntry entry, final String name) {
		final BibtexAbstractValue value = fieldValue(entry, name);
		if (value == null) {
			return null;
		}
		if (value instanceof BibtexString) {
			return ((BibtexString) value).getContent();
		}
		if (value instanceof BibtexPersonList) {
			return personList((BibtexPersonList) value);
		}
		return value.toString();
	}

	private static BibtexAbstractValue fieldValue(final BibtexEntry entry, final String name) {
		for (final Map.Entry<String, BibtexAbstractValue> field : entry.getFields().entrySet()) {
			if (field.getKey().equalsIgnoreCase(name)) {
				return field.getValue();
			}
		}
		return null;
	}

	private static String firstContributor(final BibtexEntry entry) {
		String contributor = firstPerson(fieldValue(entry, "author"));
		if (!present(contributor)) {
			contributor = firstPerson(fieldValue(entry, "editor"));
		}
		return contributor;
	}

	private static String contributors(final BibtexEntry entry) {
		return normalizeSimple(field(entry, "author") + " " + field(entry, "editor"));
	}

	private static String firstPerson(final BibtexAbstractValue value) {
		if (value instanceof BibtexPersonList) {
			final List<BibtexPerson> persons = ((BibtexPersonList) value).getList();
			if (!persons.isEmpty()) {
				return person(persons.get(0));
			}
		}
		return null;
	}

	private static String personList(final BibtexPersonList persons) {
		final StringBuilder builder = new StringBuilder();
		for (final Object personObject : persons.getList()) {
			if (builder.length() > 0) {
				builder.append(' ');
			}
			builder.append(person((BibtexPerson) personObject));
		}
		return builder.toString();
	}

	private static String person(final BibtexPerson person) {
		if (person == null) {
			return "";
		}
		if (present(person.getLast())) {
			return person.getLast();
		}
		if (present(person.getFirst())) {
			return person.getFirst();
		}
		return person.toString();
	}

	private static boolean titleMatches(final String expectedTitle, final String actualTitle) {
		if (!present(expectedTitle) || !present(actualTitle)) {
			return !present(expectedTitle) && !present(actualTitle);
		}
		if (expectedTitle.equals(actualTitle) || expectedTitle.contains(actualTitle) || actualTitle.contains(expectedTitle)) {
			return true;
		}

		final Set<String> expectedTokens = titleTokens(expectedTitle);
		final Set<String> actualTokens = titleTokens(actualTitle);
		if (expectedTokens.isEmpty() || actualTokens.isEmpty()) {
			return false;
		}
		int matches = 0;
		for (final String token : expectedTokens) {
			if (actualTokens.contains(token)) {
				matches++;
			}
		}
		return ((double) matches / (double) expectedTokens.size()) >= 0.75;
	}

	private static Set<String> titleTokens(final String value) {
		final Set<String> tokens = new HashSet<String>();
		for (final String token : value.split(" ")) {
			if (token.length() > 2) {
				tokens.add(token);
			}
		}
		return tokens;
	}

	private static boolean isbnMatches(final String expectedIsbn, final String actualIsbn) {
		return present(expectedIsbn) && present(actualIsbn) && (actualIsbn.contains(expectedIsbn) || expectedIsbn.contains(actualIsbn));
	}

	private static String canonicalTitle(final String value) {
		return normalizeAscii(value)
						.replaceAll("[^a-z0-9]+", " ")
						.replaceAll("\\s+", " ")
						.trim();
	}

	private static String normalizeSimple(final String value) {
		return normalizeAscii(value)
						.replaceAll("[{}\"']", "")
						.replaceAll("\\s+", " ")
						.trim();
	}

	private static String normalizeDoi(final String value) {
		return normalizeSimple(value)
						.replaceFirst("^https?://(?:dx\\.)?doi\\.org/", "")
						.replaceFirst("^doi:\\s*", "")
						.replaceAll("\\s+", "")
						.toLowerCase(Locale.ENGLISH);
	}

	private static String normalizeIsbn(final String value) {
		return normalizeSimple(value).replaceAll("[^0-9x]", "");
	}

	private static String normalizeAscii(final String value) {
		if (!present(value)) {
			return "";
		}
		return Normalizer.normalize(value, Normalizer.Form.NFD)
						.replaceAll("\\p{M}", "")
						.toLowerCase(Locale.ENGLISH);
	}

	private static String getExpectedBibTeX(final String resultFile) throws IOException {
		try (final InputStream in = ZoteroSemanticBibTeXAssert.class.getClassLoader().getResourceAsStream("org/bibsonomy/scraper/data/" + resultFile)) {
			if (in == null) {
				throw new IOException("No fixture found at org/bibsonomy/scraper/data/" + resultFile);
			}
			return StringUtils.getStringFromReader(new BufferedReader(new InputStreamReader(in, StringUtils.DEFAULT_CHARSET)));
		}
	}
}
