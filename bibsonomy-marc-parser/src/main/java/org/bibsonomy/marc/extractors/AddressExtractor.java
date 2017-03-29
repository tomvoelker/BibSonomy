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

import java.text.Normalizer;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author nilsraabe
 */
public class AddressExtractor implements AttributeExtractor {

	@Override
	public void extractAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
	
		String address = null;
		
		if(src instanceof ExtendedMarcWithPicaRecord) {
			ExtendedMarcWithPicaRecord picaSrc = (ExtendedMarcWithPicaRecord) src;
			
			address = src.getFirstFieldValue("260", 'a');
			
			if (!ValidationUtils.present(address)) {
				address = picaSrc.getFirstPicaFieldValue("033A","$p");
			}
		} else {
			address = src.getFirstFieldValue("260", 'a');
		}

		if (ValidationUtils.present(address)) {
			target.setAddress(Normalizer.normalize(address, Normalizer.Form.NFC));
		}  
	}
}