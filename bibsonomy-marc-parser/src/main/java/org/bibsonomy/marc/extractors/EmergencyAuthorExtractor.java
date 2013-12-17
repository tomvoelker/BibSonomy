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
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
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
