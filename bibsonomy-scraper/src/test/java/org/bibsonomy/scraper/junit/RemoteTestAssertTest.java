package org.bibsonomy.scraper.junit;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import bibtex.dom.BibtexEntry;
import bibtex.expansions.ExpansionException;
import bibtex.parser.ParseException;
import org.bibsonomy.util.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

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
		BibtexEntry testData = getTestData("BaseLineTestData.bib").get(0);
		RemoteTestAssert.assertEqualsBibtexEntry(testData, testData);
	}

	@Test
	public void compareBibtexWithActualHasMoreBibtexTags() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("BaseLineTestData.bib").get(0);
		BibtexEntry actual = getTestData("TestDataExtraTag.bib").get(0);
		String expectedErrorMessage = "Different keys of Bibtex-tags";
		String expectedErrorCauseMessage = "Elements not contained in expected Set: [abstract]";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
		assertEquals(expectedErrorCauseMessage, ae.getCause().getMessage());
	}

	@Test
	public void compareBibtexWithExpectedHasMoreBibtexTags() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("TestDataExtraTag.bib").get(0);
		BibtexEntry actual = getTestData("BaseLineTestData.bib").get(0);
		String expectedErrorMessage = "Different keys of Bibtex-tags";
		String expectedErrorCauseMessage = "Elements not contained in actual Set: [abstract]";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
		assertEquals(expectedErrorCauseMessage, ae.getCause().getMessage());

	}

	@Test
	public void compareBibtexWithActualHavingExtraAndMissingTag() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("BaseLineTestData.bib").get(0);
		BibtexEntry actual = getTestData("TestDataExtraAndMissingTag.bib").get(0);
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
		BibtexEntry expected = getTestData("BaseLineTestData.bib").get(0);
		BibtexEntry actual = getTestData("TestDataDifferentTitle.bib").get(0);
		String expectedErrorMessage = "Different values at tag: \"title\"\n" +
						"Expected: \"RETRACTED ARTICLE: Conservative management for an esophageal perforation in a patient presented with delayed diagnosis: a case report\"\n" +
						"Actual:   \"RETRACTED ARTICLE: Conservative management for an esophageal perforation\"";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	@Test
	public void compareBibtexWithActualMissingAnAuthor() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("BaseLineTestData.bib").get(0);
		BibtexEntry actual = getTestData("TestDataMissingAuthor.bib").get(0);
		String expectedErrorMessage = "Different values at tag: \"author\"";
		String expectedErrorCauseMessage = "Elements not contained in actual Set: [Kapetanos, Dimitrios]";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
		assertEquals(expectedErrorCauseMessage, ae.getCause().getMessage());
	}

	@Test
	public void compareBibtexWithActualHavingAnExtraAuthor() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("BaseLineTestData.bib").get(0);
		BibtexEntry actual = getTestData("TestDataExtraAuthor.bib").get(0);
		String expectedErrorMessage = "Different values at tag: \"author\"";
		String expectedErrorCauseMessage = "Elements not contained in expected Set: [Extra, Author]";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
		assertEquals(expectedErrorCauseMessage, ae.getCause().getMessage());

	}

	@Test
	public void compareBibtexWithActualMissingAndHavingAnExtraAuthor() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("BaseLineTestData.bib").get(0);
		BibtexEntry actual = getTestData("TestDataExtraAndMissingAuthor.bib").get(0);
		String expectedErrorMessage = "Different values at tag: \"author\"";
		String expectedErrorCauseMessage = "\n" +
						"Elements not contained in expected Set: [Extra, Author]\n" +
						"Elements not contained in actual Set:   [Kapetanos, Dimitrios]";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
		assertEquals(expectedErrorCauseMessage, ae.getCause().getMessage());
	}

	@Test
	public void compareBibtexWithActualMissingFirstNameOfAuthor() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("BaseLineTestData.bib").get(0);
		BibtexEntry actual = getTestData("TestDataAuthorMissingFirstName.bib").get(0);
		String expectedErrorMessage = "Different values at tag: \"author\"";
		String expectedErrorCauseMessage = "\n" +
						"Elements not contained in expected Set: [Lazaridis]\n" +
						"Elements not contained in actual Set:   [Lazaridis, Charalampos]";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
		assertEquals(expectedErrorCauseMessage, ae.getCause().getMessage());
	}

	@Test
	public void compareBibtexWithDifferentKey() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("BaseLineTestData.bib").get(0);
		BibtexEntry actual = getTestData("TestDataDifferentKey.bib").get(0);
		String expectedErrorMessage = "\n" +
						"Expected entrykey was: article\n" +
						"Actual entrykey was:   article expected:<Tsalis200[9]> but was:<Tsalis200[8]>";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	@Test
	public void compareBibtexWithDifferentEntryType() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("BaseLineTestData.bib").get(0);
		BibtexEntry actual = getTestData("TestDataDifferentEntryType.bib").get(0);
		String expectedErrorMessage = "\n" +
						"Expected entrytype was: article\n" +
						"Actual entrytype was:   book expected:<[article]> but was:<[book]>";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	@Test
	public void compareBibtexWithDifferentUrlProtocols() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("BaseLineTestData.bib").get(0);
		BibtexEntry actual = getTestData("TestDataDifferentUrlProtocol.bib").get(0);
		RemoteTestAssert.assertEqualsBibtexEntry(expected, actual);
	}

	@Test
	public void compareBibtexWithDifferentUrl() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("BaseLineTestData.bib").get(0);
		BibtexEntry actual = getTestData("TestDataDifferentUrl.bib").get(0);
		String expectedErrorMessage = "Different values at tag: \"url\"\n" +
						"Expected: \"https://doi.org/10.1186/1757-1626-2-164\"\n" +
						"Actual:   \"https://www.google.de/\"";

		AssertionError ae = assertThrows(AssertionError.class, () -> RemoteTestAssert.assertEqualsBibtexEntry(expected, actual));
		assertEquals(expectedErrorMessage, ae.getMessage());
	}

	@Test
	public void compareBibtexWithDifferentKeywordOrder() throws IOException, ExpansionException, ParseException {
		BibtexEntry expected = getTestData("BaseLineTestData.bib").get(0);
		BibtexEntry actual = getTestData("TestDataDifferentKeywordOrder.bib").get(0);

		RemoteTestAssert.assertEqualsBibtexEntry(expected, actual);
	}

	/*
	Test for assertEqualsBibtexEntryList
	 */

	@Test
	public void compareMultipleBibtexWithIdenticalBibTexts() throws IOException, ExpansionException, ParseException {
		List<BibtexEntry> testData = getTestData("BaseLineTestData.bib");
		RemoteTestAssert.assertEqualsBibtexEntryList(testData, testData);
	}

	private List<BibtexEntry> getTestData(String path) throws IOException, ExpansionException, ParseException {
		final InputStream in = RemoteTestAssertTest.class.getClassLoader().getResourceAsStream("org/bibsonomy/scraper/junit/" + path);
		String bibtexString = StringUtils.getStringFromReader(new BufferedReader(new InputStreamReader(in, StringUtils.DEFAULT_CHARSET)));
		return RemoteTestAssert.parseAndExpandBibTeXs(bibtexString);
	}

}


