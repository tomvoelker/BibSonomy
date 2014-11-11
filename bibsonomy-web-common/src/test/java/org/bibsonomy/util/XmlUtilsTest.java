/**
 *
 *  BibSonomy-Web-Common - Common things for web
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Tests XMLUtils
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 */
public class XmlUtilsTest {
	private static final String HTML_END = "</p></body></html>";
	private static final String HTML_START = "<html><head><title>HALLO</title><body><p>";
	private static final String[] MESSAGES = new String[] {"Hello", "Salut", "Hallo", "Buon giorno", "Haai", "Hi", "Barev"};

	/**
	 * Tests also thread safety!
	 * @throws Exception 
	 */
	@Test
	public void testGetDOM() throws Exception {
		final ExecutorService service = Executors.newFixedThreadPool(10);
		final List<XMLParsingThreadimplements> runables = new LinkedList<XMLParsingThreadimplements>();
		for (int i = 0; i < 10; i++) {
			final XMLParsingThreadimplements runable = new XMLParsingThreadimplements(("thread " + i));
			runables.add(runable);
			service.submit(runable);
		}
		
		service.shutdown();
		service.awaitTermination(10, TimeUnit.MINUTES);
		
		for (XMLParsingThreadimplements xmlParsingThreadimplements : runables) {
			if (xmlParsingThreadimplements.error != null) {
				fail(xmlParsingThreadimplements.error.getMessage());
			}
		}
	}
	
	@Test
	@Ignore // FIXME: bibsonomy boostrap layout is html 5 so jtidy does not work
	public void testGetDom() throws Exception {
		assertNotNull(XmlUtils.getDOM(new URL("http://www.bibsonomy.org/")));
	}

	private static class XMLParsingThreadimplements implements Runnable {
		private final String name;
		private Throwable error;

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
			} catch (final Throwable e) {
				this.error = e;
			}
		}
	}
}
