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
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
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
