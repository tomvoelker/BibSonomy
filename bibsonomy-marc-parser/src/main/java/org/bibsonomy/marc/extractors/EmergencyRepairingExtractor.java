package org.bibsonomy.marc.extractors;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.util.ValidationUtils;

/**
 * We need an author (or we will get NPEs by various exporters such as endnote), So this class generates a dummy if no author or editor is present
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class EmergencyRepairingExtractor implements AttributeExtractor {

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		setDummyAuthorIfNeeded(target);
	}

	public void setDummyAuthorIfNeeded(BibTex target) {
		if (ValidationUtils.present(target.getEditor())) {
			return;
		}
		List<PersonName> authors = target.getAuthor();
		if (ValidationUtils.present(authors)) {
			return;
		}
		
		
		if (requiresOnlyEditor(target)) {
			setDummyEditor(target);
		} else {
			setDummyAuthor(target);
		}
	}

	private void setDummyAuthor(BibTex target) {
		List<PersonName> authors = new ArrayList<PersonName>();
		authors.add(new PersonName("noauthor", target.getMiscField("uniqueid")));
		target.setAuthor(authors);
	}

	private void setDummyEditor(BibTex target) {
		List<PersonName> authors = new ArrayList<PersonName>();
		authors.add(new PersonName("noeditor", target.getMiscField("uniqueid")));
		target.setAuthor(authors);
	}

	private boolean requiresOnlyEditor(BibTex target) {
		return "proceedings".equals(target.getEntrytype());
	}

}
