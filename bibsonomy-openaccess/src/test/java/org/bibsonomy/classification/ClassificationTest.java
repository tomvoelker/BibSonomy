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
		
		assertEquals(3, available.size());
		assertTrue(available.contains("ddc22_000-999"));
		assertTrue(available.contains("acmccs98-1.2.3"));
		assertTrue(available.contains("JEL"));
	}
	
	@Test
	public void getClassificationDetailsTest() {
		String description  = pubClass.getDescription("JEL", "A");
	
		assertEquals("General Economics: General", description);
		
		description  = pubClass.getDescription("acmccs98-1.2.3", "D.2.11");
		
		assertEquals("Software Architectures", description);

		description  = pubClass.getDescription("acmccs98-1.2.3", "D.0");
	}
	
	@Test
	public void dDCClassificationParserTest() {
		String description = pubClass.getDescription("ddc22_000-999", "0");

		assertEquals("Computer science, information & general works" , description);
		
		description = pubClass.getDescription("ddc22_000-999", "00");
		assertEquals("Computer science, knowledge & systems" , description);
		
		description = pubClass.getDescription("ddc22_000-999", "000");
		assertEquals("Computer science, information & general works" , description);
		

		description = pubClass.getDescription("ddc22_000-999", "3");
		assertEquals("Social sciences" , description);
		

		description = pubClass.getDescription("ddc22_000-999", "30");
		assertEquals("Social sciences, sociology & anthropology" , description);
		

		description = pubClass.getDescription("ddc22_000-999", "300");
		assertEquals("Social sciences" , description);
	}
	
	@Test
	public void getClassificationChildrenTest() {
		
		List<PublicationClassification> children = pubClass.getChildren("acmccs98-1.2.3", "D.2.");
		
		assertEquals(15, children.size());
	}
}
