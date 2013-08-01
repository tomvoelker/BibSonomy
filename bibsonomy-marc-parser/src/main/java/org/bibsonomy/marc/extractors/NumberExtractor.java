package org.bibsonomy.marc.extractors;

import java.text.Normalizer;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class NumberExtractor implements AttributeExtractor {

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		String nr = null;
		 // TODO: ask Martina if this is correct
		if (!ValidationUtils.present(nr) && (src instanceof ExtendedMarcWithPicaRecord)) {
			nr = ((ExtendedMarcWithPicaRecord) src).getFirstPicaFieldValue("031A", "$e");
		}
		if (ValidationUtils.present(nr)) {
			target.setNumber(Normalizer.normalize(nr.trim(), Normalizer.Form.NFC));
		}
	}

}
