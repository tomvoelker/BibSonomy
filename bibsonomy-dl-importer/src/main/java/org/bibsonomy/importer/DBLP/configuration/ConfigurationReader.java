package org.bibsonomy.importer.DBLP.configuration;

import java.io.File;
import java.io.FileReader;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/*
 * this class prepare and starts the parse of the constants XML
 */
public class ConfigurationReader{
	
	private ConfigurationHandler handler;
	
	public ConfigurationReader(){
		handler = null;
	}

	public void readConfiguration(final File constantsXML) throws Exception{
		//prepare parse xml
		final XMLReader xmlreader = XMLReaderFactory.createXMLReader(); 	
		handler = new ConfigurationHandler();
		xmlreader.setContentHandler(handler);
		xmlreader.setErrorHandler(handler);

		// parse XML
		xmlreader.parse(new InputSource(new FileReader(constantsXML)));
	}
	
	public Configuration getConResult(){
		return handler.getConResult();
	}
	
}