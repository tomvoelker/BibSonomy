package org.bibsonomy.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.w3c.dom.Document;

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
	
    
	private static final String HTML_END = "</p></body></html>";
	private static final String HTML_START = "<html><head><title>HALLO</title><body><p>";
	private static final String[] MESSAGES = new String[] {"Hello", "Salut", "Hallo", "Buon giorno", "Haai", "Hi", "Barev"};

    
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

	/**
	 * Tests also thread safety!
	 */
	@Test
	public void testGetDOM() {
		for (int i = 0; i < 10; i++) {
			new Thread(new XMLParsingThreadimplements(("thread " + i))).start();
		}
	}


	public static class XMLParsingThreadimplements implements Runnable {
		private final String name;

		public XMLParsingThreadimplements(final String name) {
			this.name = name;
		}

		public void run() {
			try {
				for (final String message: MESSAGES) {
					final String text = message + " " + name + "!";
					
					final Document dom = XmlUtils.getDOM(HTML_START + text + HTML_END);

					final String nodeValue = dom.getChildNodes().item(0).getNextSibling().getChildNodes().item(1).getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
					assertEquals(text, nodeValue);
					Thread.sleep(10);
				}
			} catch (final Exception e) {
				System.err.println(e);
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}
	
	
}
