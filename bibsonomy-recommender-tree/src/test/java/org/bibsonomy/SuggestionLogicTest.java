package org.bibsonomy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.logic.SuggestionLogic;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for setup and requesting the suggestionLogic
 * 
 * @author nilsraabe
 *
 */
public class SuggestionLogicTest {

	@SuppressWarnings("unused")
	private static final Log log 	= LogFactory.getLog(SuggestionLogicTest.class);
	
	SuggestionLogic suggestionLogic;
	
	@Before
	public void setup() {
		
		suggestionLogic = new SuggestionLogic();
		
		File file = new File("src/test/ressources/bookmark_title.txt");
		String filePath = file.getParentFile().getAbsolutePath();
        
		assertNotNull(filePath);
        
		suggestionLogic.setSourceFilePath(filePath);
		
	}
	
	@Test
	public void runTest() {
		
		/*
		 * Try to initialize the suggestion logic
		 */
		try {
			suggestionLogic.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<Pair<String,Integer>> bookmarkSuggestion = suggestionLogic.getBookmarkSuggestion("Comm");
		
		List<Pair<String,Integer>> publicationSuggestion = suggestionLogic.getPublicationSuggestion("Comm");

		List<Pair<String,Integer>> postSuggestion = suggestionLogic.getPostSuggestion("Comm");

		/*
		 * Test the bookmarkSuggestion
		 */
		
		Queue<Pair<String, Integer>> predefinedBookmarkSuggestion = new LinkedList<Pair<String, Integer>>();
		
		predefinedBookmarkSuggestion.add(new Pair<String, Integer>("Common Lisp the Language, 2nd Edition", 0));
		
		for(Pair<String, Integer> temp : bookmarkSuggestion) {

			Pair<String, Integer> temp2 = predefinedBookmarkSuggestion.poll();
			
			assertEquals(temp.getFirst(), temp2.getFirst());
			assertEquals(temp.getSecond(), temp2.getSecond());
		}
		
		
		/*
		 * Test the publicationSuggestion
		 */
		
		Queue<Pair<String, Integer>> predefinedPublicationSuggestion = new LinkedList<Pair<String, Integer>>();
		
		predefinedPublicationSuggestion.add(new Pair<String, Integer>("Communication the other Half of Office automation.", 3));
		predefinedPublicationSuggestion.add(new Pair<String, Integer>("Communications Policy for Composite Processes.", 2));

		
		for(Pair<String, Integer> temp : publicationSuggestion) {
			Pair<String, Integer> temp2 = predefinedPublicationSuggestion.poll();

			assertEquals(temp.getFirst(), temp2.getFirst());
			assertEquals(temp.getSecond(), temp2.getSecond());
		}
		
		
		/*
		 * Test the postSuggestion
		 */
		
		Queue<Pair<String, Integer>> predefinedPostSuggestion = new LinkedList<Pair<String, Integer>>();
		
		predefinedPostSuggestion.add(new Pair<String, Integer>("Common Lisp the Language, 2nd Edition", 0));
		predefinedPostSuggestion.add(new Pair<String, Integer>("Communications Policy for Composite Processes.", 2));
		predefinedPostSuggestion.add(new Pair<String, Integer>("Communication the other Half of Office automation.", 3));


		for(Pair<String, Integer> temp : postSuggestion) {
			Pair<String, Integer> temp2 = predefinedPostSuggestion.poll();

			assertEquals(temp.getFirst(), temp2.getFirst());
			assertEquals(temp.getSecond(), temp2.getSecond());
		}
	}
}
