package net.sf.jabref.export.layout.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jabref.export.layout.LayoutFormatter;

public class MittelalterEditionFormatter implements LayoutFormatter {
	
	public static final Pattern EDITION_NUMBER_PATTERN = Pattern.compile(".*(\\d).*"); 
	
	@Override
	public String format(String arg0) {
		Matcher m = EDITION_NUMBER_PATTERN.matcher(arg0);
		while (m.find()) {
			int edition = Integer.parseInt(m.group(1));
			if(edition > 1) {
				arg0 = arg0.replace(m.group(), "<sup>" + edition + "</sup>");
			}
		}
		return arg0;
	}
}
