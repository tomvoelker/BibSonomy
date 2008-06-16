package org.bibsonomy.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class DocumentTest {

	/**
	 * tests that userName is always set to lowercase
	 * 
	 * @see UserTest#name()
	 */
	@Test
	public void userName() {
		assertEquals(null, new Document().getUserName());

		final Document doc = new Document();
		doc.setUserName("TeStUsEr");
		assertEquals("testuser", doc.getUserName());
		doc.setUserName(null);
		assertNull(doc.getUserName());
	}
}