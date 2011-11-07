package org.bibsonomy.testutil;

import java.net.URI;
import java.net.URISyntaxException;

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
}
