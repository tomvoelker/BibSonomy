package org.bibsonomy.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Holds static values that can be created at initialization.
 * 
 * @author Christian Schenk
 */
public class DefaultValues {

	private static final DefaultValues singleton = new DefaultValues();
	private static URL bibsonomyURL;

	private DefaultValues() {
		try {
			bibsonomyURL = new URL("http://www.bibsonomy.org");
		} catch (final MalformedURLException ex) {}
	}

	public static DefaultValues getInstance() {
		return singleton;
	}

	public URL getBibsonomyURL() {
		return bibsonomyURL;
	}
}