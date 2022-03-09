/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 * <p>
 * Copyright (C) 2006 - 2021 Data Science Chair,
 * University of Würzburg, Germany
 * https://www.informatik.uni-wuerzburg.de/datascience/home/
 * Information Processing and Analytics Group,
 * Humboldt-Universität zu Berlin, Germany
 * https://www.ibi.hu-berlin.de/en/research/Information-processing/
 * Knowledge & Data Engineering Group,
 * University of Kassel, Germany
 * https://www.kde.cs.uni-kassel.de/
 * L3S Research Center,
 * Leibniz University Hannover, Germany
 * https://www.l3s.de/
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scraper.junit;

import static org.bibsonomy.util.ValidationUtils.present;
import static org.junit.Assert.assertEquals;
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
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.StringUtils;
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
import java.util.Locale;
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
	 * calls the specified scraper with the provided url and tests the returned result of the scraper
	 * with the contents of the provided result file
	 *
	 * @param url
	 * @param scraperClass
	 * @param resultFile
	 */
	public static void assertScraperResult(final String url, final Class<? extends Scraper> scraperClass, final String resultFile) {
		assertScraperResult(url, null, scraperClass, resultFile);
	}

	/**
	 * calls the specified scraper with the provided url and selection and tests the returned result of the scraper
	 * with the contents of the provided result file
	 *
	 * @param url
	 * @param selection
	 * @param scraperClass
	 * @param resultFile
	 */
	public static void assertScraperResult(final String url, final String selection, final Class<? extends Scraper> scraperClass, final String resultFile) {
		//Creating instance of the scraper-class
		final Scraper scraper;
		try {
			scraper = createScraper(scraperClass);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Exception while creating instance of Scraper", e);
		}

		//Preparing test data
		final String expectedReference;
		final List<BibtexEntry> expectedBibtexEntries;
		try {
			expectedReference = normBibTeX(getExpectedBibTeX(resultFile));
			expectedBibtexEntries = parseAndExpandBibTeXs(expectedReference);
		} catch (IOException | ExpansionException | ParseException | AssertionError e) {
			throw new RuntimeException("Exception while preparing test data", e);
		}

		final String bibTeXResult;
		final List<BibtexEntry> actualBibtexEntries;
		try {
			bibTeXResult = normBibTeX(getScraperResult(url, selection, scraper));
			actualBibtexEntries = parseAndExpandBibTeXs(bibTeXResult);
		} catch (IOException | ScrapingException | ExpansionException | ParseException | AssertionError e) {
			throw new RuntimeException("Exception while preparing scraped data from " + url, e);
		}

		try {
			assertEqualsBibtexEntryList(expectedBibtexEntries, actualBibtexEntries);
		} catch (AssertionError ae) {
			ComparisonFailure cf = new ComparisonFailure(ae.getMessage(), expectedReference, bibTeXResult);
			cf.initCause(ae.getCause());
			throw cf;
		}


		//the scraper can work for an old URL, but not for the redirected. This is problematic, because the user will use the redirected URL.
		final URL redirectUrl;
		try {
			redirectUrl = WebUtils.getRedirectUrl(new URL(url));
		} catch (MalformedURLException | IllegalArgumentException e) {
			throw new RuntimeException("Exception while getting redirected Url", e);
		}

		if (redirectUrl != null && !redirectUrl.toString().equals(url)) {
			final String redirectedBibTeXResult;
			final List<BibtexEntry> redirectedBibtexEntries;
			try {
				redirectedBibTeXResult = normBibTeX(getScraperResult(redirectUrl.toString(), selection, scraper));
				redirectedBibtexEntries = parseAndExpandBibTeXs(redirectedBibTeXResult);
			} catch (IOException | ParseException | ExpansionException | ScrapingException | AssertionError e) {
				throw new RuntimeException("Exception while preparing scraped data from redirected url " + redirectUrl, e);
			}
			try {
				assertEqualsBibtexEntryList(expectedBibtexEntries, redirectedBibtexEntries);
			} catch (AssertionError ae) {
				ComparisonFailure cf = new ComparisonFailure(ae.getMessage(), expectedReference, redirectedBibTeXResult);
				cf.initCause(ae.getCause());
				throw cf;
			}

		}

	}

	private static String getScraperResult(final String url, final String selection, final Scraper scraper) throws MalformedURLException, ScrapingException {
		final ScrapingContext scrapingContext = createScraperContext(url, selection);
		scraper.scrape(scrapingContext);
		final String bibTeXResult = scrapingContext.getBibtexResult();

		if (!present(bibTeXResult)) {
			throw new AssertionError("nothing scraped from " + url);
		}

		return bibTeXResult;
	}

	protected static List<BibtexEntry> parseAndExpandBibTeXs(final String bibtex) throws IOException, ParseException, ExpansionException {
		final BibtexFile bibtexFile = new BibtexFile();

		final BibtexParser parser = new BibtexParser(true);
		final PersonListExpander personListExpander = new PersonListExpander(true, true);
		final MacroReferenceExpander macroReferenceExpander = new MacroReferenceExpander(true, true, true);

		parser.parse(bibtexFile, new BufferedReader(new StringReader(bibtex)));

		final boolean bibtexValid = bibtexFile.getEntries().stream().anyMatch(BibtexEntry.class::isInstance);
		assertTrue("scraped BibTeX not valid", bibtexValid);

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

	private static String getExpectedBibTeX(final String resultFile) throws IOException {
		try (final InputStream in = RemoteTestAssert.class.getClassLoader().getResourceAsStream("org/bibsonomy/scraper/data/" + resultFile)) {
			return StringUtils.getStringFromReader(new BufferedReader(new InputStreamReader(in, StringUtils.DEFAULT_CHARSET)));
		}
	}

	private static Scraper createScraper(final Class<? extends Scraper> scraperClass) throws InstantiationException, IllegalAccessException {
		return scraperClass.newInstance();
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
	 *
	 * @param expected List of different BibTeXEntries
	 * @param actual   List of different BibTeXEntries
	 * @throws AssertionError differences between the two lists
	 */
	protected static void assertEqualsBibtexEntryList(List<BibtexEntry> expected, List<BibtexEntry> actual) throws AssertionError {
		assertEquals("Expected and actual don't contain the same amount of BibTeXs", expected.size(), actual.size());
		//makes sure that for every expected BibTeX an actual BibTeX exists. BibTeXs are identified by their key
		for (BibtexEntry expectedEntry : expected) {
			assertTrue("actual String does not contain Bibtex with key \"" + expectedEntry.getEntryKey() + "\"",
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

	protected static void assertEqualsBibtexEntry(final BibtexEntry expected, final BibtexEntry actual) throws AssertionError {
		// BibtexPerson doesn't implement a custom equals method and is also final, so we can't create a comparableBibtexPerson
		// The comparator sorts first after the natural Order of the first name and then after the second name
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

		assertEquals(
						"\nExpected entrytype was: " + expected.getEntryType() +
										"\nActual entrytype was:   " + actual.getEntryType(),
						expected.getEntryType(),
						actual.getEntryType());
		assertEquals(
						"\nExpected entrykey was: " + expected.getEntryType() +
										"\nActual entrykey was:   " + actual.getEntryType(),
						expected.getEntryKey(),
						actual.getEntryKey());

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
				String expectedBibtexString = ((BibtexString) expectedValue).getContent().trim();
				String actualBibtexString = ((BibtexString) actualValue).getContent().trim();

				switch (key.toLowerCase(Locale.ROOT)) {
					case "keywords":
						//the order of keywords are for some sites randomized, so we can't compare only the string
						final String[] expectedKeywords = expectedBibtexString.split("\\s*[,;]\\s*");
						final String[] actualKeywords = actualBibtexString.split("\\s*[,;]\\s*");
						checkAsymmetricDifferenceBothWays(Arrays.asList(expectedKeywords), Arrays.asList(actualKeywords), null);
						break;
					case "url":
						//ignore the used protocol
						expectedBibtexString = expectedBibtexString.replaceAll("http://", "https://");
						actualBibtexString = actualBibtexString.replaceAll("http://", "https://");
						//fall through
					default:
						//compares the string-representation of BibtexString
						if (!expectedBibtexString.equals(actualBibtexString)) {
							throw new AssertionError("Different values at tag: \"" + key +
											"\"\nExpected: \"" + expectedBibtexString +
											"\"\nActual:   \"" + actualBibtexString + "\"");
						}
				}
			} else if (expectedValue instanceof BibtexPersonList && actualValue instanceof BibtexPersonList) {
				try {
					// asymmetric difference both ways to get extra or missing BibtexPersons
					checkAsymmetricDifferenceBothWays(((BibtexPersonList) expectedValue).getList(), ((BibtexPersonList) actualValue).getList(), bibtexPersonComparator);
				} catch (AssertionError ae) {
					throw new AssertionError("Different values at tag: \"" + key + "\"", ae);
				}
			} else {
				// should never be thrown, except if a for me unknown BibtexAbstractValue-Class exists
				throw new IllegalStateException(
								"Expected classes were neither BibtexPersonList nor BibtexString\n" +
												"The class of expected was: \"" + expectedValue.getClass() + "\"\n " +
												"The class of actual was:   \"" + actualValue.getClass() + "\"");
			}
		}
	}

	/**
	 * Caution: If a comparator is used the equals and compare methods could have different outputs
	 * Converts both collections to sets and makes the asymmetric difference both ways.
	 * Should the collections contain the same elements (duplicates are ignored) then no error is thrown.
	 *
	 * @param expected the expected collection
	 * @param actual   the actual collection
	 * @param comp     the comparator, which should be used, if equals and hashcode should not be used
	 * @throws AssertionError contains the differences of both collections
	 */
	protected static void checkAsymmetricDifferenceBothWays(final Collection expected, final Collection actual, final Comparator comp) {
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
