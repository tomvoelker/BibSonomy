/**
 * BibSonomy-Web-Common - Common things for web
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.StringWriter;

import org.junit.Test;

/**
 * @author dzo
 */
public class FilterInvalidXMLCharsWriterTest {
	
	@Test
	public void testWrite() throws Exception {
		String s = "\u0000\u0001\u0002\u0003\u0004\u0005" +
					"\u0006\u0007\u0008\u000B\u000C\u000E\u000F\u0010\u0011\u0012" +
					"\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C" +
					"\u001D\u001E\u001F\uFFFE\uFFFF";
		String cleaned = write(s);
		assertEquals("", cleaned);

		// text with control char
		s = "This is a text with a \u0002 control character";
		cleaned = write(s);
		// check if control char has been removed
		assertEquals("This is a text with a  control character", cleaned);

		// check replacement
		s = "\u0002";
		cleaned = write(s, true);
		assertEquals("\uFFFD", cleaned);
	}

	private String write(String s) throws Exception {
		return write(s, false);
	}

	private String write(String string, final boolean replace) throws Exception {
		final StringWriter out = new StringWriter();
		final BufferedWriter writer = new BufferedWriter(new FilterInvalidXMLCharsWriter(out, replace));
		writer.write(string);
		writer.close();
		return out.toString();
	}
}
