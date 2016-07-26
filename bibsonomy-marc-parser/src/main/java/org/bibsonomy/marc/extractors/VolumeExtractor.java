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
 * @author Lukas
 */
public class VolumeExtractor implements AttributeExtractor {

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		
		/*
		 * first try to get volume from the marc record
		 */
		String volume = src.getFirstFieldValue("490", 'v');
		
		if(volume != null) {
			target.setVolume(Normalizer.normalize(volume, Normalizer.Form.NFC));
			return;
		}
		
		/*
		 * marc record extraction failed -> check the pica record
		 */
		if(src instanceof ExtendedMarcWithPicaRecord) {
			
			ExtendedMarcWithPicaRecord record = (ExtendedMarcWithPicaRecord) src;
			
			//try to get volume on 036E first
			volume = record.getFirstPicaFieldValue("036E", "$l");
			
			if(ValidationUtils.present(volume)) {
				target.setVolume(Normalizer.normalize(volume.trim(), Normalizer.Form.NFC));
				return;
			}
			
			//try to get volume on 036E if 036E was not set
			volume = record.getFirstPicaFieldValue("036F", "$l");
			if (ValidationUtils.present(volume)) {
				target.setVolume(Normalizer.normalize(volume.trim(), Normalizer.Form.NFC));
				return;
			}
			
			// some newspapers have a volume set in 031A $d  // TODO: ask Martina if this is correct
			volume = record.getFirstPicaFieldValue("031A", "$d");
			if (ValidationUtils.present(volume)) {
				target.setVolume((Normalizer.normalize(volume.trim(), Normalizer.Form.NFC)));
			}
		}
		
	}

}
