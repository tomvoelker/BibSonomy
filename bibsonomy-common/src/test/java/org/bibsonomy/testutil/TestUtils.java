/**
 *
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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

package org.bibsonomy.testutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Ignore;

/**
 * @author dzo
 * @version $Id$
 */
@Ignore
public final class TestUtils {
	private TestUtils() {}

	/**
	 * creates an uri from a string
	 * @param uri
	 * @return the URI
	 */
	public static URI createURI(final String uri) {
		try {
			return new URI(uri);
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * @param url
	 * @return the URL
	 */
	public static URL createURL(final String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * get a {@link BufferedReader} from a string
	 * @param string
	 * @return a {@link BufferedReader}
	 */
	public static BufferedReader getReaderForString(String string) {
		return new BufferedReader(new StringReader(string));
	}

	/**
	 * @param fileName
	 * @return the file contents as string
	 * @throws IOException
	 */
	public static String readEntryFromFile(final String fileName) throws IOException {
		final InputStream stream = TestUtils.class.getClassLoader().getResourceAsStream(fileName);
		return toString(stream);
	}

	public static String toString(final InputStream stream) throws IOException {
		final StringBuffer resultString = new StringBuffer();
		final BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		String line = null;
		while ((line = in.readLine()) != null) {
			resultString.append(line + "\n");
		}
		return resultString.toString();
	}
}
