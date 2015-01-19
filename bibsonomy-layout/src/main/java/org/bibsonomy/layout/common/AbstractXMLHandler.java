/**
 * BibSonomy-Layout - Layout engine for the webapp.
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.layout.common;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.Layout;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * abstract XMLHandler to read layout config files
 *
 * @author dzo
 * @param <L> 
 */
public abstract class AbstractXMLHandler<L extends Layout> extends DefaultHandler {
	// need to save the attribute from the description element in the
	// startElement callback method to use it in the endElement method	
	private String languageAttribute;
	
	private StringBuffer buf = new StringBuffer();
	
	private List<L> layoutDefinitions;
	
	private L currentLayoutDefinition;
	
	@Override
	public void startDocument() {
		 layoutDefinitions = new LinkedList<L>();
	}
	
	/** Collect characters.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters (final char ch[], final int start, final int length) {
		/*
		 * replace arbitrary long sequences of whitespace by one space.
		 */
		final String s = new String(ch, start, length).replaceAll("\\s+", " ");
		buf.append(s);
	}
	
	@Override
	public void startElement(final String uri, final String name, final String qName, final Attributes atts) {
		buf = new StringBuffer();
		
		if (this.isLayoutElement(name)) {
			currentLayoutDefinition = this.initLayout(name, atts);
			if (atts.getValue("public") != null){
				currentLayoutDefinition.setPublicLayout(Boolean.parseBoolean(atts.getValue("public")));
			}
		} else if ("description".equals(name)) {
			this.languageAttribute = atts.getValue("xml:lang");
		}
	}
	
	@Override
	public void endElement (final String uri, final String name, final String qName) {
		if (this.isLayoutElement(name)) {
			layoutDefinitions.add(currentLayoutDefinition);
		} else if ("displayName".equals(name)) {
			currentLayoutDefinition.setDisplayName(getBuf());
		}else if ("description".equals(name)) {
			currentLayoutDefinition.addDescription(this.languageAttribute, getBuf());
		} else if ("extension".equals(name)) {
			currentLayoutDefinition.setExtension(getBuf());
		} else if ("mimeType".equals(name)) {
			currentLayoutDefinition.setMimeType(getBuf());
		} else if ("isFavorite".equals(name)) {
			currentLayoutDefinition.setIsFavorite(Boolean.parseBoolean(getBuf()));
		}
		
		this.endElement(uri, name, qName, currentLayoutDefinition);
	}
	
	/**
	 * @param uri
	 * @param name
	 * @param qName
	 * @param currentLayout
	 */
	protected void endElement(String uri, String name, String qName, L currentLayout) {
		// noop
	}

	protected abstract boolean isLayoutElement(String name);
	
	protected abstract L initLayout(String name, Attributes attrs);
	
	protected String getBuf() {
		return buf.toString().trim();
	}
	
	/**
	 * 
	 * @return the layoutDefinitions
	 */
	public List<L> getLayouts() {
		return layoutDefinitions;
	}
}
