package org.bibsonomy.layout.jabref;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Callback handler for the SAX parser.
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class JabrefLayoutXMLHandler extends DefaultHandler {

	private StringBuffer buf = new StringBuffer();

	private List<JabrefLayout> layoutDefinitions;
	
	private JabrefLayout currentLayoutDefinition;
	
	public void startDocument() {
		 layoutDefinitions = new LinkedList<JabrefLayout>();
	}

	public void endDocument() {
		// nothing to do
	}

	public void startElement (final String uri, final String name, final String qName, final Attributes atts) {
		buf = new StringBuffer();
		if ("layout".equals(name)) {
			currentLayoutDefinition = new JabrefLayout(atts.getValue("name"));
		}
	}

	/** Collect characters.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters (final char ch[], final int start, final int length) {
		/*
		 * replace arbitrary long sequences of whitespace by one space.
		 */
		final String s = new String(ch, start, length).replaceAll("\\s+", " ");
		buf.append(s);
		
	}

	public void endElement (final String uri, final String name, final String qName) {
		if ("layout".equals(name)) {
			layoutDefinitions.add(currentLayoutDefinition);
		} else if ("displayName".equals(name)) {
			currentLayoutDefinition.setDisplayName(getBuf());
		} else if ("description".equals(name)) {
			currentLayoutDefinition.setDescription(getBuf());
		} else if ("baseFileName".equals(name)) {
			currentLayoutDefinition.setBaseFileName(getBuf());
		} else if ("directory".equals(name)) {
			currentLayoutDefinition.setDirectory(getBuf());
		} else if ("extension".equals(name)) {
			currentLayoutDefinition.setExtension(getBuf());
		} else if ("mimeType".equals(name)) {
			currentLayoutDefinition.setMimeType(getBuf());
		} 
	}

	private String getBuf() {
		return buf.toString().trim();
	}

	public List<JabrefLayout> getLayouts() {
		return layoutDefinitions;
	}

}

