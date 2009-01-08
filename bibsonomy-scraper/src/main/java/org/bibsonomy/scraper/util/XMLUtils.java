package org.bibsonomy.scraper.util;

import java.io.ByteArrayInputStream;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

/**
 * @author rja
 * @version $Id$
 */
public class XMLUtils {

	/*
	 * As soon as we know if JTidy is thread safe, we can use a static instance of it
	 */
//	private static final Tidy tidy = new Tidy();
//	static {
//		tidy.setQuiet(true);
//		tidy.setShowWarnings(false);// turns off warning lines
//	}

	/** Parses a page and returns the DOM
	 * 
	 * @param content - The XML as string.
	 * @return The DOM tree of the XML string.
	 */
	public static Document getDOM(final String content) {
		final Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setShowWarnings(false);// turns off warning lines
		return tidy.parseDOM(new ByteArrayInputStream(content.getBytes()), null);
	}
}
