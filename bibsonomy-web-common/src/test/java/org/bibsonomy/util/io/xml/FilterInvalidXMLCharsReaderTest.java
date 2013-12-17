/**
 *
 *  BibSonomy-Web-Common - Common things for web
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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

package org.bibsonomy.util.io.xml;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.bibsonomy.util.StringUtils;
import org.junit.Test;


/**
 * @author dzo
  */
public class FilterInvalidXMLCharsReaderTest {

	/**
	 * tests {@link FilterInvalidXMLCharsReader#read()}
	 * @throws IOException
	 */
	@Test
	public void testRead() throws IOException {
		final BufferedReader reader = new BufferedReader(new FilterInvalidXMLCharsReader(new StringReader("This is \uFFFE\uFFFF my test string")));
		final String result = StringUtils.getStringFromReader(reader);
		assertEquals("This is  my test string", result.trim());
	}
}
