package org.bibsonomy.marc.extractors;

import java.text.Normalizer;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author nilsraabe
 */
public class MonthExtractor implements AttributeExtractor{

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		String month = null;
		 // TODO: ask Martina if this is correct
		if (!ValidationUtils.present(month) && (src instanceof ExtendedMarcWithPicaRecord)) {
			month = ((ExtendedMarcWithPicaRecord) src).getFirstPicaFieldValue("031A", "$c");
		}
		if (ValidationUtils.present(month)) {
			target.setMonth(Normalizer.normalize(month.trim(), Normalizer.Form.NFC));
		}
	}

}

