package org.bibsonomy.util.io.xml;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.xml.serializer.utils.XMLChar;

/**
 * @author dzo
 * @version $Id$
 */
public class FilterInvalidXMLCharsReader extends FilterReader {
	
	/**
	 * @param in
	 */
	public FilterInvalidXMLCharsReader(Reader in) {
		super(in);
	}
	
	@Override
	public int read() throws IOException {
		int read = super.read();
		if (read == -1) {
			return read;
		}
		if (XMLChar.isValid(read)) {
			return read;
		}
		return this.read();
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int result = -1;
		for (int i = off; i < (off + len);) {
			int c = read();
			if (c == -1) {
				return result;
			}
			cbuf[i++] = (char) c;
			result = i - off;
		}
		return result;
	}

}
