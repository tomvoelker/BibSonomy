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

package org.bibsonomy.web.spring.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.springframework.core.convert.converter.Converter;


/**
 * @author dzo
 */
public class StringToURLConverterTest {
	private static final Converter<String, URL> STRING_URL_CONVERTER = new StringToURLConverter();
	
	private static final String URL_1 = "http://bibsonomy.org";
	private static final String URL_2 = "file://var/log/kernel.log";
	
	@Test
	public void testConvert() throws MalformedURLException {
		assertNull(STRING_URL_CONVERTER.convert("   "));
		
		assertEquals(new URL(URL_1), STRING_URL_CONVERTER.convert(URL_1));
		assertEquals(new URL(URL_2), STRING_URL_CONVERTER.convert(URL_2));
	}
}
