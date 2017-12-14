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

/**
 * @author Lukas
 */
public class SeriesExtractor implements AttributeExtractor {

	@Override
	public void extractAndSetAttribute(final BibTex target, final ExtendedMarcRecord src) {
		/*
		 * first try to get series value from marc record
		 */
		String series = src.getFirstFieldValue("490", 'a');
		
		if (series != null) {
			target.setSeries(Normalizer.normalize(series, Normalizer.Form.NFC));
			return;
		}
		
		/*
		 * get the series out of the pica data
		 */
		if (src instanceof ExtendedMarcWithPicaRecord) {
			final ExtendedMarcWithPicaRecord picaSrc = (ExtendedMarcWithPicaRecord) src;
			
			series = picaSrc.getFirstPicaFieldValue("036E", "$a");
			
			if (series == null) {
				series = picaSrc.getFirstPicaFieldValue("036G", "$a");
			}
			if (series != null) {
				target.setSeries(Normalizer.normalize(series, Normalizer.Form.NFC));
			}
		}
		
	}

}
