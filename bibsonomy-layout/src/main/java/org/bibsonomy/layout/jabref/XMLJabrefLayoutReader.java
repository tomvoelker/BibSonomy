package org.bibsonomy.layout.jabref;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 
 * @author: rja
 * @version: $Id$ $Author$
 * 
 */
public class XMLJabrefLayoutReader {

	private Reader reader;

	public XMLJabrefLayoutReader(final Reader reader) {
		this.reader = reader;
	}

	/**
	 * Reads a list containing BibTeX posts from an EasyChair XML file.
	 * 
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	public List<JabrefLayout> getJabrefLayouts() throws SAXException, IOException {
		final XMLReader xr = XMLReaderFactory.createXMLReader();
		/*
		 * SAX callback handler
		 */
		final JabrefLayoutXMLHandler handler = new JabrefLayoutXMLHandler();
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);
		xr.parse(new InputSource(reader));

		return handler.getLayouts();
	}
}
