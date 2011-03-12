package org.bibsonomy.classification;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unikassel.puma.openaccess.classification.PublicationClassification;
import de.unikassel.puma.openaccess.classification.PublicationClassificator;
import de.unikassel.puma.openaccess.classification.PublicationClassificatorSingleton;

public class ClassificationTest {
	
	private static PublicationClassificator pubClass;
	
	@BeforeClass
	public static void initialise() {
		PublicationClassificatorSingleton publClassSingle = new PublicationClassificatorSingleton();
		publClassSingle.setClassificationFilePath("src/test/resources/classifications");
		
		pubClass = publClassSingle.getInstance();
	}
	
	@Test
	public void getAvailableClassificationsTest() {
		Set<String> available  = pubClass.getAvailableClassifications();
		
		assertEquals(2, available.size());
		assertTrue(available.contains("acmccs98-1.2.3"));
		assertTrue(available.contains("JEL"));
	}
	
	@Test
	public void getClassificationDetailsTest() {
		String description  = pubClass.getDescription("JEL", "A");
	
		assertEquals("General Economics: General", description);
		
		description  = pubClass.getDescription("acmccs98-1.2.3", "D.2.11");
		
		assertEquals("Software Architectures", description);
	}
	
	@Test
	public void getClassificationChildrenTest() {
		
		List<PublicationClassification> children = pubClass.getChildren("acmccs98-1.2.3", "D.2.");
		
		assertEquals(15, children.size());
	}
}
