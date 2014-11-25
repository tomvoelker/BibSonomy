/**
 * BibSonomy-MARC-Parser - Marc Parser for BibSonomy
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.impl.DataFieldImpl;
import org.marc4j.marc.impl.LeaderImpl;
import org.marc4j.marc.impl.RecordImpl;
import org.marc4j.marc.impl.SubfieldImpl;

/**
 * @author jensi
 */
public abstract class AbstractExtractorTest {
	public static class ExtendedMarcWithPicaRecordMock extends ExtendedMarcWithPicaRecord {
		
		public ExtendedMarcWithPicaRecordMock() {
			super(createRecord(), null);
		}

		public static RecordImpl createRecord() {
			RecordImpl rVal = new RecordImpl();
			Leader leader = new LeaderImpl();
			rVal.setLeader(leader);
			leader.setImplDefined1(new char[] {'a', 'b'}); // nonsense, but saves us from nullpointers
			return rVal;
		}

		private final Map<String, String> fields = new HashMap<String, String>();
		private final Map<String, String> picaFields = new HashMap<String, String>();
		
		
		@Override
		public String getFirstFieldValue(String fieldName, char subFieldChar) {
			return fields.get(fieldName + "~" + subFieldChar);
		}
		
		public ExtendedMarcWithPicaRecordMock withMarcField(String field, char subField, String value) {
			fields.put(field + "~" +subField, value);
			return this;
		}
		
		@Override
		public String getFirstPicaFieldValue(String category, String subCategory) {
			return picaFields.get(category+"~"+subCategory);
		}
		
		@Override
		public String getFirstPicaFieldValue(String category,
				String subCategory, String defaultValue) {
			if(picaFields.keySet().contains(category + "~" + subCategory)) {
				return picaFields.get(category + "~" + subCategory);
			} else {
				return defaultValue;
			}
		}
		
		public ExtendedMarcWithPicaRecordMock withPicaField(String fieldName, String subFieldName, String value) {
			picaFields.put(fieldName + "~" + subFieldName, value);
			return this;
		}
		
		@Override
		public List<DataField> getDataFields(String fieldName) {
			ArrayList<DataField> datafields = new ArrayList<DataField>();
			DataField df = new DataFieldImpl();
			df.setTag(fieldName);
			for(String key : fields.keySet()) {
				if(key.contains(fieldName)) {
					Subfield subfield = new SubfieldImpl();
					subfield.setCode(key.split("~")[1].charAt(0));
					subfield.setData(fields.get(key));
					df.addSubfield(subfield);
				}
			}
			datafields.add(df);
			
			return datafields;
		}
	}

	protected ExtendedMarcWithPicaRecordMock createExtendedMarcWithPicaRecord() {
		return new ExtendedMarcWithPicaRecordMock();
	}
}
