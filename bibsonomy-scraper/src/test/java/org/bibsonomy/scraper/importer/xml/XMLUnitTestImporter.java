package org.bibsonomy.scraper.importer.xml;

import java.util.List;

import org.bibsonomy.scraper.ScraperUnitTest;
import org.bibsonomy.scraper.importer.IUnitTestImporter;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Importer for XML and bib sources.
 * @author tst
 *
 */
public class XMLUnitTestImporter implements IUnitTestImporter {
	
	/**
	 * Name of xml file which contains the test cases.
	 */
	private static final String UNIT_TEST_DATA_XML_FILE_NAME = "UnitTestData.xml";
	

	public List<ScraperUnitTest> getUnitTests() throws Exception{
		final XMLReader xmlreader;
		xmlreader = XMLReaderFactory.createXMLReader();

		XMLUnitTestHandler handler = new XMLUnitTestHandler(); 
		xmlreader.setContentHandler(handler);
		xmlreader.setEntityResolver(handler);
		xmlreader.setErrorHandler(handler);

		final InputSource is = new InputSource(this.getClass().getResourceAsStream(UNIT_TEST_DATA_XML_FILE_NAME));
		
		xmlreader.parse(is);
		
		return handler.getTests();
	}
	
}
