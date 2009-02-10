package org.bibsonomy.scraper.util;

import java.io.ByteArrayInputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
	
	
	/** Extract the text in one parent node and all its children (recursively!). 
	 * 
	 * @param node
	 * @return All text below the given node.
	 */
	public static String getText(final Node node) {
		final StringBuffer text = new StringBuffer();

		final String value = node.getNodeValue();

		if (value != null){
			text.append(value);
		}

		if (node.hasChildNodes()) {
			final NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				text.append(getText(children.item(i)));
			}
		}

		return text.toString();
	}
}
