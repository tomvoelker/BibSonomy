/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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
package org.bibsonomy.testutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.bibsonomy.util.StringUtils;
import org.junit.Ignore;

/**
 * @author dzo
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
		return StringUtils.getStringFromReader(new BufferedReader(new InputStreamReader(stream)));
	}
	
	/**
	 * 
	 * @param file
	 * @return the file contents as string
	 * @throws IOException
	 */
	public static String readEntryFromFile(final File file) throws IOException {
		final InputStream stream = new FileInputStream(file.getPath());
		return StringUtils.getStringFromReader(new BufferedReader(new InputStreamReader(stream)));
	}
}
