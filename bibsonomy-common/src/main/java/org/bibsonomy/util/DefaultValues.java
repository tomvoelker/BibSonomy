package org.bibsonomy.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Holds static values that can be created at initialization.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class DefaultValues {

	private static final DefaultValues singleton = new DefaultValues();
	private static URL bibsonomyURL;

	/*
	 * FIXME: hard coded URLs are BAD; please remove them
	 */
	private DefaultValues() {
		try {
			bibsonomyURL = new URL("http://www.bibsonomy.org");
		} catch (final MalformedURLException ex) {}
	}

	public static DefaultValues getInstance() {
		return singleton;
	}

	/*
	 * FIXME: BibSonomy should never occur in this way in the source code
	 */
	public URL getBibsonomyURL() {
		return bibsonomyURL;
	}
}