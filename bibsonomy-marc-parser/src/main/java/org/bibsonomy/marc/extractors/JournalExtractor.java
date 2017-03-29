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

import java.text.Normalizer;
import java.text.Normalizer.Form;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.util.ValidationUtils;

/**
 * extracts journal from a PICA Record.
 * 
 * Martina Sauer (Email 2013-08-09): Der Unterschied zwischen Volume/Band und
 * Number/Heft ist folgender: bei Zeitschriften hast du in der Regel immer eine
 * Band- bzw. Jahrgangszählung: 1. Jg. 1950, 2. Jg. 1951, 3. Jg. 1952 usw. Und
 * dann gibt es aber immer noch die Hefte/Nummern, die wöchentlich,
 * zweiwöchentlich, monatlich oder so erscheinen und die auch gezählt werden
 * 
 * 
 * @author Lukas
 */
public class JournalExtractor implements AttributeExtractor {
	final String expr = "--.+--[:]?";

	private ExtendedMarcWithPicaRecord record = null;

	@Override
	public void extractAndSetAttribute(BibTex target, ExtendedMarcRecord src) throws IllegalArgumentException {
		if (src instanceof ExtendedMarcWithPicaRecord) {
			record = (ExtendedMarcWithPicaRecord) src;
			final String nullNull2At = record.getFirstPicaFieldValue("002@", "$0", "");
			if (ValidationUtils.present(nullNull2At) && (nullNull2At.indexOf("o") == -1)) {
				return;
			}
			final boolean isAufsatz = (ValidationUtils.present(nullNull2At) && (nullNull2At.length() > 1) && (nullNull2At.charAt(1) == 'o'));
			String next = null;
			StringBuilder sb = new StringBuilder();
			if (ValidationUtils.present((next = getName(record)))) {
				sb.append(next);
			}
			if (ValidationUtils.present((next = getVolume(record)))) {
				sb.append(' ').append(next);
			}
			/*
			 * Keine Jahreszahlen bei Aufsätzen (Mail von Martina Sauer 2013-08-09:
			 * Zum 1. Fall: das ist ein Aufsatz und da solltet ihr tatsächlich
			 * auf die Jahresangabe aus 011@ verzichten und ausschließlich die
			 * Angaben aus 031A nehmen. Aufsätze erkennt man daran, dass der
			 * zweite Buchstabe in 002@ ein kleines o ist (siehe auch:
			 * http://www.hebis.de/de/1publikationen/arbeitsmaterialien/hebis-handbuch/kategorien/kategorien_detail.php?we_editObject_ID=2253)
			 * Was genau sich jeweils hinter den Subfeldern in 031A verbirgt, könnt
			 * ihr hier
			 * http://www.hebis.de/de/suchfelder/handbuch_suche.php?we_objectID=2297&pid=2566
			 * sehen, also $b für den Tag und $c für den Monat ist genau richtig.
			 * Und $e für das Heft bzw. die Nr. ist auch richtig.
			 */
			if (!isAufsatz && ValidationUtils.present((next = getYear(record)))) {
				sb.append(" (").append(next).append(')');
			}
			if (sb.length() > 0) {
				target.setJournal(Normalizer.normalize(sb.toString(), Normalizer.Form.NFC));
			}
		} else {
			throw new IllegalArgumentException("expects ExtendedMarcWithPicaRecord");
		}
	}

	private String getName(ExtendedMarcWithPicaRecord r) {
		try {
			String name = r.getFirstPicaFieldValue("039B", "$8");
			if (ValidationUtils.present(name)) {
				name = ExtendedMarcRecord.trimAndNormalize(name.replaceAll(expr, ""));
			} else if (!ValidationUtils.present((name = r.getFirstPicaFieldValue("039B", "$c")))) {
				return null;
			}
			return name;
		} catch (RuntimeException e) {
			// field not present
		}
		return null;
	}

	private String getVolume(ExtendedMarcWithPicaRecord r) {
		String volume = ExtendedMarcRecord.trimAndNormalize(r.getFirstPicaFieldValue("031A", "$d", ""));
		if (volume.length() > 0) {
			return volume;
		}
		return null;
	}

	private String getYear(ExtendedMarcWithPicaRecord r) {
		String year = ExtendedMarcRecord.trimAndNormalize(r.getFirstPicaFieldValue("031A", "$j", ""));
		if (year.length() > 0)
			return year;
		return null;
	}

}
