package org.bibsonomy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.bibsonomy.common.Pair;
import org.bibsonomy.logic.SuggestionLogic;
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
	 */
	@BeforeClass
	public static void setup() {
		suggestionLogic = new SuggestionLogic();
		File file = new File("src/test/ressources/bookmark_title.txt");
		String filePath = file.getParentFile().getAbsolutePath();
		assertNotNull(filePath);
		suggestionLogic.setSourceFilePath(filePath);
		
		/*
		 * Try to initialize the suggestion logic
		 */
		try {
			suggestionLogic.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** TODO: can we use just assertEquals here?
	 * help method to compare two suggestions
	 * 
	 * @param bookmarkSuggestion
	 * @param predefinedBookmarkSuggestion
	 */
	protected static void assertSuggestionEquals(List<Pair<String, Integer>> bookmarkSuggestion,
			Queue<Pair<String, Integer>> predefinedBookmarkSuggestion) {
		for (Pair<String, Integer> temp : bookmarkSuggestion) {
			Pair<String, Integer> temp2 = predefinedBookmarkSuggestion.poll();
			assertEquals(temp.getFirst(), temp2.getFirst());
			assertEquals(temp.getSecond(), temp2.getSecond());
		}
	}
	
	/**
	 * tests for {@link SuggestionLogic#getBookmarkSuggestion(String)}
	 * tests for {@link SuggestionLogic#getPublicationSuggestion(String)}
	 */
	@Test
	public void testGetTitleSuggestions() {
		List<Pair<String,Integer>> bookmarkSuggestion = suggestionLogic.getBookmarkSuggestion("Comm");
		List<Pair<String,Integer>> publicationSuggestion = suggestionLogic.getPublicationSuggestion("Comm");
		List<Pair<String,Integer>> postSuggestion = suggestionLogic.getPostSuggestion("Comm");

		/*
		 * Test the bookmarkSuggestion
		 */
		
		Queue<Pair<String, Integer>> predefinedBookmarkSuggestion = new LinkedList<Pair<String, Integer>>();
		predefinedBookmarkSuggestion.add(new Pair<String, Integer>("Common Lisp the Language, 2nd Edition", Integer.valueOf(0)));
		
		assertSuggestionEquals(bookmarkSuggestion, predefinedBookmarkSuggestion);
		
		
		/*
		 * Test the publicationSuggestion
		 */
		Queue<Pair<String, Integer>> predefinedPublicationSuggestion = new LinkedList<Pair<String, Integer>>();
		
		predefinedPublicationSuggestion.add(new Pair<String, Integer>("Communication the other Half of Office automation.", Integer.valueOf(3)));
		predefinedPublicationSuggestion.add(new Pair<String, Integer>("Communications Policy for Composite Processes.", Integer.valueOf(2)));

		
		assertSuggestionEquals(publicationSuggestion, predefinedPublicationSuggestion);
		
		
		/*
		 * Test the postSuggestion
		 */
		
		Queue<Pair<String, Integer>> predefinedPostSuggestion = new LinkedList<Pair<String, Integer>>();
		predefinedPostSuggestion.add(new Pair<String, Integer>("Common Lisp the Language, 2nd Edition", Integer.valueOf(0)));
		predefinedPostSuggestion.add(new Pair<String, Integer>("Communications Policy for Composite Processes.", Integer.valueOf(2)));
		predefinedPostSuggestion.add(new Pair<String, Integer>("Communication the other Half of Office automation.", Integer.valueOf(3)));

		assertSuggestionEquals(postSuggestion, predefinedPostSuggestion);
	}
	
}
