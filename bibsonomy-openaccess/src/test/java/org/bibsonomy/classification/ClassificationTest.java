package org.bibsonomy.classification;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.unikassel.puma.openaccess.classification.PublicationClassificator;
import de.unikassel.puma.openaccess.classification.PublicationClassificatorSingleton;

public class ClassificationTest {
	
	private PublicationClassificator pubClass;
	
	@Before
	public void initialise() {
		PublicationClassificatorSingleton publClassSingle = new PublicationClassificatorSingleton();
		publClassSingle.setClassificationFilePath("src/test/resources/classifications");
		
		pubClass = publClassSingle.getInstance();
	}

	@Test
	public void getAvailableClassificationsTest() {
		Set<String> available  = pubClass.getAvailableClassifications();
		
		assertEquals(2, available.size());
		assertTrue(available.contains("ACM"));
		assertTrue(available.contains("JEL"));
	}
	
	@Test
	public void getClassificationDetailsTest() {
		String description  = pubClass.getDescription("JEL", "A");
	
		assertEquals("General Economics: General", description);
		
		description  = pubClass.getDescription("ACM", "A");
		
		assertEquals("General Literature", description);
	}
}
