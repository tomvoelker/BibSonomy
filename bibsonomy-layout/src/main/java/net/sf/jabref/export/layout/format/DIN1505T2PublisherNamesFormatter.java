package net.sf.jabref.export.layout.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jabref.export.layout.LayoutFormatter;

public class DIN1505T2PublisherNamesFormatter implements LayoutFormatter {
	
	public static void main(String[] args) {
		System.out.println(new DIN1505T2PublisherNamesFormatter().format("Amnesty International ; Barendregt, Wolmet ; Bekker, Mathilde M. ; Speerstra, Mathilde ; Mulder, General ; The inc ; FRG"));
	}

	@Override
	public String format(String arg0) {
		//cite up to 3 publisher
		{
			arg0 = arg0.trim();
			Matcher m = Pattern.compile("(\\A|;\\s)(\\w+)(,(\\s\\w[\\w\\.]*+)++)(\\s|\\z)").matcher(arg0);
			int count = 0;
			while (m.find()) {
				System.out.println(m.group());
				if (++count <= 3) {
					arg0 = arg0.replaceFirst(m.group(), m.group(1) + "<span style=\"font-variant: small-caps\">" + m.group(2) + "</span>" + m.group(3) + m.group(5));
				} else {
					arg0 = arg0.replaceFirst(m.group(), "");
				}
			}
		}
		//cite up to 2 publisher, that are org's etc...
		{
			Matcher m = Pattern.compile("(\\A|;\\s)([\\w\\s]+)(\\s|\\z)").matcher(arg0);
			int count = 0;
			while (m.find()) {
				if (++count <= 2) {
					arg0 = arg0.replaceFirst(m.group(), m.group(1) + "<span style=\"font-variant: small-caps\">" + m.group(2) + "</span>" + m.group(3));
				} else {
					arg0 = arg0.replaceFirst(m.group(), "");
				}
			}
		}
		return arg0;
	}

}
