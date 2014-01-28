package org.bibsonomy.marc.extractors;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author jensi
 */
public class AbstractExtractor implements AttributeExtractor {

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		String marcValue = src.getFirstFieldValue("520", 'a');
		if (ValidationUtils.present(marcValue)) {
			target.setAbstract(marcValue.trim());
		} else if (src instanceof ExtendedMarcWithPicaRecord) {
			StringBuilder sb = new StringBuilder();
			sb.append(((ExtendedMarcWithPicaRecord) src).getFirstPicaFieldValue("047I", "$a", ""));
			StringUtils.trimStringBuffer(sb);
			target.setNote(sb.toString());
		}
	}

}
