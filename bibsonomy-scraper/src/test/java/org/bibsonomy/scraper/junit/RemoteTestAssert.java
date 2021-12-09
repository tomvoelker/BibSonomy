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

import bibtex.dom.*;
import bibtex.expansions.CrossReferenceExpander;
import bibtex.expansions.ExpansionException;
import bibtex.expansions.MacroReferenceExpander;
import bibtex.expansions.PersonListExpander;
import bibtex.parser.BibtexParser;
import bibtex.parser.ParseException;
import org.apache.commons.lang.ObjectUtils;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.util.StringUtils;
import org.junit.ComparisonFailure;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static org.bibsonomy.util.ValidationUtils.present;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * asserts for remote tests
 *
 * @author dzo
 */
public class RemoteTestAssert {

	/**
	 * calls the specified scraper with the provided url and tests the returned result of the scraper
	 * with the contents of the provided result file
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
	 * @param url
	 * @param selection
	 * @param scraperClass
	 * @param resultFile
	 */
	public static void assertScraperResult(final String url, final String selection, final Class<? extends Scraper> scraperClass, final String resultFile) {
		try {
			final Scraper scraper = createScraper(scraperClass);
			final ScrapingContext scrapingContext = createScraperContext(url, selection);
			scraper.scrape(scrapingContext);
			final String bibTeXResult = scrapingContext.getBibtexResult();
			
			if (bibTeXResult != null){
				final BibtexParser parser = new BibtexParser(true);
				final BibtexFile bibtexFile = new BibtexFile();
				final BufferedReader sr = new BufferedReader(new StringReader(bibTeXResult));
				// parse source
				parser.parse(bibtexFile, sr);
				/*
				 * final check if bibtex is valid, at least one bibtexentry should be in the bibtexFile
				 */
				final boolean bibtexValid = bibtexFile.getEntries().stream().anyMatch(BibtexEntry.class::isInstance);

				// test if expected bib is equal to scraped bib (which must be valid bibtex) 
				assertThat("scraped BibTeX not valid", bibtexValid, is(true));
				final String expectedReference = normBibTeX(getExpectedBibTeX(resultFile));
				compareBibTeXs(expectedReference, normBibTeX(bibTeXResult));
			} else {
				fail("nothing scraped");
			}
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String normBibTeX(final String bibTeX) {
		if (!present(bibTeX)) {
			return bibTeX;
		}
		final String normedLineBreaks = bibTeX.replaceAll("\\r\\n", "\n").trim();
		return normedLineBreaks.replaceAll("(?m)^[\\s&&[^\\n]]+|[\\s+&&[^\\n]]+$", "");
	}

	/**
	 * @param resultFile
	 * @return
	 */
	private static String getExpectedBibTeX(String resultFile) throws IOException {
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

	protected static void compareBibTeXs(String expected, String actual) throws ParseException, ExpansionException, AssertionError, IOException {
		final BibtexParser parser = new BibtexParser(true);
		final PersonListExpander personListExpander = new PersonListExpander(true, true);
		final MacroReferenceExpander macroReferenceExpander = new MacroReferenceExpander(true, true, true);
		final CrossReferenceExpander crossReferenceExpander = new CrossReferenceExpander();
		// BibtexPerson doesn't implement a custom equals method and is also final, so we can't create a comparableBibtexPerson
		// The comparator sorts first after the natural Order of the first name and then after the secound name
		final Comparator<BibtexPerson> bibtexPersonComparator = (o1, o2) -> {
			if (ObjectUtils.equals(o1.getFirst(), o2.getFirst())&&ObjectUtils.equals(o1.getLast(), o2.getLast())){
				return 0;
			}else {
				if (ObjectUtils.equals(o1.getFirst(), o2.getFirst())){
					if (o1.getLast()!=null&&o2.getLast()!=null){
						return String.CASE_INSENSITIVE_ORDER.compare(o1.getLast(), o2.getLast());
					}else if (o1.getLast()!=null){
						return 1;
					}else {
						return -1;
					}
				}else {
					if (o1.getFirst()!=null&&o2.getFirst()!=null){
						return String.CASE_INSENSITIVE_ORDER.compare(o1.getFirst(), o2.getFirst());
					}else if (o1.getFirst()!=null){
						return 1;
					}else {
						return -1;
					}
				}
			}
		};

		final BibtexFile bibtexFile = new BibtexFile();
		// parses the String to an internal BibTeX. They are on the same bibtexFile
		parser.parse(bibtexFile, new BufferedReader(new StringReader(actual)));
		parser.parse(bibtexFile, new BufferedReader(new StringReader(expected)));

		macroReferenceExpander.expand(bibtexFile);
		crossReferenceExpander.expand(bibtexFile);
		personListExpander.expand(bibtexFile);
		// entries added to a ArrayList as the returned List of bibtexFile.getEntries() is not modifiable
		List<BibtexAbstractEntry> entries = new ArrayList<>(bibtexFile.getEntries());
		//removes entries which are not BibTeXs like TopLevelComments
		entries.removeIf(entry -> !(entry instanceof BibtexEntry));

		//Assuming the list contains order the first entry should be the actual BibTeX and the second the expected BiBteX
		final Map<String, BibtexAbstractValue> actualBibTexValues = ((BibtexEntry) entries.get(0)).getFields();
		final Map<String, BibtexAbstractValue> expectedBibTexValues = ((BibtexEntry) entries.get(1)).getFields();
		try {
			try {
				// asymmetric difference both ways to get extra or missing keys of actual and expected Bibtex
				checkAsymmetricDifferenceBothWays(expectedBibTexValues.keySet(), actualBibTexValues.keySet(), null);
			} catch (AssertionError ae) {
				throw new AssertionError("\nDifferent Bibtex-tags:\n" + ae.getMessage());
			}

			// expectedBibtexValues.keySet() and actualBibtexValues.keySet() should contain the same elements or checkAsymmetricDifferenceBothWays should have already thrown an error
			for (String key : expectedBibTexValues.keySet()) {

				BibtexAbstractValue expectedValue = expectedBibTexValues.get(key);
				BibtexAbstractValue actualValue = actualBibTexValues.get(key);

				if (expectedValue instanceof BibtexString && actualValue instanceof BibtexString) {
					//compares the string-representation of BibtexString
					try {
						assertEquals(((BibtexString) expectedValue).getContent(), ((BibtexString) actualValue).getContent());
					}catch (ComparisonFailure cf){
						throw new AssertionError("\nDifferent values at tag: \"" + key +
								"\"\nExpected: \"" + cf.getExpected() +
								"\"\nActual:   \"" + cf.getActual() + "\"");
					}

				} else if (expectedValue instanceof BibtexPersonList && actualValue instanceof BibtexPersonList) {
					try {
						// asymmetric difference both ways to get extra or missing BibtexPersons
						checkAsymmetricDifferenceBothWays(((BibtexPersonList) expectedValue).getList(), ((BibtexPersonList) actualValue).getList(), bibtexPersonComparator);
					}catch (AssertionError ae){
						throw new AssertionError("\nDifferent values at tag: \"" + key + "\"\n" + ae.getMessage());
					}
				} else {
					// should never be thrown, except if a for me unknown BibtexAbstractValue-Class exists
					throw new IllegalStateException(
							"Expected classes were neither BibtexPersonList nor BibtexString\n" +
									"The class of expected was: \"" + expectedValue.getClass() + "\"\n " +
									"The class of actual was:   \"" + actualValue.getClass() + "\"");
				}
			}
		}catch (AssertionError ae){
			throw new ComparisonFailure(ae.getMessage() + "\n", expected, actual);
		}

	}


	/**
	 * @param expected the expected collection
	 * @param actual the actual collection
	 * @param comp the comparator, which should be used, if equals and hashcode should not be used
	 * @throws AssertionError contains the differences of both collections
	 *
	 * Caution: If a comparator is used the equals and compare methods could have different outputs
	 * Converts both collections to sets and makes the asymmetric difference both ways.
	 * Should the collections contain the same elements (duplicates are ignored) then no error is thrown.
	 */
	private static void checkAsymmetricDifferenceBothWays(Collection expected, Collection actual, Comparator comp)throws AssertionError{
		Set expectedSet;
		Set actualSet;

		if (comp==null){
			expectedSet = new HashSet<>(expected);
			actualSet = new HashSet<>(actual);

			actualSet.removeAll(expected);
			expectedSet.removeAll(actual);
		}else {
			expectedSet = new TreeSet<>(comp);
			expectedSet.addAll(expected);
			actualSet = new TreeSet<>(comp);
			actualSet.addAll(actual);

			TreeSet<BibtexPerson> temp = new TreeSet<>(comp);
			temp.addAll(actualSet);
			actualSet.removeAll(expectedSet);
			expectedSet.removeAll(temp);

		}
		if (actualSet.size() != 0 && expectedSet.size() != 0){
			throw new AssertionError("Elements not contained in expected Set: " + actualSet + "\nElements not contained in actual Set:   " + expectedSet);
		}else if (actualSet.size() != 0){
			throw new AssertionError("Elements not contained in expected Set: " + actualSet);
		}else if (expectedSet.size() != 0){
			throw new AssertionError("Elements not contained in actual Set: " + expectedSet );
		}
	}
}
