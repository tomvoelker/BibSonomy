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
