package org.bibsonomy.community.importer.parser;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;


public class IntegerDataInputParser implements DataInputParser<Integer> {

	public Integer parseString(String str) {
	    NumberFormat defForm = DecimalFormat.getInstance();
	    Number d = null;
	    try {
	    	d = defForm.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Integer input = Integer.parseInt(str.trim());
		return ((Long) d).intValue();
	}

}
