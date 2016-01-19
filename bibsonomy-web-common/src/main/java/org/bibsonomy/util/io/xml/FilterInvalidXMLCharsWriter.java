/**
 * BibSonomy-Web-Common - Common things for web
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.util.io.xml;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.xml.serializer.utils.XMLChar;

/**
 * was EscapingPrintWriter
 * 
 * @author dzo
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
