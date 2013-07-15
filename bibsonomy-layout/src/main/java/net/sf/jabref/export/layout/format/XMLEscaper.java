package net.sf.jabref.export.layout.format;

import org.apache.commons.lang.StringEscapeUtils;

import net.sf.jabref.export.layout.LayoutFormatter;

public class XMLEscaper implements LayoutFormatter{

	@Override
	public String format(String arg0) {
		return StringEscapeUtils.escapeXml(arg0);
	}

}
