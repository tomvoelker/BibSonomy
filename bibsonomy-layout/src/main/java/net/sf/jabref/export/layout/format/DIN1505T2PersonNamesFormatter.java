package net.sf.jabref.export.layout.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jabref.export.layout.LayoutFormatter;

public class DIN1505T2PersonNamesFormatter implements LayoutFormatter {
	
	public static void main(String[] args) {
		System.out.println(new DIN1505T2PersonNamesFormatter().format("Barendregt, Wolmet ; Bekker, Mathilde M. ; Speerstra, Mathilde"));
	}

	@Override
	public String format(String arg0) {
		arg0 = arg0.trim();
		Matcher m = Pattern.compile("(\\A|;\\s)(\\w+)(,|\\z)").matcher(arg0);
		while (m.find()) {
			arg0 = arg0.replaceFirst(m.group(), m.group(1) + "<span style=\"font-variant: small-caps\">" + m.group(2) + "</span>" + m.group(3));
		}
		return arg0;
	}

}
