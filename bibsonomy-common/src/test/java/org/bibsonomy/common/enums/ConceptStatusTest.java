package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.UnsupportedConceptStatusException;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class ConceptStatusTest {

	/**
	 * tests getConceptStatus
	 */
	@Test
	public void getConceptStatus() {
		assertEquals(ConceptStatus.PICKED, ConceptStatus.getConceptStatus("picked"));
		assertEquals(ConceptStatus.PICKED, ConceptStatus.getConceptStatus("PiCkEd"));
		assertEquals(ConceptStatus.UNPICKED, ConceptStatus.getConceptStatus("unpicked"));
		assertEquals(ConceptStatus.UNPICKED, ConceptStatus.getConceptStatus("UnPiCkEd"));
		assertEquals(ConceptStatus.ALL, ConceptStatus.getConceptStatus("all"));
		assertEquals(ConceptStatus.ALL, ConceptStatus.getConceptStatus("AlL"));
		
		try {
			ConceptStatus.getConceptStatus(null);
			fail("Should throw exception");
		} catch (InternServerException ignore) {
		}

		for (final String test : new String[] {"", " ", "test"}) {
			try {
				ConceptStatus.getConceptStatus(test);
				fail("Should throw exception");
			} catch (UnsupportedConceptStatusException ignore) {
			}
		}
	}

	/**
	 * tests toString
	 */
	@Test
	public void testToString() {
		assertEquals("picked", ConceptStatus.PICKED.toString());
		assertEquals("unpicked", ConceptStatus.UNPICKED.toString());
		assertEquals("all", ConceptStatus.ALL.toString());
	}
}