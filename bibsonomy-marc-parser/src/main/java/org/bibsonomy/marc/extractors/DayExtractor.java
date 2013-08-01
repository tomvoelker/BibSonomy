package org.bibsonomy.marc.extractors;

import java.text.Normalizer;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author nilsraabe
 * @version $Id$
 */
public class DayExtractor implements AttributeExtractor {

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		String day = null;
		 // TODO: ask Martina if this is correct
		if (!ValidationUtils.present(day) && (src instanceof ExtendedMarcWithPicaRecord)) {
			day = ((ExtendedMarcWithPicaRecord) src).getFirstPicaFieldValue("031A", "$b");
		}
		if (ValidationUtils.present(day)) {
			target.setDay(Normalizer.normalize(day.trim(), Normalizer.Form.NFC));
		}
	}

}
