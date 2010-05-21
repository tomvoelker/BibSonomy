package org.bibsonomy.community.importer.parser;


public class DoubleDataInputParser implements DataInputParser<Double> {

	public Double parseString(String str) {
		Double input = Double.parseDouble(str.trim());
		return input;
	}

}
