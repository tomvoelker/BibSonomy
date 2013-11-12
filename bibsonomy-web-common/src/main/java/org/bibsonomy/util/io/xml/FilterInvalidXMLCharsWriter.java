package org.bibsonomy.util.io.xml;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.xml.serializer.utils.XMLChar;

/**
 * was EscapingPrintWriter
 * 
 * @author dzo
 * @version $Id$
 */
public class FilterInvalidXMLCharsWriter extends FilterWriter {
	private static final char ILLEGAL_CHAR_SUBSTITUTE = '\uFFFD';
	
	
	private boolean replaceInvalidChars = false;
	
	/**
	 * default constructor
	 * @param out
	 */
	public FilterInvalidXMLCharsWriter(Writer out) {
		super(out);
	}
	
	/**
	 * constructor for setting replaceInvalidChars
	 * @param out
	 * @param replaceInvalidChars
	 */
	public FilterInvalidXMLCharsWriter(final Writer out, boolean replaceInvalidChars) {
		super(out);
		this.replaceInvalidChars = replaceInvalidChars;
	}
	
	@Override
	public void write(int c) throws IOException {
		if (XMLChar.isValid(c)) {
			super.write(c);
		} else if (this.replaceInvalidChars) {
			super.write(ILLEGAL_CHAR_SUBSTITUTE);
		}
	}
	
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		for (int i = off; i < (off + len); i++) {
			this.write(cbuf[i]);
		}
	}
}
