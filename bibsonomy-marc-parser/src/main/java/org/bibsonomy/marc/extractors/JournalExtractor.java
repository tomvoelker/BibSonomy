/**
 *
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

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.util.ValidationUtils;

/**
 * extracts journal from a PICA Record
 * 
 * @author Lukas
 * @version $Id$
 */
public class JournalExtractor implements AttributeExtractor {
	final String expr1 = "/--.+--:/";
	final String expr2 = "/--.+--/";
	private ExtendedMarcWithPicaRecord record = null;
	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		
		record = (ExtendedMarcWithPicaRecord)src;
		if(record.getFirstPicaFieldValue("002@", "0").indexOf("o")==-1) return;
		String next = null;
		if(ValidationUtils.present((next=getName(record))))
			target.setJournal(next);
		if(ValidationUtils.present((next=getYear(record))))
			target.setYear(next);
		if(ValidationUtils.present((next=getVolume(record))))
			target.setVolume(next);
	}
	
	private String getName(ExtendedMarcWithPicaRecord r) {
		try {
			String name = r.getFirstPicaFieldValue("039B", "$8");
			if(ValidationUtils.present(name)) {
				name = (name.replaceAll(expr1, "")).replaceAll(expr2, "");
			} else if(!ValidationUtils.present((name=r.getFirstPicaFieldValue("039B", "$c")))) {
				return null;
    		}
			return name;
		} catch (RuntimeException e) {
			//field not present
		}
		return null;
    }
    
	private String getVolume(ExtendedMarcWithPicaRecord r) {
    	String volume = r.getFirstPicaFieldValue("031A", "$d");
    	if(volume.length() > 0) return volume;
    	return null;
    }
    
	private String getYear(ExtendedMarcWithPicaRecord r) {
    	String year = r.getFirstPicaFieldValue("031A", "$j");
    	if(year.length() > 0) return year;
    	return null;
    }

}

