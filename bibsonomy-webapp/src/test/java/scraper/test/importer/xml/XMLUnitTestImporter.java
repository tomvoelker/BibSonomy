package scraper.test.importer.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.annotation.Inherited;
import java.util.LinkedList;
import java.util.List;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import scraper.Scraper;
import scraper.test.ParseFailureMessage;
import scraper.test.URLTest.URLScraperUnitTest;
import scraper.test.importer.IUnitTestImporter;

/**
 * Importer for XML and bib sources.
 * @author tst
 *
 */
public class XMLUnitTestImporter implements IUnitTestImporter {
	
	/*
	 * Names from XML elements and attributes
	 */
	private static final String ELEMENT_URL_TEST = "URLTest";
	private static final String ELEMENT_Test_DESCRIPTION = "TestDescription";
	private static final String ELEMENT_URL = "URL";
	private static final String ELEMENT_BIB_FILE = "BibFile";
	private static final String ELEMENT_SCRAPER = "Scraper";
	private static final String ATTRIBUTE_ID = "id";
	
	/**
	 * Name of xml file which contains the test cases.
	 */
	private static final String UNIT_TEST_DATA_XML_FILE_NAME = "UnitTestData.xml";
	

	/**
	 * Generates List with ScraperUnitTest and returns it to runner.
	 * Needed because of IUnitTestImporter 
	 */
	public List getUnitTests() throws Exception{
		Document doc = readUnitTestData();
		List tests = parseTests(doc);
		return tests;
	}
	
	/**
	 * Extract tests from UnitTestData.xml and builds List with the
	 * ScraperUnitTests.
	 * @param unitTestData Document representation of UnitTestData.xml
	 * @return List with ScraperUnitTests
	 */
	public List parseTests(Document unitTestData){
		List tests = new LinkedList<URLScraperUnitTest>();
		
		// get all URLTest elements
		NodeList urlTests = unitTestData.getElementsByTagName(ELEMENT_URL_TEST);
		
		for(int i=0; i<urlTests.getLength(); i++){
			String bibFile = null;
			String url = null;
			String description = null;
			String scraperName = null;
			String id = null;
			try {
				Element urlTest = (Element) urlTests.item(i);

				bibFile = null;
				url = null;
				description = null;
				scraperName = null;
				id = null;
				
				// extract id attribute
				id = urlTest.getAttribute(ATTRIBUTE_ID);
				
				// extract BibFile element
				Element bibFileElement = (Element) urlTest.getElementsByTagName(ELEMENT_BIB_FILE).item(0);
				bibFile = bibFileElement.getFirstChild().getNodeValue();

				// extract TestDescription element
				Element testDescriptionElement = (Element) urlTest.getElementsByTagName(ELEMENT_Test_DESCRIPTION).item(0);
				description = testDescriptionElement.getFirstChild().getNodeValue();

				// extract URL element
				Element urlElement = (Element) urlTest.getElementsByTagName(ELEMENT_URL).item(0);
				url = urlElement.getFirstChild().getNodeValue();

				// extract URL element
				Element scraperElement = (Element) urlTest.getElementsByTagName(ELEMENT_SCRAPER).item(0);
				scraperName = scraperElement.getFirstChild().getNodeValue();
				
				// get expected reference
				String expectedReference = getExpectedReference(bibFile);
				
				// get associated scraper
				Scraper scraper = getScraperClass(scraperName);
				
				// use test only if all values are given
				if(url != null && expectedReference != null && scraper != null && description != null && id != null){
					URLScraperUnitTest test = new URLScraperUnitTest(url, expectedReference, scraper, description, id);
					tests.add(test);
				}
			
			// TODO: print failues during parsing url tests?
			} catch (Exception e) {
				ParseFailureMessage.printParseFailureMessage(e, id);
			}
		}
		return tests;
	}

	/**
	 * Loads bib file with expected reference for test case.
	 * @param bibFile Name of the bib file as String
	 * @return Content from bib file as String
	 * @throws IOException
	 */
	private String getExpectedReference(String bibFile) throws IOException{
		InputStream is = ClassLoader.getSystemResourceAsStream("scraper/test/data/" + bibFile);
		StringWriter writer = new StringWriter();
		
		int read = is.read();
		while(read != -1){
			writer.write(read);
			read = is.read();
		}
		
		// clean up
		writer.flush();
		writer.close();
		is.close();
		
		return writer.toString();
	}
	
	/**
	 * Get Scraper which is referenced in test case. 
	 * @param scraperName Name of a Scraper sub class as String.
	 * @return referenced Scraper
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private Scraper getScraperClass(String scraperName) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		return (Scraper) Class.forName(scraperName).newInstance();
	}
	
	/**
	 * Reads and parse UnitTestData.xml
	 * @return Document representation from UnitTestData.xml
	 * @throws SAXException
	 * @throws IOException
	 */
	private Document readUnitTestData() throws SAXException, IOException{
		InputSource is = new InputSource(this.getClass().getResourceAsStream(UNIT_TEST_DATA_XML_FILE_NAME));
		DOMParser parser = new DOMParser();
		parser.parse(is);
		return parser.getDocument();
	}
}
