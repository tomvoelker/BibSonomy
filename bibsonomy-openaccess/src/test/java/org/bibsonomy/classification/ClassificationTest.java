package org.bibsonomy.classification;

import org.junit.Ignore;

import de.unikassel.puma.openaccess.classification.PublicationClassificatorSingleton;

public class ClassificationTest {

	private static final String XML_PATH = "/home/philipp/workspace/KDE/bibsonomy/bibsonomy-openaccess/src/main/resources/classifications";
	
	@Ignore
	// TODO fix it
	public static void main(String[] args) {

//		File path = new File(XML_PATH);
		PublicationClassificatorSingleton pc = new PublicationClassificatorSingleton();
		pc.setClassificationFilePath(XML_PATH);
		
		System.out.println(pc.getInstance().getDescription("ACM", "A"));
		
	}
}
