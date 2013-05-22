package org.bibsonomy.marc.extractors;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.model.BibTex;

/**
 * set note of bibtex if entrytype is phdthesis
 * 
 * @author Lukas
 * @version $Id$
 */
public class NoteExtractor implements AttributeExtractor {

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		if (target.getEntrytype().equals("phdthesis") && src instanceof ExtendedMarcWithPicaRecord) {
			target.setNote(((ExtendedMarcWithPicaRecord) src).getFirstPicaFieldValue("037C", "$a"));
		}
	}

}
