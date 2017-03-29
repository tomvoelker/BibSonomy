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

import java.util.List;
import java.util.Set;

import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.util.ValidationUtils;

/**
 * In cases where an author is required but none has been set, this extractor treats any mentioned person or organization it can find as the author.
 * 
 * @author Jens Illig
 */
public class EmergencyAuthorExtractor extends AbstractParticipantExtractor {

	@Override
	public void extractAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		final List<PersonName> authors = target.getAuthor();
		if (ValidationUtils.present(authors) || ValidationUtils.present(target.getEditor())) {
			return;
		}
		
		//in case of conference typed in 013H/0X as k we use 110, 111, 710 for organization
		boolean conference = false;
		if (src instanceof ExtendedMarcWithPicaRecord) {
			conference = checkForConference((ExtendedMarcWithPicaRecord) src);
		}
		
		// still no author -> try everything mentioned
		if (!extractAndAddAuthorPersons(authors, src, "700", (Set<String>) null)) {
			if (!conference) {
				if (!extractAndAddAuthorCorporations(authors, src, "710", (Set<String>) null)) {
					extractAndAddAuthorMeetings(authors, src, "711", (Set<String>) null);
				}
			}
		}
		
		target.setAuthor(authors);
	}

}
