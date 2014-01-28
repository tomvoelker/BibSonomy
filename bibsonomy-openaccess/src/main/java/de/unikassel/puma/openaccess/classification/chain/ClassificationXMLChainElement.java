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
