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

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.xml.serializer.utils.XMLChar;

/**
 * @author dzo
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
