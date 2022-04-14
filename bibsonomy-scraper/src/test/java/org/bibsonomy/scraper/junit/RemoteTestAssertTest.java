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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.dom.BibtexString;
import bibtex.expansions.ExpansionException;
import bibtex.parser.ParseException;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.KDEUrlCompositeScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.BibtexScraper;
import org.bibsonomy.scraper.url.kde.aaai.AAAIScraper;
import org.bibsonomy.util.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class RemoteTestAssertTest {
	/*
	Tests for checkAsymmetricDifferenceBothWays
	 */
	@Test
	public void compareListsWithSameElements() {
		List<Integer> list1 = Arrays.asList(1, 2, 3, 4);
		List<Integer> list2 = Arrays.asList(1, 2, 3, 4);
		RemoteTestAssert.checkAsymmetricDifferenceBothWays(list1, list2, null);
	}

	@Test
	public void compareListsWithDifferentElements() {
		List<Integer> list1 = Arrays.asList(1, 2, 3, 4);
		List<Integer> list2 = Arrays.asList(5, 6, 7, 8);
		String expectedErrorMessage = "\n" +
						"Elements not contained in expected Set: [5, 6, 7, 8]\n" +
						"Elements not contained in actual Set:   [1, 2, 3, 4]";
		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.checkAsymmetricDifferenceBothWays(list1, list2, null));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	@Test
	public void compareListsWithMoreActualValues() {
		List<Integer> list1 = Arrays.asList(1, 2, 3, 4);
		List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5);
		String expectedErrorMessage = "Elements not contained in expected Set: [5]";
		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.checkAsymmetricDifferenceBothWays(list1, list2, null));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	@Test
	public void compareListsWithMoreExpectedValues() {
		List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
		List<Integer> list2 = Arrays.asList(1, 2, 3, 4);
		String expectedErrorMessage = "Elements not contained in actual Set: [5]";
		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.checkAsymmetricDifferenceBothWays(list1, list2, null));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	@Test
	public void compareListsWithSameElementsWithCustomComparator() {
		List<String> list1 = Arrays.asList("A", "b", "C", "D");
		List<String> list2 = Arrays.asList("a", "B", "c", "D");
		RemoteTestAssert.checkAsymmetricDifferenceBothWays(list1, list2, String.CASE_INSENSITIVE_ORDER);
	}

	@Test
	public void compareListsWithDifferentElementsWithCustomComparator() {
		List<String> list1 = Arrays.asList("A", "b", "C", "D");
		List<String> list2 = Arrays.asList("E", "F", "g", "d");
		String expectedErrorMessage = "\n" +
						"Elements not contained in expected Set: [E, F, g]\n" +
						"Elements not contained in actual Set:   [A, b, C]";
		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.checkAsymmetricDifferenceBothWays(list1, list2, String.CASE_INSENSITIVE_ORDER));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	/*
	Test for assertEqualsBibtexEntry
	 */

	@Test
	public void compareBibtexWithIdenticalBibText() throws IOException, ExpansionException, ParseException {
		BibtexEntry testData = getTestData("singlebibtex/TestDataBaseLine.bib").get(0);
		RemoteTestAssert.assertEqualsBibtexEntry(testData, testData);
	}

	@Test
	public void compareBibtexWithActualHasMoreBibtexTags() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("singlebibtex/TestDataBaseLine.bib").get(0);
		BibtexEntry actual = getTestData("singlebibtex/TestDataExtraTag.bib").get(0);
		String expectedErrorMessage = "Different keys of Bibtex-tags";
		String expectedErrorCauseMessage = "Elements not contained in expected Set: [abstract]";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
		assertEquals(expectedErrorCauseMessage, ae.getCause().getMessage());
	}

	@Test
	public void compareBibtexWithExpectedHasMoreBibtexTags() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("singlebibtex/TestDataExtraTag.bib").get(0);
		BibtexEntry actual = getTestData("singlebibtex/TestDataBaseLine.bib").get(0);
		String expectedErrorMessage = "Different keys of Bibtex-tags";
		String expectedErrorCauseMessage = "Elements not contained in actual Set: [abstract]";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
		assertEquals(expectedErrorCauseMessage, ae.getCause().getMessage());

	}

	@Test
	public void compareBibtexWithActualHavingExtraAndMissingTag() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("singlebibtex/TestDataBaseLine.bib").get(0);
		BibtexEntry actual = getTestData("singlebibtex/TestDataExtraAndMissingTag.bib").get(0);
		String expectedErrorMessage = "Different keys of Bibtex-tags";
		String expectedErrorCauseMessage = "\n" +
						"Elements not contained in expected Set: [abstract]\n" +
						"Elements not contained in actual Set:   [year]";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
		assertEquals(expectedErrorCauseMessage, ae.getCause().getMessage());
	}

	@Test
	public void compareBibtexWithDifferentBibtexStringValues() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("singlebibtex/TestDataBaseLine.bib").get(0);
		BibtexEntry actual = getTestData("singlebibtex/TestDataDifferentTitle.bib").get(0);
		String expectedErrorMessage = "Different values at tag: title\n" +
						"Expected: RETRACTED ARTICLE: Conservative management for an esophageal perforation in a patient presented with delayed diagnosis: a case report\n" +
						"Actual  : RETRACTED ARTICLE: Conservative management for an esophageal perforation";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	@Test
	public void compareBibtexWithActualMissingAnAuthor() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("singlebibtex/TestDataBaseLine.bib").get(0);
		BibtexEntry actual = getTestData("singlebibtex/TestDataMissingAuthor.bib").get(0);
		String expectedErrorMessage = "Different values at tag: author";
		String expectedErrorCauseMessage = "Elements not contained in actual Set: [Kapetanos, Dimitrios]";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
		assertEquals(expectedErrorCauseMessage, ae.getCause().getMessage());
	}

	@Test
	public void compareBibtexWithActualHavingAnExtraAuthor() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("singlebibtex/TestDataBaseLine.bib").get(0);
		BibtexEntry actual = getTestData("singlebibtex/TestDataExtraAuthor.bib").get(0);
		String expectedErrorMessage = "Different values at tag: author";
		String expectedErrorCauseMessage = "Elements not contained in expected Set: [Extra, Author]";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
		assertEquals(expectedErrorCauseMessage, ae.getCause().getMessage());

	}

	@Test
	public void compareBibtexWithActualMissingAndHavingAnExtraAuthor() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("singlebibtex/TestDataBaseLine.bib").get(0);
		BibtexEntry actual = getTestData("singlebibtex/TestDataExtraAndMissingAuthor.bib").get(0);
		String expectedErrorMessage = "Different values at tag: author";
		String expectedErrorCauseMessage = "\n" +
						"Elements not contained in expected Set: [Extra, Author]\n" +
						"Elements not contained in actual Set:   [Kapetanos, Dimitrios]";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
		assertEquals(expectedErrorCauseMessage, ae.getCause().getMessage());
	}

	@Test
	public void compareBibtexWithActualMissingFirstNameOfAuthor() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("singlebibtex/TestDataBaseLine.bib").get(0);
		BibtexEntry actual = getTestData("singlebibtex/TestDataAuthorMissingFirstName.bib").get(0);
		String expectedErrorMessage = "Different values at tag: author";
		String expectedErrorCauseMessage = "\n" +
						"Elements not contained in expected Set: [Lazaridis]\n" +
						"Elements not contained in actual Set:   [Lazaridis, Charalampos]";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
		assertEquals(expectedErrorCauseMessage, ae.getCause().getMessage());
	}

	@Test
	public void compareBibtexWithDifferentKey() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("singlebibtex/TestDataBaseLine.bib").get(0);
		BibtexEntry actual = getTestData("singlebibtex/TestDataDifferentKey.bib").get(0);
		String expectedErrorMessage = "Bibtex-Keys are not equal\n" +
						"Expected: Tsalis2009\n" +
						"Actual  : Tsalis2008";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	@Test
	public void compareBibtexWithDifferentEntryType() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("singlebibtex/TestDataBaseLine.bib").get(0);
		BibtexEntry actual = getTestData("singlebibtex/TestDataDifferentEntryType.bib").get(0);
		String expectedErrorMessage = "Bibtex-Entrytypes are not equal\n" +
						"Expected: article\n" +
						"Actual  : book";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	@Test
	public void compareBibtexWithDifferentUrlProtocols() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("singlebibtex/TestDataBaseLine.bib").get(0);
		BibtexEntry actual = getTestData("singlebibtex/TestDataDifferentUrlProtocol.bib").get(0);
		RemoteTestAssert.assertEqualsBibtexEntry(expected, actual);
	}

	@Test
	public void compareBibtexWithDifferentUrl() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("singlebibtex/TestDataBaseLine.bib").get(0);
		BibtexEntry actual = getTestData("singlebibtex/TestDataDifferentUrl.bib").get(0);
		String expectedErrorMessage = "Different values at tag: url\n" +
						"Expected: https://doi.org/10.1186/1757-1626-2-164\n" +
						"Actual  : http://www.google.de/";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	@Test
	public void compareBibtexWithDifferentKeywordOrder() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("singlebibtex/TestDataBaseLine.bib").get(0);
		BibtexEntry actual = getTestData("singlebibtex/TestDataDifferentKeywordOrder.bib").get(0);

		RemoteTestAssert.assertEqualsBibtexEntry(expected, actual);
	}

	/*
	Test for assertEqualsBibtexEntryList
	 */

	@Test
	public void compareMultipleBibtexWithIdenticalBibTexts() throws IOException, ExpansionException, ParseException {
		List<BibtexEntry> testData = getTestData("multiplebibtex/TestDataBaseLine.bib");
		RemoteTestAssert.assertEqualsBibtexEntryList(testData, testData);
	}

	@Test
	public void compareMultipleBibtexWithExtraActualBibtex() throws IOException, ExpansionException, ParseException {
		List<BibtexEntry> expected = getTestData("multiplebibtex/TestDataBaseLine.bib");
		List<BibtexEntry> actual = getTestData("multiplebibtex/TestDataExtraBibtex.bib");
		String expectedErrorMessage = "Expected and actual don't contain the same amount of BibTeXs\n" +
						"Expected: 2\n" +
						"Actual  : 3";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntryList(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	@Test
	public void compareMultipleBibtexWithMissingActualBibtex() throws IOException, ExpansionException, ParseException {
		List<BibtexEntry> expected = getTestData("multiplebibtex/TestDataBaseLine.bib");
		List<BibtexEntry> actual = getTestData("multiplebibtex/TestDataMissingBibtex.bib");
		String expectedErrorMessage = "Expected and actual don't contain the same amount of BibTeXs\n" +
						"Expected: 2\n" +
						"Actual  : 1";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntryList(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	@Test
	public void compareMultipleBibtexWithDifferentBibtex() throws IOException, ExpansionException, ParseException {
		List<BibtexEntry> expected = getTestData("multiplebibtex/TestDataBaseLine.bib");
		List<BibtexEntry> actual = getTestData("multiplebibtex/TestDataDifferentBibtex.bib");
		String expectedErrorMessage = "Actual String does not contain Bibtex with key kluger1996effects";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntryList(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	@Test
	public void compareMultipleBibtexWithDifferentBibtexWithSameKey() throws IOException, ExpansionException, ParseException {
		List<BibtexEntry> expected = getTestData("multiplebibtex/TestDataBaseLine.bib");
		List<BibtexEntry> actual = getTestData("multiplebibtex/TestDataDifferentBibtexSameKey.bib");
		String expectedErrorMessage = "Bibtex-Entrytypes are not equal\n" +
						"Expected: misc\n" +
						"Actual  : article";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntryList(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}
	/*
	Tests for assertEqualsBibtexString()
	 */
	@Test
	public void compareEqualBibtexStrings() throws MalformedURLException {
		BibtexFile bibtexFile = new BibtexFile();
		BibtexString expected = bibtexFile.makeString("6757gkhjlsbafkasgh%/%/(&)\t\n");
		BibtexString actual = bibtexFile.makeString("6757gkhjlsbafkasgh%/%/(&)\t\n");
		RemoteTestAssert.assertEqualsBibtexString("title", expected, actual);
	}

	@Test
	public void compareDifferentBibtexStrings() {
		BibtexFile bibtexFile = new BibtexFile();
		BibtexString expected = bibtexFile.makeString("675gkhjlsbafkasgh%/%/(&)\t\n");
		BibtexString actual = bibtexFile.makeString("6757gkhjlsbafkasgh%/%/(&)\t\n");
		String expectedErrorMessage = "Different values at tag: title\n" +
						"Expected: 675gkhjlsbafkasgh%/%/(&)\n" +
						"Actual  : 6757gkhjlsbafkasgh%/%/(&)";
		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexString("title", expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	@Test
	public void compareEqualBibtexStringUrls() throws MalformedURLException {
		BibtexFile bibtexFile = new BibtexFile();
		BibtexString expected = bibtexFile.makeString("https://www.bibsonomy.org/popular");
		BibtexString actual = bibtexFile.makeString("https://www.bibsonomy.org/popular");
		RemoteTestAssert.assertEqualsBibtexString("title", expected, actual);
	}

	@Test
	public void compareEqualBibtexStringUrlsButDifferentProtocolAndAnchor() throws MalformedURLException {
		BibtexFile bibtexFile = new BibtexFile();
		BibtexString expected = bibtexFile.makeString("https://www.bibsonomy.org");
		BibtexString actual = bibtexFile.makeString("http://www.bibsonomy.org/#bookmarks");
		RemoteTestAssert.assertEqualsBibtexString("title", expected, actual);
	}

	@Test
	public void compareDifferentBibtexStringUrlsButEqualRedirectedUrl() throws MalformedURLException {
		BibtexFile bibtexFile = new BibtexFile();
		BibtexString expected = bibtexFile.makeString("https://rb.gy/ra9zii");
		BibtexString actual = bibtexFile.makeString("https://www.bibsonomy.org/");
		RemoteTestAssert.assertEqualsBibtexString("title", expected, actual);
	}

	@Test
	public void compareDifferentBibtexStringUrls() {
		BibtexFile bibtexFile = new BibtexFile();
		BibtexString expected = bibtexFile.makeString("https://www.bibsonomy.org/popular");
		BibtexString actual = bibtexFile.makeString("https://www.bibsonomy.org/");
		String expectedErrorMessage = "Different values at tag: title\n" +
						"Expected: https://www.bibsonomy.org/popular\n" +
						"Actual  : https://www.bibsonomy.org/";
		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexString("title", expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	@Test
	public void compareEqualBibtexStringKeywords() throws MalformedURLException {
		BibtexFile bibtexFile = new BibtexFile();
		BibtexString expected = bibtexFile.makeString("bib, sono, my");
		BibtexString actual = bibtexFile.makeString("bib, my, sono");
		String expectedErrorMessage = "Different values at tag: title\n" +
						"Expected: https://www.bibsonomy.org/popular\n" +
						"Actual  : https://www.bibsonomy.org/";
		RemoteTestAssert.assertEqualsBibtexString("keywords", expected, actual);
	}

	@Test
	public void compareDifferentBibtexStringKeywords() {
		BibtexFile bibtexFile = new BibtexFile();
		BibtexString expected = bibtexFile.makeString("bib, sono, my");
		BibtexString actual = bibtexFile.makeString("bib, sono, me");
		String expectedErrorMessage = "\n" +
						"Elements not contained in expected Set: [me]\n" +
						"Elements not contained in actual Set:   [my]";
		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexString("keywords", expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	/*
	Tests for createScraper()
	 */
	@Test
	public void checkUrlScraperNotInKDEUrlCompositeScraper() throws InstantiationException, IllegalAccessException {
		String expectedErrorMessage = "KDEUrlCompositeScraper does not contain TestScraper";
		class TestScraper implements UrlScraper {

			@Override
			public boolean scrape(ScrapingContext scrapingContext) throws ScrapingException {
				return false;
			}

			@Override
			public String getInfo() {
				return null;
			}

			@Override
			public Collection<Scraper> getScraper() {
				return null;
			}

			@Override
			public boolean supportsScrapingContext(ScrapingContext scrapingContext) {
				return false;
			}

			@Override
			public List<Pair<Pattern, Pattern>> getUrlPatterns() {
				return null;
			}

			@Override
			public boolean supportsUrl(URL url) {
				return false;
			}

			@Override
			public String getSupportedSiteName() {
				return null;
			}

			@Override
			public String getSupportedSiteURL() {
				return null;
			}
		}

		RuntimeException re = assertThrows(RuntimeException.class, () -> RemoteTestAssert.createScraper(TestScraper.class));
		assertEquals(expectedErrorMessage, re.getMessage());
	}

	@Test
	public void checkCorrectScraperCreatedForUrlScraper() throws InstantiationException, IllegalAccessException {
		Scraper scraper = RemoteTestAssert.createScraper(AAAIScraper.class);
		assertEquals(KDEUrlCompositeScraper.class, scraper.getClass());
	}

	@Test
	public void checkCorrectScraperCreatedForNotUrlScraper() throws InstantiationException, IllegalAccessException {
		Scraper scraper = RemoteTestAssert.createScraper(BibtexScraper.class);
		assertEquals(BibtexScraper.class, scraper.getClass());
	}

	private List<BibtexEntry> getTestData(String path) throws IOException, ExpansionException, ParseException {
		try (final InputStream in = RemoteTestAssertTest.class.getClassLoader().getResourceAsStream("org/bibsonomy/scraper/junit/" + path)) {
			if (in == null) {
				throw new RuntimeException("No file found at the path org/bibsonomy/scraper/junit/" + path);
			}
			String bibtexString = StringUtils.getStringFromReader(new BufferedReader(new InputStreamReader(in, StringUtils.DEFAULT_CHARSET)));
			return RemoteTestAssert.parseAndExpandBibTeXs(bibtexString);
		}
	}

}


