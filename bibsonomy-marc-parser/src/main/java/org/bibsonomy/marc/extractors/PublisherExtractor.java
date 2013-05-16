package org.bibsonomy.marc.extractors;

import java.text.Normalizer;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.model.BibTex;

/**
 * @author Lukas
 * @version $Id$
 */
public class PublisherExtractor implements AttributeExtractor {
	
	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		
		final String publisher = src.getFirstFieldValue("260", 'b');
		
		if (publisher != null) {
			target.setPublisher(Normalizer.normalize(publisher, Normalizer.Form.NFC));
			// warum war das so? da fehlte hinten en Zeichen
			// target.setPublisher(publisher.substring(0, publisher.length() - 1));
		}
		
	}

}
