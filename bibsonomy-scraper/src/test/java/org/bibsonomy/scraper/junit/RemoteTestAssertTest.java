package org.bibsonomy.scraper.junit;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import bibtex.expansions.ExpansionException;
import bibtex.parser.ParseException;
import org.bibsonomy.util.StringUtils;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RemoteTestAssertTest {

	@Test
	public void compareBibtexWithIdenticalBibTexts() throws IOException, ExpansionException, ParseException {
		String bibtex = getTestData("BaseLineTestData.bib");
		RemoteTestAssert.compareBibTeXs(bibtex, bibtex);
	}

	@Test
	public void compareBibtexWithActualHasMoreBibtexTags() throws IOException {
		String expected = getTestData("BaseLineTestData.bib");
		String actual = getTestData("TestDataExtraTag.bib");
		String expectedMessage = "\nDifferent Bibtex-tags:\nElements not contained in expected Set: [abstract]";

		ComparisonFailure e = assertThrows(ComparisonFailure.class, () -> RemoteTestAssert.compareBibTeXs(expected, actual));

		assertTrue(e.getMessage().startsWith(expectedMessage));
		assertEquals(e.getExpected(), expected);
		assertEquals(e.getActual(), actual);
	}

	@Test
	public void compareBibtexWithExpectedHasMoreBibtexTags() throws IOException {
		String expected = getTestData("TestDataExtraTag.bib");
		String actual = getTestData("BaseLineTestData.bib");
		String expectedMessage = "\nDifferent Bibtex-tags:\nElements not contained in actual Set: [abstract]";

		ComparisonFailure e = assertThrows(ComparisonFailure.class, () -> RemoteTestAssert.compareBibTeXs(expected, actual));

		assertTrue(e.getMessage().startsWith(expectedMessage));
		assertEquals(e.getExpected(), expected);
		assertEquals(e.getActual(), actual);
	}

	@Test
	public void compareBibtexWithActualHavingExtraAndMissingTag() throws IOException {
		String expected = getTestData("BaseLineTestData.bib");
		String actual = getTestData("TestDataExtraAndMissingTag.bib");
		String expectedMessage = "\n" +
						"Different Bibtex-tags:\n" +
						"Elements not contained in expected Set: [abstract]\n" +
						"Elements not contained in actual Set:   [year]";

		ComparisonFailure e = assertThrows(ComparisonFailure.class, () -> RemoteTestAssert.compareBibTeXs(expected, actual));

		assertTrue(e.getMessage().startsWith(expectedMessage));
		assertEquals(e.getExpected(), expected);
		assertEquals(e.getActual(), actual);
	}

	@Test
	public void compareBibtexWithDifferentBibtexStringValues() throws IOException {
		String expected = getTestData("BaseLineTestData.bib");
		String actual = getTestData("TestDataDifferentTitle.bib");
		String expectedMessage = "\n" +
						"Different values at tag: \"title\"\n" +
						"Expected: \"RETRACTED ARTICLE: Conservative management for an esophageal perforation in a patient presented with delayed diagnosis: a case report\"\n" +
						"Actual:   \"RETRACTED ARTICLE: Conservative management for an esophageal perforation\"";

		ComparisonFailure e = assertThrows(ComparisonFailure.class, () -> RemoteTestAssert.compareBibTeXs(expected, actual));

		assertTrue(e.getMessage().startsWith(expectedMessage));
		assertEquals(e.getExpected(), expected);
		assertEquals(e.getActual(), actual);
	}

	@Test
	public void compareBibtexWithActualMissingAnAuthor() throws IOException {
		String expected = getTestData("BaseLineTestData.bib");
		String actual = getTestData("TestDataMissingAuthor.bib");
		String expectedMessage = "\n" +
						"Different values at tag: \"author\"\n" +
						"Elements not contained in actual Set: [Kapetanos, Dimitrios]";

		ComparisonFailure e = assertThrows(ComparisonFailure.class, () -> RemoteTestAssert.compareBibTeXs(expected, actual));

		assertTrue(e.getMessage().startsWith(expectedMessage));
		assertEquals(e.getExpected(), expected);
		assertEquals(e.getActual(), actual);
	}

	@Test
	public void compareBibtexWithActualHavingAnExtraAuthor() throws IOException {
		String expected = getTestData("BaseLineTestData.bib");
		String actual = getTestData("TestDataExtraAuthor.bib");
		String expectedMessage = "\n" +
						"Different values at tag: \"author\"\n" +
						"Elements not contained in expected Set: [Extra, Author]";

		ComparisonFailure e = assertThrows(ComparisonFailure.class, () -> RemoteTestAssert.compareBibTeXs(expected, actual));

		assertTrue(e.getMessage().startsWith(expectedMessage));
		assertEquals(e.getExpected(), expected);
		assertEquals(e.getActual(), actual);
	}

	@Test
	public void compareBibtexWithActualMissingAndHavingAnExtraAuthor() throws IOException {
		String expected = getTestData("BaseLineTestData.bib");
		String actual = getTestData("TestDataExtraAndMissingAuthor.bib");
		String expectedMessage = "\n" +
						"Different values at tag: \"author\"\n" +
						"Elements not contained in expected Set: [Extra, Author]";

		ComparisonFailure e = assertThrows(ComparisonFailure.class, () -> RemoteTestAssert.compareBibTeXs(expected, actual));

		assertTrue(e.getMessage().startsWith(expectedMessage));
		assertEquals(e.getExpected(), expected);
		assertEquals(e.getActual(), actual);
	}

	@Test
	public void compareBibtexWithActualMissingFirstNameOfAuthor() throws IOException {
		String expected = getTestData("BaseLineTestData.bib");
		String actual = getTestData("TestDataAuthorMissingFirstName.bib");
		String expectedMessage = "\n" +
						"Different values at tag: \"author\"\n" +
						"Elements not contained in expected Set: [Lazaridis]\n" +
						"Elements not contained in actual Set:   [Lazaridis, Charalampos]";

		ComparisonFailure e = assertThrows(ComparisonFailure.class, () -> RemoteTestAssert.compareBibTeXs(expected, actual));

		assertTrue(e.getMessage().startsWith(expectedMessage));
		assertEquals(e.getExpected(), expected);
		assertEquals(e.getActual(), actual);
	}

	@Test
	public void compareBibtexWithDifferentKey() throws IOException {
		String expected = getTestData("BaseLineTestData.bib");
		String actual = getTestData("TestDataDifferentKey.bib");
		String expectedMessage = "actual String does not contain Bibtex with key \"Tsalis2009\"";

		ComparisonFailure e = assertThrows(ComparisonFailure.class, () -> RemoteTestAssert.compareBibTeXs(expected, actual));

		assertTrue(e.getMessage().startsWith(expectedMessage));
		assertEquals(e.getExpected(), expected);
		assertEquals(e.getActual(), actual);
	}

	@Test
	public void compareBibtexWithDifferentEntryType() throws IOException {
		String expected = getTestData("BaseLineTestData.bib");
		String actual = getTestData("TestDataDifferentEntryType.bib");
		String expectedMessage = "\nExpected entrytype was: article\n" + "Actual entrytype was:   book ";

		ComparisonFailure e = assertThrows(ComparisonFailure.class, () -> RemoteTestAssert.compareBibTeXs(expected, actual));

		assertTrue(e.getMessage().startsWith(expectedMessage));
		assertEquals(e.getExpected(), expected);
		assertEquals(e.getActual(), actual);
	}

	private String getTestData(String path) throws IOException {
		final InputStream in = RemoteTestAssertTest.class.getClassLoader().getResourceAsStream("org/bibsonomy/scraper/junit/" + path);
		return StringUtils.getStringFromReader(new BufferedReader(new InputStreamReader(in, StringUtils.DEFAULT_CHARSET)));
	}

}


