package net.sf.jabref.export.layout.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jabref.export.layout.LayoutFormatter;

public class DIN1505T2PersonNamesFormatter implements LayoutFormatter {
	
	public static final Pattern PERSON_NAMES_PATTERN = Pattern.compile("(\\A|;\\s)(.+?)(,|\\z)");

	@Override
	public String format(String arg0) {
		arg0 = arg0.trim();
		Matcher m = PERSON_NAMES_PATTERN.matcher(arg0);
		while (m.find()) {
			arg0 = arg0.replaceFirst(m.group(), m.group(1) + "<span style=\"font-variant: small-caps\">" + m.group(2) + "</span>" + m.group(3));
		}
		return arg0;
	}

}
