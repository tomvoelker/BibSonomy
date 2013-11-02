package org.bibsonomy.importer.bookmark.service.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.xml.utils.XMLChar;

/**
 * This class wraps an InputStream filtering out invalid UTF-8/16 encoded XML characters.
 * 
 * @author MarcelM
 */
public class FilterInvalidXMLCharsInputStream extends InputStream{
	
	private final InputStream orig;
	
	public FilterInvalidXMLCharsInputStream(InputStream orig) {
		this.orig = orig;
	}
	
	@Override
	public int read() throws IOException {
		int c = orig.read();
		//Check for End of File
		if (c == -1) {
			return c;
		}
		if (XMLChar.isValid(c)) {
			return c;
		}
		return this.read();
	}
	
	@Override
	public void close() throws IOException {
		if (orig != null) {
			orig.close();
		}
		super.close();
	}

}
