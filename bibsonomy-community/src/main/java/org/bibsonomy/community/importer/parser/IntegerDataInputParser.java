package org.bibsonomy.community.importer.parser;


public class IntegerDataInputParser implements DataInputParser<Integer> {

	public Integer parseString(String str) {
		Integer input = Integer.parseInt(str.trim());
		return input;
	}

}
