package net.sf.jabref.export.layout.format;

import net.sf.jabref.export.layout.LayoutFormatter;

public class DIN1505T2AndsReplacer implements LayoutFormatter {

	@Override
	public String format(String arg0) {
		return arg0.replaceAll("\\sand\\s", " ; ");
	}

}
