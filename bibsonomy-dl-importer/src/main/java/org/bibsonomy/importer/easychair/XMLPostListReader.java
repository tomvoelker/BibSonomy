package org.bibsonomy.importer.easychair;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/** Reads an EasyChair XML file, parses it using SAX and the {@link XMLHandler}, 
 * and returns the extracted BibTeX posts. 
 * 
 * @author rja
 * @version $Id$
 */
public class XMLPostListReader {

	private Reader reader;
	
	public XMLPostListReader (final Reader reader) {
		this.reader = reader;
	}
	
	/** Reads a list containing BibTeX posts from an EasyChair XML file.
	 * 
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	public List<Post<BibTex>> readPostList() throws SAXException, IOException {
		final XMLReader xr = XMLReaderFactory.createXMLReader();
		/*
		 * SAX callback handler
		 */
		final XMLHandler handler = new XMLHandler();
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);
		xr.parse(new InputSource(reader));
		
		return handler.getList();
	}

}
