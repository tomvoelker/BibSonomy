package org.bibsonomy.model;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.model.util.PersonNameUtils;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class PersonNameTest {

	/**
	 * 
	 */
	@Test
	public void testEqualsAndHashCodeWhiteSpace() {
		final PersonName p1 = new PersonName(null, "Knuth");
		final PersonName p2 = new PersonName(" ", "Knuth");
		final PersonName p3 = new PersonName("", "Knuth");
		final PersonName p4 = new PersonName("\n", "Knuth");
		assertEquals(p1, p1);
		assertEquals(p1, p2);
		assertEquals(p1, p3);
		assertEquals(p1, p4);
		assertEquals(p2, p1);
		assertEquals(p2, p2);
		assertEquals(p2, p3);
		assertEquals(p2, p4);
		assertEquals(p3, p1);
		assertEquals(p3, p2);
		assertEquals(p3, p3);
		assertEquals(p3, p4);
		assertEquals(p4, p1);
		assertEquals(p4, p2);
		assertEquals(p4, p3);
		assertEquals(p4, p4);
		assertEquals(p1.hashCode(), p1.hashCode());
		assertEquals(p1.hashCode(), p2.hashCode());
		assertEquals(p1.hashCode(), p3.hashCode());
		assertEquals(p1.hashCode(), p4.hashCode());
		assertEquals(p2.hashCode(), p1.hashCode());
		assertEquals(p2.hashCode(), p2.hashCode());
		assertEquals(p2.hashCode(), p3.hashCode());
		assertEquals(p2.hashCode(), p4.hashCode());
		assertEquals(p3.hashCode(), p1.hashCode());
		assertEquals(p3.hashCode(), p2.hashCode());
		assertEquals(p3.hashCode(), p3.hashCode());
		assertEquals(p3.hashCode(), p4.hashCode());
		assertEquals(p4.hashCode(), p1.hashCode());
		assertEquals(p4.hashCode(), p2.hashCode());
		assertEquals(p4.hashCode(), p3.hashCode());
		assertEquals(p4.hashCode(), p4.hashCode());
	}
	
	@Test
	public void testStrangeBehaviour1() throws Exception {
		final PersonName p1 = PersonNameUtils.discoverPersonName("Lonely Writer");
	}

}
