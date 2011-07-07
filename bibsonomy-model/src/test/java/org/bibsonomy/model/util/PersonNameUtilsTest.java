/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

package org.bibsonomy.model.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.bibsonomy.model.PersonName;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class PersonNameUtilsTest {

	/**
	 * 
	 */
	@Test
	public void testExtractList() {
		final List<PersonName> should = Arrays.asList(new PersonName("D.E. Knuth"));
		final List<PersonName> is = PersonNameUtils.extractList("D.E. Knuth");
		assertEqualPersonNames(should.get(0), is.get(0));
	}
	
	/**
	 * 
	 */
	@Test
	public void testExtractList2() {
		final List<PersonName> should = Arrays.asList(
				new PersonName("D.E. Knuth"), 
				new PersonName("Hans Dampf"),
				new PersonName("Donald E. Knuth"),
				new PersonName("Foo van Bar"),
				new PersonName("R. Jäschke"),
				new PersonName("John Chris Smith"),
				new PersonName("John von Neumann"),
				new PersonName("von der Schmidt, Alex"),
				new PersonName("{Long Company Name}"),
				new PersonName("L. Balby Marinho"),
				new PersonName("Balby Marinho, Leandro"),
				new PersonName("Leandro Balby Marinho")
		);
		System.out.println("sh = " + should);
		final List<PersonName> is = PersonNameUtils.extractList("D.E. Knuth and Hans Dampf and Donald E. Knuth and Foo van Bar and Jäschke, R. and John Chris Smith and John von Neumann and von der Schmidt, Alex and {Long Company Name} and L. Balby Marinho and Balby Marinho, Leandro and Leandro Balby Marinho");
		System.out.println("is = " + is);
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

	/**
	 * 
	 */
	@Test
	public void testLastFirstToFirstLast() {
		
		assertEquals("D.E. Knuth", PersonNameUtils.lastFirstToFirstLast("Knuth, D.E."));
		assertEquals("D.E. Knuth", PersonNameUtils.lastFirstToFirstLast("D.E. Knuth"));

	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testDiscover() throws Exception {
		final PersonName pn1 = new PersonName("de la Vall{'e}e Poussin, Charles Louis Xavier Joseph");
		assertEquals("Charles Louis Xavier Joseph", pn1.getFirstName());
		assertEquals("de la Vall{'e}e Poussin", pn1.getLastName());
		
		final PersonName pn2 = new PersonName("{Long Company Name}");
		assertEquals("{Long Company Name}", pn2.getLastName());
		
		final PersonName pn3 = new PersonName("Donald E. Knuth");
		assertEquals("Donald E.", pn3.getFirstName());
		assertEquals("Knuth", pn3.getLastName());

		final PersonName pn4 = new PersonName("Foo van Bar");
		assertEquals("Foo", pn4.getFirstName());
		assertEquals("van Bar", pn4.getLastName());
		
		final PersonName pn5 = new PersonName("von der Schmidt, Alex");
		assertEquals("Alex", pn5.getFirstName());
		assertEquals("von der Schmidt", pn5.getLastName());
		
		final PersonName pn6 = new PersonName("L. Bar Mar");
		assertEquals("L.", pn6.getFirstName());
		assertEquals("Bar Mar", pn6.getLastName());
		
		final PersonName pn7 = new PersonName("Bar Mar, Leo");
		assertEquals("Leo", pn7.getFirstName());
		assertEquals("Bar Mar", pn7.getLastName());
		
		final PersonName pn9 = new PersonName("Bar Mar, L.");
		assertEquals("L.", pn9.getFirstName());
		assertEquals("Bar Mar", pn9.getLastName());

		final PersonName pn10 = new PersonName("John Chris Smith");
		assertEquals("John Chris", pn10.getFirstName());
		assertEquals("Smith", pn10.getLastName());	
	}
	
	/**
	 * Not working name
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testDiscoverNotWorking() throws Exception {
		/*
		 * this "First Last" name form is ambigue - we can't discover the correct name 
		 */
		final PersonName pn8 = new PersonName("Leo Bar Mar");
		assertEquals("Leo", pn8.getFirstName());
		assertEquals("Bar Mar", pn8.getLastName());
	}

	/**
	 * @throws Exception
	 */
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
		assertEquals("Dampf", PersonNameUtils.getFirstPersonsLastName("Dampf, Hans"));
		assertEquals("Dampf", PersonNameUtils.getFirstPersonsLastName("Hans Dampf and Reiner Zufall"));
		assertEquals("von Neumann", PersonNameUtils.getFirstPersonsLastName("von Neumann, John"));
		assertEquals("von Neumann", PersonNameUtils.getFirstPersonsLastName("John von Neumann"));
		assertEquals("{Long Company Name}", PersonNameUtils.getFirstPersonsLastName("{Long Company Name}"));
	}
}

