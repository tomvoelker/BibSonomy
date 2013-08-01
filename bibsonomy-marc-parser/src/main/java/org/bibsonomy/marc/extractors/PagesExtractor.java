/**
 *  BibSonomy-MARC-Parser - Marc Parser for Bibsonomy
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
 * @version $Id$
 */
public class PagesExtractor implements AttributeExtractor{

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		String pages = null;
		if (src instanceof ExtendedMarcWithPicaRecord) {
			pages = ((ExtendedMarcWithPicaRecord) src).getFirstPicaFieldValue("031A", "$h", null);
		}
//		This is the number of pages, but for citations we need the pages of e.g. an article in some journal
//		if (!ValidationUtils.present(pages)) {
//			pages = src.getFirstFieldValue("300", 'a');
//		}
		if (ValidationUtils.present(pages)) {
			target.setPages(Normalizer.normalize(pages.trim(), Normalizer.Form.NFC));
		}
		// + 31A $h (pages) (bei pages extractor)
	}

}
