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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author nilsraabe
  */
public class YearExtractor implements AttributeExtractor{
	
	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		
		Pattern regex = Pattern.compile("^*\\d{4}");
		
		String year = null; 
		
		if(src instanceof ExtendedMarcWithPicaRecord) {
			ExtendedMarcWithPicaRecord picaSrc = (ExtendedMarcWithPicaRecord) src;
			
			year = src.getFirstFieldValue("260", 'c');
			
			if (!ValidationUtils.present(year)) {
				year = picaSrc.getFirstPicaFieldValue("011@","$a");
			}
		} else {
			year = src.getFirstFieldValue("260", 'c');
		}

		if(ValidationUtils.present(year)) {
			Matcher m = regex.matcher(year);
			
			if(m.find()) {
				target.setYear(m.group());
			}
		}
	}
}
