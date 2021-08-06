/**
 * BibSonomy-MARC-Parser - Marc Parser for BibSonomy
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
 * @author jensi
 */
public class AbstractExtractor implements AttributeExtractor {

	@Override
	public void extractAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
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
