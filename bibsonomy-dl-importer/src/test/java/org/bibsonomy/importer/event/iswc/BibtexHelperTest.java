package org.bibsonomy.importer.event.iswc;

import java.util.ArrayList;

import org.junit.Test;
import org.bibsonomy.importer.event.iswc.model.Publication;

import static org.junit.Assert.*;

/**
 * Test of the methods from {@link BibtexHelper}. 
 * @author tst
 */
public class BibtexHelperTest {
	
	/**
	 * test of the buildAuthorsString({@link ArrayList} authors) method
	 */@Test
	public void buildAuthorsStringTest(){
		
		// init values for testing
		ArrayList<String> authors = new ArrayList<String>();
		authors.add("author1");
		authors.add("author2");
		authors.add("author3");
		
		// generate author string
		String authorsString = BibtexHelper.buildPersonString(authors);
		
		// compare
		assertEquals(authorsString, "author1 and author2 and author3");
	}
	
	/**
	 * test of the buildTitleKey(string title) method
	 */@Test
	public void buildTitleKeyTest(){
		// init
		String title = "this is a semantic web conference title";
		
		// get firts matching token of title
		String titleKey = BibtexHelper.buildTitleKey(title);
		
		// compare
		assertEquals(titleKey, "semantic");
	}
	
	/**
	 * test of the extractLastname(String person) method
	 */@Test
	public void extractLastnameTest(){
		
		// init validation values
		String person1 = "Thomas Steuber";
		String person2 = "Thomas Lastname Steuber";
		
		// get testing values
		String lastname1 = BibtexHelper.extractLastname(person1);
		String lastname2 = BibtexHelper.extractLastname(person2);
		
		// compare
		assertEquals(lastname1, "Steuber");
		assertEquals(lastname2, "Steuber");
	}
	
	/**
	 * test of the buildBibtexOfInproceedings({@link Publication}) method
	 */@Test
	public void buildBibtexOfInproceedingsTest(){
		 
		// init publication
		Publication publication = new Publication();
		publication.setAuthor("a author");
		publication.setBibabstract("a abstract");
		publication.setBibtexkey("myKey");
		publication.setEntrytype("inproceedings");
		publication.setKeywords("my tag list");
		publication.setTitle("this is a title");
		
		// build bibtex
		String bibtex = BibtexHelper.buildBibtex(publication);
		
		assertNotNull(bibtex);
		assertNotSame(bibtex, "");
			
	}

}
