package net.sf.jabref.export.layout.format;

import java.util.Properties;

import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;

import net.sf.jabref.export.layout.LayoutFormatter;

/**
 * This Formatter inserts values of Properties managed by Spring
 *
 * @author MarcelM
 */
public class InsertPropertyFormatter implements LayoutFormatter {

	/* (non-Javadoc)
	 * @see net.sf.jabref.export.layout.LayoutFormatter#format(java.lang.String)
	 */
	@Override
	public String format(String arg0) {
		//Get Spring-Managed Properties
		Properties properties = JabrefLayoutRenderer.getProperties();
		//FIXME - Error Handling eg. PropertyNotFound?
		String message = properties.getProperty(arg0);
		return message;
	}

}
