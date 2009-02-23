package org.bibsonomy.rest.renderer.xml.tools;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import org.bibsonomy.util.XmlUtils;

/**
 * PrintWriter which preprocesses all content to be printed/written 
 * by replacing control characters (e.g., in order to yield valid
 * XML). It uses hereby the method {@link StringUtils#escapeControlCharacters}}.
 * 
 * @see org.bibsonomy.util.StringUtils
 * @see java.io.PrintWriter
 * @author Dominik Benz
 * @version $Id$
 */
public class EscapingPrintWriter extends Writer {
		
	/** PrintWriter */
	private PrintWriter pw;
	
	/**
	 * Create a new instance of an EscapingPrintWriter which is 
	 * backed by a PrintWriter 
	 * 
	 * @param out an OutputStream
	 */
	public EscapingPrintWriter(OutputStream out) {
		this.pw = new PrintWriter(out);
	}
	
	/* (non-Javadoc)
	 * @see java.io.Writer#close()
	 */
	@Override
	public void close() throws IOException {
		this.pw.close();

	}

	/* (non-Javadoc)
	 * @see java.io.Writer#flush()
	 */
	@Override
	public void flush() throws IOException {
		this.pw.flush();

	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(char[], int, int)
	 */
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		this.pw.write(XmlUtils.removeXmlControlCharacters(cbuf), off, len);
	}
}
