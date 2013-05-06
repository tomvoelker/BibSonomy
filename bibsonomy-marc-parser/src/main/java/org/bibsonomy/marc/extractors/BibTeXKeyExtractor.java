package org.bibsonomy.marc.extractors;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.util.BibTexUtils;

/**
 * @author Lukas
 * @version $Id$
 */
public class BibTeXKeyExtractor implements AttributeExtractor {

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		
		final String bibtexkey = BibTexUtils.generateBibtexKey(target);
		
		target.setBibtexKey(bibtexkey);
		
	}

}
