/**
 * BibSonomy-Web-Common - Common things for web
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
package org.bibsonomy.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bibsonomy.util.io.xml.FilterInvalidXMLCharsWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

/**
 * Some utility functions for working with XML
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 */
public class XmlUtils {
	/**
	 * removes all invalid xml chars from the string
	 * @param s the string to be cleaned
	 * @return the cleaned string
	 */
	public static String removeInvalidXmlChars(final String s) {
		if ( s == null )
			return ""; //nothing to do

		//else:
		final StringWriter stringWriter = new StringWriter();
		try {
			final Writer writer = new FilterInvalidXMLCharsWriter(stringWriter);
			writer.write(s);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return stringWriter.toString();
	}
	
	/**
	 * Parses a page and returns the DOM
	 * 
	 * @param content
	 *            - The XML as string.
	 * @return The DOM tree of the XML string.
	 */
	public static Document getDOM(final String content) {
		return getDOM(content, false);
	}

	/**
	 * @param content
	 * @param xmlTags
	 *            <code>true</code>, if the content should be handled as XML
	 *            (e.g., empty tags are not removed!)
	 * @return The DOM of the given XML string
	 */
	public static Document getDOM(final String content, final boolean xmlTags) {
		return getDOM(new ByteArrayInputStream(content.getBytes()), xmlTags);
	}

	/**
	 * Parse html file from given URL into DOM tree.
	 * 
	 * @param inputURL
	 *            file's url
	 * @return parsed DOM tree
	 * @throws IOException
	 *             if html file could not be parsed.
	 */
	public static Document getDOM(final URL inputURL) throws IOException {
		return getDOM(inputURL, false);
	}

	/**
	 * Parse html file from given URL into DOM tree.
	 * 
	 * @param inputURL
	 *            file's url
	 * @param xmlTags
	 *            <code>true</code>, if the content should be handled as XML
	 *            (e.g., empty tags are not removed!)
	 * @return parsed DOM tree
	 * @throws IOException
	 *             if html file could not be parsed.
	 */
	public static Document getDOM(final URL inputURL, final boolean xmlTags) throws IOException {
		final Tidy tidy = getTidy(xmlTags);
		final HttpURLConnection connection = WebUtils.createConnnection(inputURL);
		final String encodingName = WebUtils.extractCharset(connection.getContentType());
		tidy.setInputEncoding(encodingName);
		return tidy.parseDOM(connection.getInputStream(), null);
	}

	/**
	 * Parse html file from given input stream into DOM tree.
	 * 
	 * @param inputStream
	 * @return parsed DOM tree
	 */
	public static Document getDOM(final InputStream inputStream) {
		return getDOM(inputStream, false);
	}

	/**
	 * Parse html file from given input stream into DOM tree.
	 * 
	 * @param inputStream
	 * @param xmlTags
	 * @return parsed DOM tree
	 */
	public static Document getDOM(final InputStream inputStream, final boolean xmlTags) {
		final Tidy tidy = getTidy(xmlTags);

		// we don't know the encoding now ... so we assume utf8
		tidy.setInputEncoding(StringUtils.CHARSET_UTF_8);

		return tidy.parseDOM(inputStream, null);
	}

	/**
	 * Returns a version of tidy where {@link Tidy#setXmlTags(boolean)} is set
	 * to xmlTags. <br/>
	 * Note that <code>xmlTags = true</code> is in particular neccessary for the
	 * UnAPI scraper to allow empty &lt;abbr&gt; tags.
	 * 
	 * @param xmlTags
	 * @return
	 */
	private static Tidy getTidy(final boolean xmlTags) {
		final Tidy tidy = new Tidy(); // tidy is not thread safe so we create a
										// new instance each time
		tidy.setQuiet(true);
		tidy.setShowWarnings(false);// turns off warning lines
		tidy.setShowErrors(0); // turn off error printing
		tidy.setXmlTags(xmlTags);
		return tidy;
	}

	/**
	 * Extract the text in one parent node and all its children (recursively!).
	 * 
	 * @param node
	 * @return All text below the given node.
	 */
	public static String getText(final Node node) {
		final StringBuilder text = new StringBuilder();

		final String value = node.getNodeValue();

		if (value != null) {
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
