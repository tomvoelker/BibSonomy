package org.bibsonomy.model.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.bibsonomy.model.PersonName;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class PersonNameUtilsTest {

	@Test
	public void testExtractList() {
		final List<PersonName> should = Arrays.asList(new PersonName("D.E. Knuth"));
		final List<PersonName> is = PersonNameUtils.extractList("D.E. Knuth");
		assertEqualPersonNames(should.get(0), is.get(0));
	}
	
	@Test
	public void testExtractList2() {
		final List<PersonName> should = Arrays.asList(
				new PersonName("D.E. Knuth"), 
				new PersonName("Hans Dampf"),
				new PersonName("Donald E. Knuth"),
				new PersonName("Foo van Bar"),
				new PersonName("R. Jäschke"),
				new PersonName("L. Balby Marinho")
		);
		final List<PersonName> is = PersonNameUtils.extractList("D.E. Knuth and Hans Dampf and Donald E. Knuth and Foo van Bar and Jäschke, R. and L. Balby Marinho");
		for (int i = 0; i < should.size(); i++) {
			assertEqualPersonNames(should.get(i), is.get(i));			
		}
	}
	
	/**
	 * This test must fail, because we can't discover names of the form "Foo Bar, Blubb" 
	 * in the "First Last" format. 
	 */
	public void testFails() {
		final PersonName is = new PersonName("Blubb Foo Bar");
		final PersonName should = new PersonName("Foo Bar, Blubb");
		
		//assertEqualPersonNames(should, is);
		assertFalse(is.getFirstName().equals(should.getFirstName()));
		assertFalse(is.getLastName().equals(should.getLastName()));
	}
	
	private static void assertEqualPersonNames(final PersonName a, final PersonName b) {
		assertEquals(a.getFirstName(), b.getFirstName());
		assertEquals(a.getLastName(), b.getLastName());
	}

	@Test
	public void testLastFirstToFirstLast() {
		
		assertEquals("D.E. Knuth", PersonNameUtils.lastFirstToFirstLast("Knuth, D.E."));
		assertEquals("D.E. Knuth", PersonNameUtils.lastFirstToFirstLast("D.E. Knuth"));

	}
	
	@Test
	public void testDiscover() throws Exception {
		final PersonName personName = new PersonName("de la Vall{'e}e Poussin, Charles Louis Xavier Joseph");
		assertEquals("Charles Louis Xavier Joseph", personName.getFirstName());
		assertEquals("de la Vall{'e}e Poussin", personName.getLastName());
	}

	@Test
	public void testLastFirstToFirstLastMany() throws Exception {
		assertEquals("D.E. Knuth", PersonNameUtils.lastFirstToFirstLastMany("Knuth, D.E."));
		assertEquals("D.E. Knuth and D.E. Knuth", PersonNameUtils.lastFirstToFirstLastMany("Knuth, D.E. and D.E. Knuth"));
		assertEquals("D.E. Knuth and D.E. Knuth and Leandro Balby Marinho and Leandro Balby Marinho", PersonNameUtils.lastFirstToFirstLastMany("Knuth, D.E. and D.E. Knuth and Leandro Balby Marinho and Balby Marinho, Leandro"));
	}

	/**
	 * tests getFirstPersonsLastName
	 */
	@Test
	public void getFirstPersonsLastName() {
		assertNull(PersonNameUtils.getFirstPersonsLastName(null));
		assertEquals("Dampf", PersonNameUtils.getFirstPersonsLastName("Hans Dampf"));
		assertEquals("Dampf", PersonNameUtils.getFirstPersonsLastName("Hans Dampf and Reiner Zufall"));
		// XXX: this should be "Dampf" instead of "Zufall"
		assertEquals("Zufall", PersonNameUtils.getFirstPersonsLastName("Hans Dampf, Reiner Zufall"));
	}
}
