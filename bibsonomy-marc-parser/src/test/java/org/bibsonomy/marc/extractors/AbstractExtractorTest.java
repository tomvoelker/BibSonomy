package org.bibsonomy.marc.extractors;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.marc.ExtendedMarcRecord;

/**
 * @author jensi
 * @version $Id$
 */
public abstract class AbstractExtractorTest {
	public class ExtendedMarcRecordMock extends ExtendedMarcRecord {
		private final Map<String, String> fields = new HashMap<String, String>();
		
		public ExtendedMarcRecordMock() {
			super(null);
		}
		
		@Override
		public String getFirstFieldValue(String fieldName, char subFieldChar) {
			return fields.get(fieldName + "~" + subFieldChar);
		}
		
		public ExtendedMarcRecordMock withMarcField(String field, char subField, String value) {
			fields.put(field + "~" +subField, value);
			return this;
		}
	}

	protected ExtendedMarcRecordMock createExtendedMarcRecord() {
		return new ExtendedMarcRecordMock();
	}
}
