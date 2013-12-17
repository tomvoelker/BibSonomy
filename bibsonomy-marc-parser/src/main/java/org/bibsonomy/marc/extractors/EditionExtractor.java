package org.bibsonomy.marc.extractors;

import java.text.Normalizer;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.model.BibTex;

/**
 * @author Lukas
 */
public class EditionExtractor implements AttributeExtractor {

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		
		final String edition = src.getFirstFieldValue("250", 'a');
		if (edition != null) {
			target.setEdition(Normalizer.normalize(edition, Normalizer.Form.NFC));
		}

	}

}
