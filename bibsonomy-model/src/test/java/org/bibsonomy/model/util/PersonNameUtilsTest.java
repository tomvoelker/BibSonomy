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

import java.util.Arrays;
import java.util.List;

import org.bibsonomy.model.PersonName;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author rjanew P
 * @version $Id$
 */
public class PersonNameUtilsTest {

	/**
	 * 
	 */
	@Test
	public void testExtractList() {
		final List<PersonName> should = Arrays.asList(PersonNameUtils.discoverPersonName("D.E. Knuth"));
		final List<PersonName> is = PersonNameUtils.discoverPersonNames("D.E. Knuth");
		assertEqualPersonNames(should.get(0), is.get(0));
	}
	
	/**
	 * 
	 */
	@Test
	public void testExtractList2() {
		final List<PersonName> should = Arrays.asList(
				PersonNameUtils.discoverPersonName("D.E. Knuth"), 
				PersonNameUtils.discoverPersonName("Hans Dampf"),
				PersonNameUtils.discoverPersonName("Donald E. Knuth"),
				PersonNameUtils.discoverPersonName("Foo van Bar"),
				PersonNameUtils.discoverPersonName("R. Jäschke"),
				PersonNameUtils.discoverPersonName("John Chris Smith"),
				PersonNameUtils.discoverPersonName("John von Neumann"),
				PersonNameUtils.discoverPersonName("von der Schmidt, Alex"),
				PersonNameUtils.discoverPersonName("{Long Company Name}"),
				PersonNameUtils.discoverPersonName("L. Balby Marinho"),
				PersonNameUtils.discoverPersonName("Balby Marinho, Leandro"),
				PersonNameUtils.discoverPersonName("Leandro Balby Marinho"),
				PersonNameUtils.discoverPersonName("Hanand Foobar")
		);
		final List<PersonName> is = PersonNameUtils.discoverPersonNames("D.E. Knuth and Hans Dampf and Donald E. Knuth and Foo van Bar and Jäschke, R. and John Chris Smith and John von Neumann and von der Schmidt, Alex and {Long Company Name} and L. Balby Marinho and Balby Marinho, Leandro and Leandro Balby Marinho and Hanand Foobar");
		for (int i = 0; i < should.size(); i++) {
			assertEqualPersonNames(should.get(i), is.get(i));			
		}
	}
	
	/**
	 * This test must fail, because we can't discover names of the form "Foo Bar, Blubb" 
	 * in the "First Last" format. 
	 */
	public void testFails() {
		final PersonName is = PersonNameUtils.discoverPersonName("Blubb Foo Bar");
		final PersonName should = PersonNameUtils.discoverPersonName("Foo Bar, Blubb");
		
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
		final PersonName pn1 = PersonNameUtils.discoverPersonName("de la Vall{'e}e Poussin, Charles Louis Xavier Joseph");
		assertEquals("Charles Louis Xavier Joseph", pn1.getFirstName());
		assertEquals("de la Vall{'e}e Poussin", pn1.getLastName());
		
		final PersonName pn2 = PersonNameUtils.discoverPersonName("{Long Company Name}");
		assertEquals("{Long Company Name}", pn2.getLastName());
		
		final PersonName pn3 = PersonNameUtils.discoverPersonName("Donald E. Knuth");
		assertEquals("Donald E.", pn3.getFirstName());
		assertEquals("Knuth", pn3.getLastName());

		final PersonName pn4 = PersonNameUtils.discoverPersonName("Foo van Bar");
		assertEquals("Foo", pn4.getFirstName());
		assertEquals("van Bar", pn4.getLastName());
		
		final PersonName pn5 = PersonNameUtils.discoverPersonName("von der Schmidt, Alex");
		assertEquals("Alex", pn5.getFirstName());
		assertEquals("von der Schmidt", pn5.getLastName());

		// this works, since we split after the last "."
		final PersonName pn6 = PersonNameUtils.discoverPersonName("L. Bar Mar");
		assertEquals("L.", pn6.getFirstName());
		assertEquals("Bar Mar", pn6.getLastName());
		
		final PersonName pn7 = PersonNameUtils.discoverPersonName("Bar Mar, Leo");
		assertEquals("Leo", pn7.getFirstName());
		assertEquals("Bar Mar", pn7.getLastName());
		
		final PersonName pn9 = PersonNameUtils.discoverPersonName("Bar Mar, L.");
		assertEquals("L.", pn9.getFirstName());
		assertEquals("Bar Mar", pn9.getLastName());

		final PersonName pn10 = PersonNameUtils.discoverPersonName("John Chris Smith");
		assertEquals("John Chris", pn10.getFirstName());
		assertEquals("Smith", pn10.getLastName());	
		
		final PersonName pn13 = PersonNameUtils.discoverPersonName("Joseph John {Rocchio, Jr.}");
		assertEquals("Joseph John", pn13.getFirstName());
		assertEquals("{Rocchio, Jr.}", pn13.getLastName());	
	}
	
	/**
	 * It's not so simple to extract names where we have (erroneously) several 
	 * "and"'s between names
	 */
	@Test
	public void testDiscoverPersonNames() {
		final List<PersonName> pn = PersonNameUtils.discoverPersonNames("D.E. Knuth and and Foo Bar and   and");
		assertEquals(Arrays.asList(new PersonName("D.E.", "Knuth"), new PersonName("Foo", "Bar")), pn);
		
	}
	
	/**
	 * 
	 */
	@Test
	public void testSerializePersonName() {
		/*
		 * lastFirstName = true
		 */
		assertEquals("Knuth, D.E.", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("D.E. Knuth"), true));
		assertEquals("Dampf, Hans", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("Hans Dampf"), true));
		assertEquals("Knuth, Donald E.", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("Donald E. Knuth"), true));
		assertEquals("van Bar, Foo", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("Foo van Bar"), true));
		assertEquals("Jäschke, R.", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("R. Jäschke"), true));
		assertEquals("Smith, John Chris", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("John Chris Smith"), true));
		assertEquals("von Neumann, John", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("John von Neumann"), true));
		assertEquals("von der Schmidt, Alex", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("von der Schmidt, Alex"), true));
		assertEquals("{Long Company Name}", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("{Long Company Name}"), true));
		assertEquals("Balby Marinho, L.", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("L. Balby Marinho"), true));
		assertEquals("Balby Marinho, Leandro", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("Balby Marinho, Leandro"), true));

		/*
		 * lastFirstName = false
		 */
		assertEquals("D.E. Knuth", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("D.E. Knuth"), false));
		assertEquals("Hans Dampf", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("Hans Dampf"), false));
		assertEquals("Donald E. Knuth", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("Donald E. Knuth"), false));
		assertEquals("Foo van Bar", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("Foo van Bar"), false));
		assertEquals("R. Jäschke", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("R. Jäschke"), false));
		assertEquals("John Chris Smith", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("John Chris Smith"), false));
		assertEquals("John von Neumann", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("John von Neumann"), false));
		assertEquals("Alex von der Schmidt", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("von der Schmidt, Alex"), false));
		assertEquals("{Long Company Name}", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("{Long Company Name}"), false));
		assertEquals("L. Balby Marinho", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("L. Balby Marinho"), false));
		assertEquals("Leandro Balby Marinho", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("Balby Marinho, Leandro"), false));
		assertEquals("Leandro Balby Marinho", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("Leandro Balby Marinho"), false));

		/*
		 * special case: "others"
		 */
		assertEquals("others", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("others"), true));
		assertEquals("others", PersonNameUtils.serializePersonName(PersonNameUtils.discoverPersonName("others"), false));
	}
	
	/**
	 * 
	 */
	@Test
	public void serializePersonNames1() {
		final PersonName[] personNames = new PersonName[]{
				PersonNameUtils.discoverPersonName("Balby Marinho, Leandro"),
				PersonNameUtils.discoverPersonName("Donald E. Knuth")
		};
		
		assertEquals("Leandro Balby Marinho and Donald E. Knuth", PersonNameUtils.serializePersonNames(Arrays.asList(personNames), false));
		assertEquals("Balby Marinho, Leandro and Knuth, Donald E.", PersonNameUtils.serializePersonNames(Arrays.asList(personNames), true));
	}
	
	/**
	 * 
	 */
	@Test
	public void serializePersonNames2() {
		final PersonName[] personNames = new PersonName[]{
				PersonNameUtils.discoverPersonName("Hans von und zu Kottenbröder"),
				PersonNameUtils.discoverPersonName("Nachname, Vorname")
		};
		
		assertEquals("Hans von und zu Kottenbröder and Vorname Nachname", PersonNameUtils.serializePersonNames(Arrays.asList(personNames), false));
		assertEquals("von und zu Kottenbröder, Hans and Nachname, Vorname", PersonNameUtils.serializePersonNames(Arrays.asList(personNames), true));
	}

	
	/**
	 * Not working namesFIX
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testDiscoverNotWorking() throws Exception {
		/*
		 * this "First Last" name form is ambigue - we can't discover the correct name 
		 */
		final PersonName pn8 = PersonNameUtils.discoverPersonName("Leo Bar Mar");
		assertEquals("Leo", pn8.getFirstName());
		assertEquals("Bar Mar", pn8.getLastName());
		/*
		 * does not work, since we split at the last "."
		 */
		final PersonName pn9 = PersonNameUtils.discoverPersonName("M. Joe Fox");
		assertEquals("M. Joe", pn9.getFirstName());
		assertEquals("Fox", pn9.getLastName());
		
		/*
		 * we remove numbers and split this into parts .... well, it's just broken
		 */
		final PersonName pn11 = PersonNameUtils.discoverPersonName("ECML/PKDD’03 workshop proceedings");
		assertEquals("ECML/PKDD’03 workshop proceedings", pn11.getLastName());
		
		/*
		 * FIXME: we don't support lineage ... :-( 
		 */

		final PersonName pn12 = PersonNameUtils.discoverPersonName("Rocchio, Jr., Joseph John");
		assertEquals("Joseph John", pn12.getFirstName());
		assertEquals("Rocchio, Jr.", pn12.getLastName());	
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

}

