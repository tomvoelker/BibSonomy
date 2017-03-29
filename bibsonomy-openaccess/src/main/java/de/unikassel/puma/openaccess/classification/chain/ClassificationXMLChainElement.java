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
package de.unikassel.puma.openaccess.classification.chain;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URL;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.unikassel.puma.openaccess.classification.Classification;
import de.unikassel.puma.openaccess.classification.ClassificationSource;
import de.unikassel.puma.openaccess.classification.ClassificationXMLParser;

/**
 * @author philipp
 */
public class ClassificationXMLChainElement implements ClassificationSource {

	private final ClassificationXMLParser classificationParser;
	
	private ClassificationSource next = null;
	
	public ClassificationXMLChainElement(final ClassificationXMLParser classParser) {
		this.classificationParser = classParser;
	}
	
	public void setNext(final ClassificationSource next) {
		this.next = next;
	}

	@Override
	public Classification getClassification(final URL url) throws IOException {
		try  {
			final XMLReader xr = XMLReaderFactory.createXMLReader();
			/*
			 * SAX callback handler
			 */
			xr.setContentHandler(this.classificationParser);
			xr.setErrorHandler(this.classificationParser);
			xr.parse(url.getPath());
			
			if (!present(this.classificationParser.getList())) {
				if (!present(this.next)) {
					return null;
				}
				
				return this.next.getClassification(url);
			}
			
			return new Classification(this.classificationParser.getName(), this.classificationParser.getList(), this.classificationParser.getDelimiter());
		} catch (final SAXException e) {
			//unable to parse
			if (!present(this.next)) {
				return null;
			}
			
			return this.next.getClassification(url);
		}
	}
}
