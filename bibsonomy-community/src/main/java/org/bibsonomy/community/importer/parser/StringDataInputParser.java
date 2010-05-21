package org.bibsonomy.community.importer.parser;


public class StringDataInputParser implements DataInputParser<String> {

	public String parseString(String str) {
		return str.trim();
	}

}
