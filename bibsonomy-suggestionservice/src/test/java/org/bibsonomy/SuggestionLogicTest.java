package org.bibsonomy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.logic.SuggestionLogic;
import org.bibsonomy.model.Suggestion;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for setup and requesting the suggestionLogic
 * 
 * @author nilsraabe
 *
 */
public class SuggestionLogicTest {
	private static SuggestionLogic suggestionLogic;
	
	/**
	 * setup suggestionLogic with test data
	 * @throws Exception 
	 */
	@BeforeClass
	public static void setup() throws Exception {
		suggestionLogic = new SuggestionLogic();
		File file = new File("src/test/resources/bookmark_title.txt");
		String filePath = file.getParentFile().getAbsolutePath();
		assertNotNull(filePath);
		
		suggestionLogic.setSourceFilePath(filePath);
		
		/*
		 * Try to initialize the suggestion logic
		 */
		suggestionLogic.init();
	}
	
	/**
	 * tests for {@link SuggestionLogic#getBookmarkSuggestion(String)}
	 * tests for {@link SuggestionLogic#getPublicationSuggestion(String)}
	 */
	@Test
	public void testGetTitleSuggestions() {
		final SortedSet<Suggestion> bookmarkSuggestion = suggestionLogic.getBookmarkSuggestion("Comm");
		final SortedSet<Suggestion> publicationSuggestion = suggestionLogic.getPublicationSuggestion("Comm");
		final SortedSet<Suggestion> postSuggestion = suggestionLogic.getPostSuggestion("Comm");

		/*
		 * test bookmark suggestion
		 */
		SortedSet<Suggestion> expectedBookmarkSuggestion = new TreeSet<Suggestion>();
		expectedBookmarkSuggestion.add(new Suggestion("Common Lisp the Language, 2nd Edition", 0));
		assertEquals(expectedBookmarkSuggestion, bookmarkSuggestion);
		
		/*
		 * test the publication suggestion
		 */
		SortedSet<Suggestion> expectedPublicationSuggestion = new TreeSet<Suggestion>(Arrays.asList(
				new Suggestion("Communication the other Half of Office automation.", 3),
				new Suggestion("Communications Policy for Composite Processes.", 2)));
		assertEquals(expectedPublicationSuggestion, publicationSuggestion);
		
		
		/*
		 * test the post suggestion
		 */
		final SortedSet<Suggestion> allExpectedSuggestions = new TreeSet<Suggestion>();
		allExpectedSuggestions.addAll(expectedBookmarkSuggestion);
		allExpectedSuggestions.addAll(expectedPublicationSuggestion);
		
		assertEquals(allExpectedSuggestions, postSuggestion);
	}
	
}
