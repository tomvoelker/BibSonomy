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
package org.bibsonomy.marc;

import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.bibsonomy.scraper.converter.picatobibtex.PicaUtils;
import org.marc4j.marc.Record;

/**
 * @author jensi
 */
public class ExtendedMarcWithPicaRecord extends ExtendedMarcRecord {

	private final PicaRecord pica;

	public ExtendedMarcWithPicaRecord(Record marc, PicaRecord pica) {
		super(marc);
		this.pica = pica;
	}

	public String getFirstPicaFieldValue(final String category, final String subCategory) {
		return PicaUtils.getSubCategory(pica, category, subCategory, null);
	}
	
	public String getFirstPicaFieldValue(final String category, final String subCategory, final String defaultValue) {
		return PicaUtils.getSubCategory(pica, category, subCategory, defaultValue);
	}
}
