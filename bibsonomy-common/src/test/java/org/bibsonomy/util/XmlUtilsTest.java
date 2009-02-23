package org.bibsonomy.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests XMLUtils
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class XmlUtilsTest {

    final String escapeString = "\u0000\u0001\u0002\u0003\u0004\u0005" +
    "\u0006\u0007\u0008\u000B\u000C\u000E\u000F\u0010\u0011\u0012" +
    "\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C" +
    "\u001D\u001E\u001F\uFFFE\uFFFF";	
	
    @Test
	public void removeXmlControlCharacters() {
    	
		// all defined control chars
		String s = "\u0000\u0001\u0002\u0003\u0004\u0005" +
	    "\u0006\u0007\u0008\u000B\u000C\u000E\u000F\u0010\u0011\u0012" +
	    "\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C" +
	    "\u001D\u001E\u001F\uFFFE\uFFFF";		
		String cleaned = XmlUtils.removeXmlControlCharacters(s);
		System.out.println(cleaned);
		assertEquals("", cleaned);    	
    	
    	// text with control char
		s = "This is a text with a \u0002 control character";
		cleaned = XmlUtils.removeXmlControlCharacters(s);
		// check if control char has been removed		
		assertEquals("This is a text with a  control character", cleaned);
				
		// check replacement
		s = "\u0002";
		cleaned = XmlUtils.removeXmlControlCharacters(s, true);
		assertEquals("\uFFFD", cleaned);
		
	}
	
	
}
