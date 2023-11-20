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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;

/**
 * @author jensi
 */
public class EditorExtractor extends AbstractParticipantExtractor {

	private static final Set<String> editorRelatorCodes = new HashSet<String>();
	static {
		editorRelatorCodes.add("edt");
	}

	@Override
	public void extractAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		ArrayList<PersonName> editors = new ArrayList<PersonName>();
		
		boolean edtFound = true;
		if (!extractAndAddAuthorPersons(editors, src, "700", editorRelatorCodes)) {
			if (!extractAndAddAuthorCorporations(editors, src, "710", editorRelatorCodes)) {
				if (!extractAndAddAuthorMeetings(editors, src, "711", editorRelatorCodes)) {
					edtFound = false;
				}
			}
		}
		if (edtFound) {
			target.setEditor(editors);
		}
	}

}
