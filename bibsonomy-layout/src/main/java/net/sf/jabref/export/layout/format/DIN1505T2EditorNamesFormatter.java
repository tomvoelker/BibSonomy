package net.sf.jabref.export.layout.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jabref.export.layout.LayoutFormatter;
/**
 * This formatter prints "(Bearb.)" behind each name. Input must already be
 * formatted by the DIN1505T2PersonNamesFormatter.
 * 
 * @author hagen
 *
 */
public class DIN1505T2EditorNamesFormatter implements LayoutFormatter {
	
	public static final Pattern EDITOR_NAMES_PATTERN = Pattern.compile("((\\A|\\s)<span style=\"font-variant: small-caps\">.+?</span>.*?)(\\s;|\\z)"); 
	
	@Override
	public String format(String arg0) {
		Matcher m = EDITOR_NAMES_PATTERN.matcher(arg0);
		while (m.find()) {
			arg0 = arg0.replace(m.group(), m.group(1) + " (Bearb.)" + m.group(3));
		}
		return arg0;
	}

}
