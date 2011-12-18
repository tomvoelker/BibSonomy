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
	
	public static void main(String[] args) {
		System.out.println(new DIN1505T2EditorNamesFormatter().format(new DIN1505T2PersonNamesFormatter().format("Barendregt, Wolmet ; Bekker, Mathilde M. ; Speerstra, Mathilde")));
	}

	@Override
	public String format(String arg0) {
		Matcher m = Pattern.compile("((\\A|\\s)<span style=\"font-variant: small-caps\">[\\w-]+</span>.*?)(\\s;|\\z)").matcher(arg0);
		while (m.find()) {
			System.out.println(m.group(1));
			arg0 = arg0.replace(m.group(), m.group(1) + " (Bearb.)" + m.group(3));
		}
		return arg0;
	}

}
