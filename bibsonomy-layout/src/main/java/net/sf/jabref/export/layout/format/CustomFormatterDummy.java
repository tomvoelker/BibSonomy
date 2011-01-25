package net.sf.jabref.export.layout.format;

import net.sf.jabref.export.layout.LayoutFormatter;

/**
 * Dummy class for a custom formatter to be used within 
 * jabref layouts. This class is intented as an example
 * when writing custom formatters in the future.
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 */
public class CustomFormatterDummy implements LayoutFormatter {

    @Override
    public String format(String arg0) {	
	return arg0;
    }
}
