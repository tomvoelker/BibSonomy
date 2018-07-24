/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * IO helper methods
 *
 * @author dzo
 */
public final class IOUtils {

	private IOUtils() {}

	/**
	 * loads the content of a file into a string
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public static String readInputStreamToString(final InputStream stream) throws IOException {
		final StringBuilder builder = new StringBuilder();
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StringUtils.DEFAULT_CHARSET))) {

			while (reader.ready()) {
				builder.append(reader.readLine() + "\n");
			}
		}

		return builder.toString().trim();
	}
}
