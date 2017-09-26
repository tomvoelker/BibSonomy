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
import org.bibsonomy.model.util.MiscFieldConflictResolutionStrategy;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author Jens Illig
 */
public class HebisIdExtractor implements AttributeExtractor{

	@Override
	public void extractAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		String ppn = null;
		if (src instanceof ExtendedMarcWithPicaRecord) {
			final ExtendedMarcWithPicaRecord extendedMarcPicaRecord = (ExtendedMarcWithPicaRecord) src;
			ppn = extendedMarcPicaRecord.getFirstPicaFieldValue("003@", "$0", null);
			String s = extendedMarcPicaRecord.getFirstPicaFieldValue("002@", "$0");
			// preliminary solution for retro
			if ((s != null) && (s.indexOf("r") == 0)) {
				ppn = "r" + ppn;
			}
		}

		if (ValidationUtils.present(ppn)) {
			target.addMiscField("uniqueid", "HEB" + ppn.trim());
			target.syncMiscFields(MiscFieldConflictResolutionStrategy.MISC_FIELD_MAP_WINS);
		}
	}

}
