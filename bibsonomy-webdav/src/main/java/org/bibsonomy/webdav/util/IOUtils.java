package org.bibsonomy.webdav.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Originally from com.atlassian.confluence.extra.webdav.impl.ConfluenceBackend.
 * Refactored to this class to keep the ResourceBackend implementation small.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class IOUtils {

	// The UTF Byte Order Marker - required by Notepad to read UTF files.
	static final char UTF_BOM = '\uFEFF';

	private static final String DEFAULT_ENCODING = "iso-8859-1";

	/**
	 * Never create an instance.
	 */
	private IOUtils() {
	}

	/**
	 * Returns an InputStream which will read the specified String value. The
	 * encoding specifies what encoding the resulting stream of bytes will be
	 * in.
	 * 
	 * @param content
	 *            The string to stream.
	 * @param encoding
	 *            The encoding standard to use.
	 * @return The stream.
	 * @throws IOException
	 *             if there was a problem creating the stream.
	 * @throws UnsupportedEncodingException
	 *             if the specified encoding standard is not supported.
	 */
	public static InputStream readStringAsInputStream(final String content, String encoding) throws IOException {
		if (encoding == null) encoding = DEFAULT_ENCODING;
		return new ByteArrayInputStream(getByteArray(content, encoding));
	}

	/**
	 * Converts the specified string to a byte array using the specified
	 * encoding. If the encoding is UTF, a Byte Order Marker (BOM) is added to
	 * the front of it.
	 * 
	 * @param content
	 *            The string to convert.
	 * @param encoding
	 *            The encoding to convert into.
	 * @return The byte array.
	 * @throws UnsupportedEncodingException
	 *             if the specified encoding is not supported.
	 */
	public static byte[] getByteArray(String content, final String encoding) throws UnsupportedEncodingException {
		if (content != null) {
			// First, clean up the newlines for consistency.
			content = content.replaceAll("\r", "").replaceAll("\n", "\r\n");

			// Add the BOM so so that apps will know it's UTF.
			if (encoding != null && encoding.toLowerCase().startsWith("utf-")) content = UTF_BOM + content;
			return content.getBytes(encoding);
		}

		return null;
	}

	/**
	 * Reads an InputStream with the given enconding into a string.
	 * 
	 * @param input
	 * @param encoding
	 * @return data from the InputStream as a string
	 * @throws IOException
	 */
	public static String readInputStreamAsString(final InputStream input, String encoding) throws IOException {
		if (encoding == null) encoding = DEFAULT_ENCODING;

		final InputStreamReader in = new InputStreamReader(input, encoding);
		final StringBuffer value = new StringBuffer();

		char[] chars = new char[1024];
		int read;
		while ((read = in.read(chars)) >= 0) {
			value.append(chars, 0, read);
		}

		String stringValue = value.toString();
		if (stringValue.charAt(0) == UTF_BOM) {
			stringValue = stringValue.substring(1);
		}

		return stringValue;
	}
}