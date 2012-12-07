package org.bibsonomy.importer.DBLP.parser;

import java.io.IOException;
import java.util.Date;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


public class DBLPParserReader{

	private DBLPParserHandler handler;

	private final String url;

	public DBLPParserReader(String url){
		this.handler = null;
		this.url = url;
	}

	public void readDBLP(Date dblpdate) throws SAXException, IOException {
		//prepare parse xml
		final XMLReader xmlreader = XMLReaderFactory.createXMLReader();

		this.handler = new DBLPParserHandler(dblpdate);
		xmlreader.setContentHandler(handler);
		xmlreader.setEntityResolver(handler);
		xmlreader.setErrorHandler(handler);

		xmlreader.parse(url);
	}

	public DBLPParseResult getResult() {
		return handler.getResult();
	}

}