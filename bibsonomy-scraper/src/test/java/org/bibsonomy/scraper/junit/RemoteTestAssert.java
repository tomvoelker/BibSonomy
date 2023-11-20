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
package org.bibsonomy.scraper.junit;

import static org.bibsonomy.util.ValidationUtils.present;
import static org.junit.Assert.assertTrue;
import bibtex.dom.BibtexAbstractEntry;
import bibtex.dom.BibtexAbstractValue;
import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.dom.BibtexPerson;
import bibtex.dom.BibtexPersonList;
import bibtex.dom.BibtexString;
import bibtex.expansions.ExpansionException;
import bibtex.expansions.MacroReferenceExpander;
import bibtex.expansions.PersonListExpander;
import bibtex.parser.BibtexParser;
import bibtex.parser.ParseException;
import org.apache.commons.lang.ObjectUtils;
import org.bibsonomy.scraper.KDEUrlCompositeScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.WebUtils;
import org.junit.ComparisonFailure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * asserts for remote tests
 *
 * @author dzo
 */
public class RemoteTestAssert {

	/**
	 * Scrapes the specified url with the expectedScraperClass. Should the expectedScraperClass implement the UrlScraper-Interface then
	 * the KDEUrlCompositeScraper will be used for scraping.
	 * The returned result of the scraper will be tested against the contents of the provided result file
	 * @param url
	 * @param expectedScraperClass the scraper, which should be set in the Scrapingcontext
	 * @param resultFile
	 */
	public static void assertScraperResult(final String url, final Class<? extends Scraper> expectedScraperClass, final String resultFile) {
		assertScraperResult(url, null, expectedScraperClass, resultFile, true);
	}

	/**
	 * Scrapes the specified url with the expectedScraperClass. Should the expectedScraperClass implement the UrlScraper-Interface then
	 * the KDEUrlCompositeScraper will be used for scraping.
	 * The returned result of the scraper will be tested against the contents of the provided result file
	 * @param url
	 * @param selection
	 * @param expectedScraperClass the scraper, which should be set in the Scrapingcontext
	 * @param resultFile
	 */
	public static void assertScraperResult(final String url, final String selection, final Class<? extends Scraper> expectedScraperClass, final String resultFile) {
		assertScraperResult(url, selection, expectedScraperClass, resultFile, true);
	}


	/**
	 * Scrapes the specified url and the selection with the expectedScraperClass. Should the expectedScraperClass implement the UrlScraper-Interface then
	 * the KDEUrlCompositeScraper will be used for scraping.
	 * The returned result of the scraper will be tested against the contents of the provided result file
	 * @param url
	 * @param selection
	 * @param expectedScraperClass  the scraper, which should be set in the Scrapingcontext
	 * @param resultFile
	 * @param testRedirectedUrl should the bibtex, which the scraper gets from redirected url, be tested
	 */
	public static void assertScraperResult(final String url, final String selection, final Class<? extends Scraper> expectedScraperClass, final String resultFile, final boolean testRedirectedUrl) {
		//Preparing test data
		final String expectedReference;
		final List<BibtexEntry> expectedBibtexEntries;
		try {
			expectedReference = getExpectedBibTeX(resultFile);
			expectedBibtexEntries = parseAndExpandBibTeXs(expectedReference);
		} catch (IOException | ExpansionException | ParseException | AssertionError e) {
			throw new RuntimeException("Exception while getting and preparing test data", e);
		}
		//Getting the scraping-result and preparing it to be tested
		final String scraperResult;
		final List<BibtexEntry> actualBibtexEntries;
		try {
			scraperResult = getScraperResult(url, selection, expectedScraperClass);
			actualBibtexEntries = parseAndExpandBibTeXs(scraperResult);
		} catch (IOException | ScrapingException | ExpansionException | ParseException | AssertionError | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Exception while scraping and preparing the scraping-result", e);
		}
		//comparing expected and actual bibtex
		try {
			assertEqualsBibtexEntryList(expectedBibtexEntries, actualBibtexEntries);
		} catch (AssertionError ae) {
			ComparisonFailure cf = new ComparisonFailure("Actual Bibtex is not equal to expected Bibtex",
							expectedReference,
							scraperResult);
			cf.initCause(ae);
			throw cf;
		}

		if (testRedirectedUrl&&url != null && UrlScraper.class.isAssignableFrom(expectedScraperClass)) {
			//the scraper can work for an old URL, but not for the redirected. This is problematic, because the user will use the redirected URL.
			final URL redirectUrl;
			try {
				redirectUrl = WebUtils.getRedirectUrl(new URL(url));
			} catch (MalformedURLException | IllegalArgumentException e) {
				throw new RuntimeException("Exception while getting redirected Url", e);
			}

			if (redirectUrl != null && !redirectUrl.toString().equals(url)) {
				final String redirectedScraperResult;
				final List<BibtexEntry> redirectedBibtexEntries;
				try {
					redirectedScraperResult = getScraperResult(redirectUrl.toString(), selection, expectedScraperClass);
					redirectedBibtexEntries = parseAndExpandBibTeXs(redirectedScraperResult);
				} catch (IOException | ParseException | ExpansionException | ScrapingException | AssertionError | InstantiationException | IllegalAccessException e) {
					throw new RuntimeException("Exception while scraping and preparing data to test from redirected url " + redirectUrl, e);
				}
				try {
					assertEqualsBibtexEntryList(expectedBibtexEntries, redirectedBibtexEntries);
				} catch (AssertionError ae) {
					ComparisonFailure cf = new ComparisonFailure(
									"Bibtex from redirected Url " + redirectUrl + " is not equal to expected Bibtex",
									expectedReference,
									redirectedScraperResult);
					cf.initCause(ae);
					throw cf;
				}
			}
		}

	}

	private static String getScraperResult(final String url, final String selection, final Class<? extends Scraper> expectedScraperClass) throws MalformedURLException, ScrapingException, InstantiationException, IllegalAccessException {
		final Scraper scraper = createScraper(expectedScraperClass);
		final ScrapingContext scrapingContext = createScraperContext(url, selection);
		scraper.scrape(scrapingContext);

		final String bibTeXResult = scrapingContext.getBibtexResult();

		if (!present(bibTeXResult)) {
			throw new AssertionError("Nothing scraped from " + url);
		}

		Scraper usedScraper = scrapingContext.getScraper();
		if (usedScraper == null) {
			throw new AssertionError("No Scraper was set");
		}

		if (!usedScraper.getClass().equals(expectedScraperClass)) {
			throw new AssertionError("Not the expected Scraper was used\n" +
							"Expected: " + expectedScraperClass.getSimpleName() +
							"\nActual  : " + usedScraper.getClass().getSimpleName());
		}
		return normBibTeX(bibTeXResult);
	}

	protected static List<BibtexEntry> parseAndExpandBibTeXs(final String bibtex) throws IOException, ParseException, ExpansionException {
		final BibtexFile bibtexFile = new BibtexFile();

		final BibtexParser parser = new BibtexParser(true);
		/*
		No BibtexMultipleFieldValuesPolicy is set, so the default is used and only the first occurrence of the field is used
		 */
		parser.parse(bibtexFile, new BufferedReader(new StringReader(bibtex)));
		//if no bibtex is in the bibtexFile then the bibtex was not valid
		final boolean bibtexValid = bibtexFile.getEntries().stream().anyMatch(BibtexEntry.class::isInstance);
		assertTrue("Scraped BibTeX not valid", bibtexValid);

		final PersonListExpander personListExpander = new PersonListExpander(true, true);
		final MacroReferenceExpander macroReferenceExpander = new MacroReferenceExpander(true, true, true);
		macroReferenceExpander.expand(bibtexFile);
		personListExpander.expand(bibtexFile);

		final ArrayList<? extends BibtexAbstractEntry> bibtexFileEntries = new ArrayList<>(bibtexFile.getEntries());
		bibtexFileEntries.removeIf(entry -> !(entry instanceof BibtexEntry));

		return (List<BibtexEntry>) bibtexFileEntries;
	}

	private static String normBibTeX(final String bibTeX) {
		if (!present(bibTeX)) {
			return bibTeX;
		}
		final String normedLineBreaks = bibTeX.replaceAll("\\r\\n", "\n").trim();
		return normedLineBreaks.replaceAll("(?m)^[\\s&&[^\\n]]+|[\\s+&&[^\\n]]+$", "");
	}

	private static String normUrl(final String url){
		return url.replaceAll("http://", "https://")
						.replaceAll("#\\S*$", "")
						.replaceAll("/$", "");
	}

	private static String getExpectedBibTeX(final String resultFile) throws IOException {
		try (final InputStream in = RemoteTestAssert.class.getClassLoader().getResourceAsStream("org/bibsonomy/scraper/data/" + resultFile)) {
			return normBibTeX(StringUtils.getStringFromReader(new BufferedReader(new InputStreamReader(in, StringUtils.DEFAULT_CHARSET))));
		}
	}

	protected static Scraper createScraper(final Class<? extends Scraper> scraperClass) throws InstantiationException, IllegalAccessException {
		final Scraper scraper;
		//if the scraper is a URL-Scraper, we use the KDEUrlCompositeScraper and then check if the correct scraper was used in the composite
		if (UrlScraper.class.isAssignableFrom(scraperClass)) {
			scraper = new KDEUrlCompositeScraper();
			//check if KDEUrlCompositeScraper contains the expected-scraper
			Collection<Scraper> urlScrapers = scraper.getScraper();
			boolean foundExpectedScraper = false;
			for (Scraper urlScraper : urlScrapers) {
				if (urlScraper.getClass().equals(scraperClass)){
					foundExpectedScraper = true;
					break;
				}
			}
			if (!foundExpectedScraper){
				throw new RuntimeException("KDEUrlCompositeScraper does not contain " + scraperClass.getSimpleName());
			}
		} else {
			scraper = scraperClass.newInstance();
		}
		return scraper;
	}

	private static ScrapingContext createScraperContext(final String url, final String selection) throws MalformedURLException {
		final URL testURL;
		if (present(url)) {
			testURL = new URL(url);
		} else {
			testURL = null;
		}
		final ScrapingContext testSC = new ScrapingContext(testURL);
		if (selection != null) {
			testSC.setSelectedText(selection);
		}
		return testSC;
	}

	/**
	 * compares two lists of bibtexEntries. Each BibtexEntry in expected is compared to the BibtexEntry in actual with the same Bibtex-key
	 * @param expected List of different BibTeXEntries
	 * @param actual   List of different BibTeXEntries
	 * @throws AssertionError differences between the two lists
	 */
	protected static void assertEqualsBibtexEntryList(List<BibtexEntry> expected, List<BibtexEntry> actual) throws AssertionError {
		if (expected.size() != actual.size()) {
			throw new AssertionError("Expected and actual don't contain the same amount of BibTeXs" +
							"\nExpected: " + expected.size() +
							"\nActual  : " + actual.size());
		}

		//makes sure that for every expected BibTeX an actual BibTeX exists. BibTeXs are identified by their key
		for (BibtexEntry expectedEntry : expected) {
			assertTrue("Actual String does not contain Bibtex with key " + expectedEntry.getEntryKey(),
							actual.stream().anyMatch(a -> a.getEntryKey().equals(expectedEntry.getEntryKey())));
		}

		for (BibtexEntry expectedEntry : expected) {
			for (BibtexEntry actualEntry : actual) {
				if (expectedEntry.getEntryKey().equals(actualEntry.getEntryKey())) {
					assertEqualsBibtexEntry(expectedEntry, actualEntry);
				}
			}
		}
	}

	/**
	 * Compares two BibtexEntries.
	 * @param expected BibtexEntry
	 * @param actual   BibtexEntry
	 * @throws AssertionError
	 */
	protected static void assertEqualsBibtexEntry(final BibtexEntry expected, final BibtexEntry actual) throws AssertionError {
		// BibtexPerson doesn't implement a custom equals method and is also final, so we can't create a comparableBibtexPerson
		// The comparator sorts first after the natural Order of the first name and then after the second name.
		final Comparator<BibtexPerson> bibtexPersonComparator = (o1, o2) -> {
			if (ObjectUtils.equals(o1.getFirst(), o2.getFirst()) && ObjectUtils.equals(o1.getLast(), o2.getLast())) {
				return 0;
			} else {
				if (ObjectUtils.equals(o1.getFirst(), o2.getFirst())) {
					if (o1.getLast() != null && o2.getLast() != null) {
						return String.CASE_INSENSITIVE_ORDER.compare(o1.getLast(), o2.getLast());
					} else if (o1.getLast() != null) {
						return 1;
					} else {
						return -1;
					}
				} else {
					if (o1.getFirst() != null && o2.getFirst() != null) {
						return String.CASE_INSENSITIVE_ORDER.compare(o1.getFirst(), o2.getFirst());
					} else if (o1.getFirst() != null) {
						return 1;
					} else {
						return -1;
					}
				}
			}
		};
		if (!org.apache.commons.lang.StringUtils.equals(expected.getEntryType(), actual.getEntryType())) {
			throw new AssertionError("Bibtex-Entrytypes are not equal" +
							"\nExpected: " + expected.getEntryType() +
							"\nActual  : " + actual.getEntryType());
		}
		if (!org.apache.commons.lang.StringUtils.equals(expected.getEntryKey(), actual.getEntryKey())) {
			throw new AssertionError("Bibtex-Keys are not equal" +
							"\nExpected: " + expected.getEntryKey() +
							"\nActual  : " + actual.getEntryKey());
		}

		final Map<String, BibtexAbstractValue> expectedBibTexValues = expected.getFields();
		final Map<String, BibtexAbstractValue> actualBibTexValues = actual.getFields();

		try {
			// asymmetric difference both ways to get extra or missing keys of actual and expected Bibtex
			checkAsymmetricDifferenceBothWays(expectedBibTexValues.keySet(), actualBibTexValues.keySet(), null);
		} catch (AssertionError ae) {
			throw new AssertionError("Different keys of Bibtex-tags", ae);
		}

		/*
		expectedBibtexValues.keySet() and actualBibtexValues.keySet() should contain the same elements or
		checkAsymmetricDifferenceBothWays should have already thrown an error
		*/
		for (String key : expectedBibTexValues.keySet()) {

			final BibtexAbstractValue expectedValue = expectedBibTexValues.get(key);
			final BibtexAbstractValue actualValue = actualBibTexValues.get(key);

			if (expectedValue instanceof BibtexString && actualValue instanceof BibtexString) {
				try {
					assertEqualsBibtexString(key, (BibtexString) expectedValue, (BibtexString) actualValue);
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
			} else if (expectedValue instanceof BibtexPersonList && actualValue instanceof BibtexPersonList) {
				try {
					// asymmetric difference both ways to get extra or missing BibtexPersons
					checkAsymmetricDifferenceBothWays(
									((BibtexPersonList) expectedValue).getList(),
									((BibtexPersonList) actualValue).getList(),
									bibtexPersonComparator);
				} catch (AssertionError ae) {
					throw new AssertionError("Different values at tag: " + key, ae);
				}
			} else {
				// should never be thrown
				throw new IllegalStateException(
								"Expected classes were neither BibtexPersonList nor BibtexString\n" +
												"The class of expected was: " + expectedValue.getClass().getSimpleName() + "\n " +
												"The class of actual was  : " + actualValue.getClass().getSimpleName());
			}
		}
	}

	/**
	 * Compares two BibtexStrings. If the value is a URL the protocol and anchors are ignored. Additionally, the redirected URL is also accepted.
	 * For the following keys applies
	 * Keywords: ignores the order
	 * Author, Editor: ignores the format and order
	 * @param key
	 * @param expected
	 * @param actual
	 * @throws AssertionError
	 * @throws MalformedURLException
	 */
	protected static void assertEqualsBibtexString(final String key, final BibtexString expected, final BibtexString actual) throws AssertionError, MalformedURLException {
		final String expectedString = expected.getContent().trim();
		final String actualString = actual.getContent().trim();
		if (expectedString.equals(actualString)) {
			return;
		}
		if (UrlUtils.isUrl(expectedString) && UrlUtils.isUrl(actualString)) {
			//ignore the used protocol and anchor
			String expectedUrl = normUrl(expectedString);
			String actualUrl = normUrl(actualString);

			if (expectedUrl.equals(actualUrl)) {
				return;
			}

			URL redirectedUrl = WebUtils.getRedirectUrl(new URL(expectedUrl));
			if (redirectedUrl != null) {
				String redirectedUrlStr = normUrl(redirectedUrl.toString());
				if (redirectedUrlStr.equals(actualUrl)) {
					return;
				}
			}
		} else if (org.apache.commons.lang.StringUtils.containsIgnoreCase(key, "keyword")) {
			//the order of keywords are for some sites randomized.
			final String[] expectedKeywords = expectedString.split("\\s*[,;]\\s*");
			final String[] actualKeywords = actualString.split("\\s*[,;]\\s*");
			checkAsymmetricDifferenceBothWays(Arrays.asList(expectedKeywords), Arrays.asList(actualKeywords), null);
			return;
		}
		throw new AssertionError("Different values at tag: " + key +
						"\nExpected: " + expectedString +
						"\nActual  : " + actualString);
	}

	/**
	 * Caution: If a comparator is used the equals and compare methods could have different outputs
	 * Converts both collections to sets and makes the asymmetric difference both ways.
	 * Should the collections contain the same elements (duplicates are ignored) then no error is thrown.
	 * @param expected the expected collection
	 * @param actual   the actual collection
	 * @param comp     the comparator, which should be used, if equals and hashcode should not be used
	 * @throws AssertionError contains the differences of both collections
	 */
	protected static void checkAsymmetricDifferenceBothWays(final Collection expected, final Collection actual, final Comparator comp) throws AssertionError {
		Set expectedSet;
		Set actualSet;

		if (comp == null) {
			// Hash-values are compared
			expectedSet = new HashSet<>(expected);
			actualSet = new HashSet<>(actual);

			actualSet.removeAll(expectedSet);
			expectedSet.removeAll(actual);
		} else {
			//with TreeSet a comparator can be used, but the basic operations run in O(n)
			expectedSet = new TreeSet<>(comp);
			expectedSet.addAll(expected);
			actualSet = new TreeSet<>(comp);
			actualSet.addAll(actual);

			TreeSet<BibtexPerson> temp = new TreeSet<>(comp);
			temp.addAll(actualSet);
			actualSet.removeAll(expectedSet);
			expectedSet.removeAll(temp);
		}

		if (actualSet.size() != 0 && expectedSet.size() != 0) {
			throw new AssertionError("\nElements not contained in expected Set: " + actualSet + "\nElements not contained in actual Set:   " + expectedSet);
		} else if (actualSet.size() != 0) {
			throw new AssertionError("Elements not contained in expected Set: " + actualSet);
		} else if (expectedSet.size() != 0) {
			throw new AssertionError("Elements not contained in actual Set: " + expectedSet);
		}
	}
}
