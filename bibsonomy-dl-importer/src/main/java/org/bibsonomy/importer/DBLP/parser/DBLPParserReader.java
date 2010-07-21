package org.bibsonomy.importer.DBLP.parser;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.util.Date;


public class DBLPParserReader{

	private DBLPParserHandler handler;

	private String url;

	public DBLPParserReader(String url){
		handler = null;
		this.url = url;
	}

	public void readDBLP(Date dblpdate) throws SAXException, IOException {
		//prepare parse xml
		XMLReader xmlreader;
		xmlreader = XMLReaderFactory.createXMLReader();

		handler = new DBLPParserHandler(dblpdate);
		xmlreader.setContentHandler(handler);
		xmlreader.setEntityResolver(handler);
		xmlreader.setErrorHandler(handler);

		xmlreader.parse(url);
	}

	public DBLPParseResult getResult() {
		return handler.getResult();
	}

}