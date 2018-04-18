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
