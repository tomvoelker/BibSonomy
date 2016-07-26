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
package org.bibsonomy.marc;

import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;

import org.bibsonomy.util.ValidationUtils;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

/**
 * @author jensi
 */
public class ExtendedMarcRecord {

	private final Record record;

	public ExtendedMarcRecord(Record record) {
		this.record = record;
	}

	public String getFirstFieldValue(String fieldName, char subFieldChar) {
		List<DataField> dfs = getDataFields(fieldName);
		for (DataField df : dfs) {
			Subfield sf = df.getSubfield(subFieldChar);
			if (sf != null) {
				return sf.getData();
			}
		}
		return null;
	}
	
	public void appendFirstFieldValueWithDelmiterIfPresent(Appendable a, String fieldName, char subFieldChar, String delimiter) {
		String val =  getFirstFieldValue(fieldName, subFieldChar);
		if (ValidationUtils.present(val)) {
			try {
				a.append(delimiter);
				a.append(trimAndNormalize(val));
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<DataField> getDataFields(String fieldName) {
		if ((fieldName == null) || fieldName.startsWith("00")) {
			throw new IllegalArgumentException("Not a Datafield name: '" + fieldName + "'");
		}
		return (List<DataField>) record.getVariableFields(fieldName);
	}

	@SuppressWarnings("unchecked")
	public List<ControlField> getControlFields(String fieldName) {
		if ((fieldName == null) || !fieldName.startsWith("00")) {
			throw new IllegalArgumentException("Not a Controlfield name: '" + fieldName + "'");
		}
		return (List<ControlField>) record.getVariableFields(fieldName);
	}
	
	/**
	 * @return the record
	 */
	public Record getRecord() {
		return this.record;
	}
	
	public static String trimAndNormalize(String val) {
		if (val == null) {
			return null;
		}
		return Normalizer.normalize(val.trim(), Form.NFC);
	}
}
