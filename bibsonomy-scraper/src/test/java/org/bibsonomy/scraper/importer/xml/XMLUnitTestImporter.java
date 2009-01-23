package org.bibsonomy.scraper.importer.xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import org.bibsonomy.scraper.ParseFailureMessage;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScraperUnitTest;
import org.bibsonomy.scraper.URLTest.URLScraperUnitTest;
import org.bibsonomy.scraper.importer.IUnitTestImporter;


import com.sun.org.apache.xerces.internal.parsers.DOMParser;

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
		XMLReader xmlreader;
		xmlreader = XMLReaderFactory.createXMLReader();

		XMLUnitTestHandler handler = new XMLUnitTestHandler(); 
		xmlreader.setContentHandler(handler);
		xmlreader.setEntityResolver(handler);
		xmlreader.setErrorHandler(handler);

		InputSource is = new InputSource(this.getClass().getResourceAsStream(UNIT_TEST_DATA_XML_FILE_NAME));
		
		xmlreader.parse(is);
		
		return handler.getTests();
	}
	
}
