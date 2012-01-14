package net.sf.jabref.export.layout.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jabref.export.layout.LayoutFormatter;

public class PrintIfxoxo implements LayoutFormatter {

	@Override
	public String format(String arg0) {
		Matcher m = Pattern.compile("\\Axo++(.)*\\z").matcher(arg0);
		if (m.find()) return m.group(1);
		return "";
	}

}
