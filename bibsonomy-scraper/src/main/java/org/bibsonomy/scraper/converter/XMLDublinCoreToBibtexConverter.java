/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
/**
 * 
 */
package org.bibsonomy.scraper.converter;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author Mohammed Abed
 * 
 * this converter converts the xml DC format to bibtex
 */
public class XMLDublinCoreToBibtexConverter extends AbstractDublinCoreToBibTeXConverter {
	private static final Log log = LogFactory.getLog(XMLDublinCoreToBibtexConverter.class);
	
	private static final Pattern isbnPattern = Pattern.compile("([^0-9]|^)(978\\d{9}[\\dx]|979\\d{9}[\\dx]|\\d{9}[\\dx])([^0-9x]|$)", Pattern.CASE_INSENSITIVE);
	private static final Pattern HTTP_PATTERN = Pattern.compile("https://.*(http://.*)$");
	
	/**
	 * this method return the value of the required node
	 * 
	 * @param element the element that contains the value
	 * 
	 * @return the value of the node
	 */
	private static String getCharacterDataFromElement(Element element) {
		final Node child = element.getFirstChild();
		if (child instanceof CharacterData) {
			final CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.converter.AbstractDublinCoreToBibTeXConverter#extractData(java.lang.String)
	 */
	@Override
	protected Map<String, String> extractData(String dublinCore) {
		final Map<String, String> data = new HashMap<String, String>();
		try {
			final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(dublinCore));
			final Document doc = db.parse(is);
			NodeList node = null;
			Element line = null;
			String value = "";
			String lang = "";
			
			/*
			 * search the type of the citation
			 */
			node = doc.getElementsByTagName("type");
			line = (Element) node.item(0);
			value = getCharacterDataFromElement(line);
			addOrAppendField(TYPE_KEY, value, lang, data);
			
			/*
			 * search the title of the citation
			 */
			node = doc.getElementsByTagName("title");
			line = (Element) node.item(0);
			value = getCharacterDataFromElement(line);
			addOrAppendField(TITLE_KEY, value, lang, data);
			
			/*
			 * search the authors of the citation
			 */
			node = doc.getElementsByTagName("creator");
			for (int i = 0; i < node.getLength(); i++) {
				line = (Element) node.item(i);
				value = getCharacterDataFromElement(line);
				addOrAppendField(AUTHOR_KEY, value, lang, data);
			}
			
			/*
			 * search the authors of the citation
			 */
			node = doc.getElementsByTagName("description");
			for (int i = 0; i < node.getLength(); i++) {
				line = (Element) node.item(i);
				value = getCharacterDataFromElement(line);
				addOrAppendField("description", value, lang, data);
			}
			
			/*
			 * search the identifiers of the citation
			 */
			node = doc.getElementsByTagName("identifier");
			for (int i = 0; i < node.getLength(); i++) {
				line = (Element) node.item(i);
				value = getCharacterDataFromElement(line);
				addOrAppendField(ID_KEY, value, lang, data);
				/*
				 * extracting the ISBN
				 */
				if (value.startsWith("URN:ISBN:")) {
					
					/*
					 * ISBN 10 and ISBN 13
					 */
					final Matcher M_ISB10 = isbnPattern.matcher(value);
					if(M_ISB10.find()) {
						value = M_ISB10.group(2);
						addOrAppendField("ISBN", value, lang, data);
					}
				}
				
				/*
				 * extract url
				 */
				if (value.contains("http://")) {
					final Matcher M_HTTP_PATTERN = HTTP_PATTERN.matcher(value);
					if(M_HTTP_PATTERN.find()) {
						value = M_HTTP_PATTERN.group(1);
						addOrAppendField("url", value, lang, data);
					}
				}
			}
			
			/*
			 * search the Date of the citation
			 */
			node = doc.getElementsByTagName("date");
			line = (Element) node.item(0);
			value = getCharacterDataFromElement(line);
			value = extractYear(value);
			addOrAppendField("year", value, lang, data);
			
			/*
			 * search the publisher of the citation
			 */
			node = doc.getElementsByTagName("publisher");
			line = (Element) node.item(0);
			value = getCharacterDataFromElement(line);
			addOrAppendField("publisher", value, lang, data);
			
			/*
			 * search the language of the citation
			 */
			node = doc.getElementsByTagName("language");
			line = (Element) node.item(0);
			value = getCharacterDataFromElement(line);
			addOrAppendField("language", value, lang, data);
			
			/*
			 * search the subject of the citation
			 */
			node = doc.getElementsByTagName("subject");
			for (int i = 0; i < node.getLength(); i++) {
				
				line = (Element) node.item(i);
				value = getCharacterDataFromElement(line);
				addOrAppendField("subject", value, lang, data);
			}
		} catch (final Exception e) {
			log.error("error parsing DC XML", e);
		}
		return data;
	}
}
