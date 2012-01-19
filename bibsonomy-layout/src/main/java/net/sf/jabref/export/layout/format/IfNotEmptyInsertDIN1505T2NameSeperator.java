package net.sf.jabref.export.layout.format;

import static org.bibsonomy.util.ValidationUtils.present;

import net.sf.jabref.export.layout.LayoutFormatter;

public class IfNotEmptyInsertDIN1505T2NameSeperator implements LayoutFormatter {

	@Override
	public String format(String arg0) {
		if (present(arg0)) return " ; ";
		return "";
	}

}
