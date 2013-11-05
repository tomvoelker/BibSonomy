package net.sf.jabref.export.layout.format;

import org.bibsonomy.model.util.BibTexUtils;

import net.sf.jabref.export.layout.LayoutFormatter;

public class CleanLatexMarkup implements LayoutFormatter{

	@Override
	public String format(String arg0) {
		return BibTexUtils.cleanBibTex(arg0);
	}

}
