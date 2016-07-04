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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.ValidationUtils;

/**
 * extracts a BibTex author attribute out of a MarcRecord
 * 
 * @author lha
 */
public class AuthorExtractor extends AbstractParticipantExtractor {

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		final List<PersonName> authors = new ArrayList<PersonName>();
		
		//in case of conference typed in 013H/0X as k we use 110, 111, 710 for organization
		boolean conference = false;
		if (src instanceof ExtendedMarcWithPicaRecord) {
			conference = checkForConference((ExtendedMarcWithPicaRecord) src);
		}
		
		//get fields containing firstname information
		boolean mainAuthorFound = true;
		if (!extractAndAddAuthorPersons(authors, src, "100", (Set<String>) null)) {
			if (!conference) {
				if (!extractAndAddAuthorCorporations(authors, src, "110",
						(Set<String>) null)) {
					if (!extractAndAddAuthorMeetings(authors, src, "111",
							(Set<String>) null)) {
					mainAuthorFound = false;
				}
			}
		}
		}
		if (!extractAndAddAuthorPersons(authors, src, "700", authorRelatorCodes)) {
			if (!conference) {
			if (!mainAuthorFound && !extractAndAddAuthorCorporations(authors, src, "710", authorRelatorCodes)) {
				if (!mainAuthorFound && !extractAndAddAuthorMeetings(authors, src, "711", authorRelatorCodes)) {
					mainAuthorFound = false;
				}
			}
		}
		}
		
		if ((authors.size() == 0) && (src instanceof ExtendedMarcWithPicaRecord)){
			String familyName =((ExtendedMarcWithPicaRecord) src).getFirstPicaFieldValue("028A", "$a", "");
			String firstName =((ExtendedMarcWithPicaRecord) src).getFirstPicaFieldValue("028A", "$d", "");
			if (ValidationUtils.present(familyName) || ValidationUtils.present(firstName)) {
				authors.add(new PersonName(BibTexUtils.escapeBibtexMarkup(firstName.trim(), true), BibTexUtils.escapeBibtexMarkup(familyName.trim(), true)));
			}
		}
		
		target.setAuthor(authors);
		
	}
	
	
	
	
	private static final List<Set<String>> authorRelatorCodes = new ArrayList<Set<String>>();
	static {
		// freely chosen from http://www.loc.gov/marc/relators/relaterm.html
		Set<String> creatingAuthors = new HashSet<String>();
		Set<String> performingAuthors = new HashSet<String>();
		Set<String> ratherUnspecificAuthors = new HashSet<String>();
		creatingAuthors.addAll(Arrays.asList("aut", "cre", "cmp", "drt"));
		performingAuthors.addAll(Arrays.asList("cnd", "prf", "cmp", "sng", "spk", "stl"));
		ratherUnspecificAuthors.addAll(Arrays.asList("ctb"));
		authorRelatorCodes.add(creatingAuthors);
		authorRelatorCodes.add(performingAuthors);
		authorRelatorCodes.add(ratherUnspecificAuthors);
	}





}
