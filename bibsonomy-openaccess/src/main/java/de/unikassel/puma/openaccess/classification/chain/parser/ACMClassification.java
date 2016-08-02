/**
 * BibSonomy-OpenAccess - Check Open Access Policies for Publications
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package de.unikassel.puma.openaccess.classification.chain.parser;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedHashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.unikassel.puma.openaccess.classification.ClassificationObject;
import de.unikassel.puma.openaccess.classification.ClassificationXMLParser;

/**
 * @author philipp
 */
public class ACMClassification extends ClassificationXMLParser {

	private static final String NAME = "ACM";

	private static final String DELIMITER = ".";
	
	private StringBuffer buf = new StringBuffer();
	
	private boolean skip = false;
	private String skipElement = "";

	private String startNode = "";
	private String startDescription = "";
	
	
	@Override
	public void startDocument() {
		this.classifications = new LinkedHashMap<String, ClassificationObject>();
		this.buf = new StringBuffer();
	}

	@Override
	public void endDocument() {
		// noop
	}

	@Override
	public void startElement (final String uri, final String name, final String qName, final Attributes atts) throws SAXException {
		
		if(this.skip) {
			return;
		}

		if ("node".equals(qName)) {

			if(atts.getLength() == 2) {
				
				if(atts.getLocalName(0).equals("id") && atts.getLocalName(1).equals("label")) {
					final String id = atts.getValue(0);
					
					if(id.equals("acmccs98")) {
						return;
					}
					
					if((id.length() < 4) && !id.endsWith(".")) {
						this.startNode = id;
						this.startDescription = atts.getValue(1);
					} else {
//						 += ".";
						
						if(present(this.startNode)) {
							this.startNode += ".";
							this.classificate(this.startNode, this.startDescription);
							
							this.startDescription = this.startNode = "";
						}
						
						this.classificate(id, atts.getValue(1));
					}
				}
			}
			
		} else if("isComposedBy".equals(qName)) {

		} else if("isRelatedTo".equals(qName)) {
			this.skip = true;
			this.skipElement = "isRelatedTo";
				
		} else if("hasNote".equals(qName)) {
			this.skip = true;
			this.skipElement = "hasNote";
			
		} else {
			throw new SAXException("Unable to parse");
		}
		this.buf = new StringBuffer();
	}

	/** Collect characters.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters (final char ch[], final int start, final int length) {
		this.buf.append(ch, start, length);
	}
	
	@Override
	public void endElement (final String uri, final String name, final String qName) throws SAXException {
		if (this.skip) {
			if (qName.equals(this.skipElement)) {
				this.skip = false;
				this.skipElement = "";
			}
		} else if(present(this.startNode)) {
			this.classificate(this.startNode, this.startDescription);
			this.startNode = this.startDescription = "";
		}
		
	}
	
	private void requClassificate(String name, final String description, final ClassificationObject object) {
		if(name.isEmpty()) {
			return;
		}
		
		final int delimiter = name.indexOf('.') +1;
		String actual;
		
		if (delimiter != 0) {
			actual = name.substring(0, delimiter);
			name = name.substring(delimiter, name.length());
		} else {
			actual = name;
			name = "";
		}
	
		if (object.getChildren().containsKey(actual)) {
			this.requClassificate(name, description, object.getChildren().get(actual));
		} else {
			if (name.isEmpty()) {
				final ClassificationObject co = new ClassificationObject(actual, description);
				object.addChild(actual, co);
			} else {
				final ClassificationObject co = new ClassificationObject(actual, description);
				object.addChild(actual, co);
				this.requClassificate(name, description, co);
			}
		}
	}
	
	private void classificate(String name, final String description) {
		final int delimiter = name.indexOf('.') +1;
		String actual;
		
		if (delimiter != 0) {
			actual = name.substring(0, delimiter);
			name = name.substring(delimiter, name.length());
		} else {
			actual = name;
			name = "";
		}
	
		if (this.classifications.containsKey(actual)) {
			this.requClassificate(name, description, this.classifications.get(actual));
		} else {
			
			final ClassificationObject co = new ClassificationObject(actual, description);
			this.classifications.put(actual, co);
			this.requClassificate(name, description, co);
		}
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDelimiter() {
		return DELIMITER;
	}

}
