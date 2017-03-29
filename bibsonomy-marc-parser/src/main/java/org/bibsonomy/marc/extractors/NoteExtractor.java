/**
 * BibSonomy-MARC-Parser - Marc Parser for BibSonomy
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	public void extractAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
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
