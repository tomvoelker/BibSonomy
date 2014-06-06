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
		//Property,default
		String[] parts = arg0.split(",", 2);
		String property = null;
		String defValue = null;
		
		if (parts != null && parts.length == 2) {
			property = parts[0];
			defValue = parts[1];
		} else {
			property = arg0;
		}
		//Get Spring-Managed Properties
		Properties properties = JabrefLayoutRenderer.getProperties();
		if (properties != null) {
			return properties.getProperty(property);
		}
		return defValue;
	}

}
