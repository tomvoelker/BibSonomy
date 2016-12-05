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
 */
public class EmergencyRepairingExtractor implements AttributeExtractor {

	@Override
	public void extractAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		setDummyAuthorIfNeeded(target);
		setDummyYearIfNeeded(target);
	}

	private void setDummyYearIfNeeded(BibTex target) {
		if (target.getYear() == null) {
			target.setYear("noyear");
		}
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
