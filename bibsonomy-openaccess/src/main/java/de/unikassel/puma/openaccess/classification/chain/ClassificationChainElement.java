package de.unikassel.puma.openaccess.classification.chain;


import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URL;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.unikassel.puma.openaccess.classification.Classification;
import de.unikassel.puma.openaccess.classification.ClassificationParser;
import de.unikassel.puma.openaccess.classification.ClassificationSource;

public class ClassificationChainElement implements ClassificationSource {

	private final ClassificationParser classificationParser;
	
	private ClassificationChainElement next = null;
	
	public ClassificationChainElement(ClassificationParser classParser) {
		this.classificationParser = classParser;
	}
	
	public void setNext(ClassificationChainElement next) {
		this.next = next;
	}
	
	public ClassificationChainElement getNext() {
		return this.next;
	}

	@Override
	public Classification getClassification(URL url) throws IOException {
		try  {
			final XMLReader xr = XMLReaderFactory.createXMLReader();
			/*
			 * SAX callback handler
			 */
			xr.setContentHandler(classificationParser);
			xr.setErrorHandler(classificationParser);
			xr.parse(url.getPath());
			
			if(!present(classificationParser.getList())) {
				if(!present(next)) {
					return null;
				} else {
					return next.getClassification(url);
				}
			}
			
			return new Classification(classificationParser.getName(), classificationParser.getList());
		} catch (SAXException e) {
			//unable to parse
			if(!present(next)) {
				return null;
			} else {
				return next.getClassification(url);
			}
		}
	}
}
