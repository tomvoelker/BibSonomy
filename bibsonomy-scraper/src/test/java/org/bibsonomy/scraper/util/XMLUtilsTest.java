package org.bibsonomy.scraper.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.w3c.dom.Document;

/**
 * @author rja
 * @version $Id$
 */
public class XMLUtilsTest {

	private static final String HTML_END = "</p></body></html>";
	private static final String HTML_START = "<html><head><title>HALLO</title><body><p>";
	private static final String[] MESSAGES = new String[] {"Hello", "Salut", "Hallo", "Buon giorno", "Haai", "Hi", "Barev"};

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
					
					final Document dom = XMLUtils.getDOM(HTML_START + text + HTML_END);

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
