/**
 *  
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.rest.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import org.bibsonomy.util.XmlUtils;

/**
 * PrintWriter which preprocesses all content to be printed/written 
 * by replacing control characters (e.g., in order to yield valid
 * XML). It uses hereby the method {@link XmlUtils#removeXmlControlCharacter(char[], boolean)}.
 * 
 * @see org.bibsonomy.util.XmlUtils
 * @see java.io.PrintWriter
 * @author Dominik Benz
 * @version $Id$
 */
public class EscapingPrintWriter extends Writer {
		
	/** PrintWriter */
	private final PrintWriter pw;
	
	/**
	 * Create a new instance of an EscapingPrintWriter which is 
	 * backed by a PrintWriter 
	 * 
	 * @param out an OutputStream
	 */
	public EscapingPrintWriter(final OutputStream out) {
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
	public void write(final char[] cbuf, final int off, final int len) throws IOException {
		this.pw.write(XmlUtils.removeXmlControlCharacters(cbuf, true), off, len);
	}
}
