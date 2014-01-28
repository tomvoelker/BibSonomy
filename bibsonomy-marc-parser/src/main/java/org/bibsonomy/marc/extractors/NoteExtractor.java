package org.bibsonomy.marc.extractors;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.ValidationUtils;

/**
 * set note of bibtex if entrytype is phdthesis
 * 
 * @author Lukas
 */
public class NoteExtractor implements AttributeExtractor {

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		if (target.getEntrytype().equals("phdthesis") == false) {
			return;
		}
		String marcValue = src.getFirstFieldValue("502", 'a');
		if (ValidationUtils.present(marcValue)) {
			target.setNote(marcValue.trim());
		} else if (src instanceof ExtendedMarcWithPicaRecord) {
			StringBuilder sb = new StringBuilder();
			sb.append(((ExtendedMarcWithPicaRecord) src).getFirstPicaFieldValue("037C", "$a", ""));
			StringUtils.trimStringBuffer(sb);
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(((ExtendedMarcWithPicaRecord) src).getFirstPicaFieldValue("037C", "$b", ""));
			StringUtils.replaceFirstOccurrence(sb, "@", "");
			StringUtils.trimStringBuffer(sb);
			
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(((ExtendedMarcWithPicaRecord) src).getFirstPicaFieldValue("037C", "$c", ""));
			StringUtils.replaceFirstOccurrence(sb, "@", "");
			StringUtils.trimStringBuffer(sb);
			
			target.setNote(sb.toString());
		}
	}

}
