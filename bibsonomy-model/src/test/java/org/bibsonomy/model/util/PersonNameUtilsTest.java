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
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author rjanew P
 * @version $Id$
 */
public class PersonNameUtilsTest {
	
	/**
	 * Check if extracting a name in a list of other names gives the same result as only extracting the name.
	 * @throws PersonListParserException 
	 */
	@Test
	public void testExtractList2() throws PersonListParserException {
		final List<PersonName> should = Arrays.asList(
				PersonNameUtils.discoverPersonNames("D.E. Knuth").get(0), 
				PersonNameUtils.discoverPersonNames("Hans Dampf").get(0),
				PersonNameUtils.discoverPersonNames("Donald E. Knuth").get(0),
				PersonNameUtils.discoverPersonNames("Foo van Bar").get(0),
				PersonNameUtils.discoverPersonNames("R. Jäschke").get(0),
				PersonNameUtils.discoverPersonNames("John Chris Smith").get(0),
				PersonNameUtils.discoverPersonNames("John von Neumann").get(0),
				PersonNameUtils.discoverPersonNames("von der Schmidt, Alex").get(0),
				PersonNameUtils.discoverPersonNames("{Long Company Name}").get(0),
				PersonNameUtils.discoverPersonNames("L. Balby Marinho").get(0),
				PersonNameUtils.discoverPersonNames("Balby Marinho, Leandro").get(0),
				PersonNameUtils.discoverPersonNames("Leandro Balby Marinho").get(0),
				PersonNameUtils.discoverPersonNames("Hanand Foobar").get(0)
		);
		final List<PersonName> is = PersonNameUtils.discoverPersonNames("D.E. Knuth and Hans Dampf and Donald E. Knuth and Foo van Bar and Jäschke, R. and John Chris Smith and John von Neumann and von der Schmidt, Alex and {Long Company Name} and L. Balby Marinho and Balby Marinho, Leandro and Leandro Balby Marinho and Hanand Foobar");
		for (int i = 0; i < should.size(); i++) {
			assertEqualPersonNames(should.get(i), is.get(i));			
		}
	}
	
	/**
	 * This test must fail, because we can't discover names of the form "Foo Bar, Blubb" 
	 * in the "First Last" format. 
	 * @throws PersonListParserException 
	 */
	public void testFails() throws PersonListParserException {
		final PersonName is = PersonNameUtils.discoverPersonNames("Blubb Foo Bar").get(0);
		final PersonName should = PersonNameUtils.discoverPersonNames("Foo Bar, Blubb").get(0);
		
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
		final PersonName pn1 = PersonNameUtils.discoverPersonNames("de la Vall{'e}e Poussin, Charles Louis Xavier Joseph").get(0);
		assertEquals("Charles Louis Xavier Joseph", pn1.getFirstName());
		assertEquals("de la Vall{'e}e Poussin", pn1.getLastName());
		
		final PersonName pn2 = PersonNameUtils.discoverPersonNames("{Long Company Name}").get(0);
		assertEquals("{Long Company Name}", pn2.getLastName());
		
		final PersonName pn3 = PersonNameUtils.discoverPersonNames("Donald E. Knuth").get(0);
		assertEquals("Donald E.", pn3.getFirstName());
		assertEquals("Knuth", pn3.getLastName());

		final PersonName pn4 = PersonNameUtils.discoverPersonNames("Foo van Bar").get(0);
		assertEquals("Foo", pn4.getFirstName());
		assertEquals("van Bar", pn4.getLastName());
		
		final PersonName pn5 = PersonNameUtils.discoverPersonNames("von der Schmidt, Alex").get(0);
		assertEquals("Alex", pn5.getFirstName());
		assertEquals("von der Schmidt", pn5.getLastName());

		// this works, since we split after the last "."
		final PersonName pn6 = PersonNameUtils.discoverPersonNames("L. Bar Mar").get(0);
		assertEquals("L. Bar", pn6.getFirstName());
		assertEquals("Mar", pn6.getLastName());
		
		final PersonName pn7 = PersonNameUtils.discoverPersonNames("Bar Mar, Leo").get(0);
		assertEquals("Leo", pn7.getFirstName());
		assertEquals("Bar Mar", pn7.getLastName());
		
		final PersonName pn9 = PersonNameUtils.discoverPersonNames("Bar Mar, L.").get(0);
		assertEquals("L.", pn9.getFirstName());
		assertEquals("Bar Mar", pn9.getLastName());

		final PersonName pn10 = PersonNameUtils.discoverPersonNames("John Chris Smith").get(0);
		assertEquals("John Chris", pn10.getFirstName());
		assertEquals("Smith", pn10.getLastName());	
		
		final PersonName pn13 = PersonNameUtils.discoverPersonNames("Joseph John {Rocchio, Jr.}").get(0);
		assertEquals("Joseph John", pn13.getFirstName());
		assertEquals("{Rocchio, Jr.}", pn13.getLastName());	
		
		final PersonName pn14 = PersonNameUtils.discoverPersonNames("{Rocchio, Jr.}, Joseph John").get(0);
		assertEquals("Joseph John", pn14.getFirstName());
		assertEquals("{Rocchio, Jr.}", pn14.getLastName());	

		final PersonName pn15 = PersonNameUtils.discoverPersonNames("Chappe d'Auteroche").get(0);
		assertEquals("Chappe", pn15.getFirstName());
		assertEquals("d'Auteroche", pn15.getLastName());

		
		final PersonName pn16 = PersonNameUtils.discoverPersonNames("{Frans\\,A.} Janssen").get(0);
		assertEquals("{Frans\\,A.}", pn16.getFirstName());
		assertEquals("Janssen", pn16.getLastName());

		/*
		 * an earlier version of the parser removed "-"
		 */
		final PersonName pn17 = PersonNameUtils.discoverPersonNames("Bai-lin Hao").get(0);
		assertEquals("Bai-lin", pn17.getFirstName());
		assertEquals("Hao", pn17.getLastName());

		/*
		 * an earlier version of the parser removed "~"
		 */
		final PersonName pn18 = PersonNameUtils.discoverPersonNames("Y.~F. Chen").get(0);
		assertEquals("Y.~F.", pn18.getFirstName());
		assertEquals("Chen", pn18.getLastName());
		
		final PersonName pn19 = PersonNameUtils.discoverPersonNames("Rocchio, Jr., Joseph John").get(0);
		assertEquals("Joseph John", pn19.getFirstName());
		assertEquals("Rocchio, Jr.", pn19.getLastName());	
	}
	
	/**
	 * @throws PersonListParserException
	 */
	@Test
	@Ignore
	public void testSpeed() throws PersonListParserException {
		final int count = 500000;
		final String persons = "Wolfgang van Briel and {Barnes and Noble, Inc.} and Ebbo Hahlweg and {Frans\\,A.} Janssen and Joseph John {Rocchio, Jr.} and Barab{\\'a}si, Albert-L{\\'a}szl{\\'o} and {Rocchio, Jr.}, Joseph John and Albert, R{\\'e}ka and Müller, {Arne} and Meier, {Beate} and Jon Kleinberg and \\&\\#201;va Tardos";

		long now = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			PersonNameUtils.discoverPersonNames(persons);
		}
		long diff = System.currentTimeMillis() - now;
		System.out.println("unser: " + diff);
		
		
		now = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			PersonNameParser.parse(persons);
		}
		diff = System.currentTimeMillis() - now;
		System.out.println("bibtx: " + diff);
		
	}
	
	/**
	 * It's not so simple to extract names where we have (erroneously) several 
	 * "and"'s between names
	 * @throws PersonListParserException 
	 */
	@Test
	public void testDiscoverPersonNames() throws PersonListParserException {
		final List<PersonName> pn = PersonNameUtils.discoverPersonNames("D.E. Knuth and Foo Bar and ");
		assertEquals(Arrays.asList(new PersonName("D.E.", "Knuth"), new PersonName("Foo", "Bar")), pn);
		
		/*
		 * ensure that "andere" is not used as "and" delimiter
		 */
		final List<PersonName> pn2 = PersonNameUtils.discoverPersonNames("Wolfgang van Briel and Ebbo Hahlweg and andere");
		assertEquals(Arrays.asList(new PersonName("Wolfgang", "van Briel"), new PersonName("Ebbo", "Hahlweg"), new PersonName("", "andere")), pn2);

		/*
		 * many braces ...
		 */
		final List<PersonName> pn3 = PersonNameUtils.discoverPersonNames("Barab{\\'a}si, Albert-L{\\'a}szl{\\'o} and Albert, R{\\'e}ka");
		assertEquals(Arrays.asList(new PersonName("Albert-L{\\'a}szl{\\'o}", "Barab{\\'a}si"), new PersonName("R{\\'e}ka", "Albert")), pn3);
	
		/*
		 * and again braces ...
		 */
		final List<PersonName> pn4 = PersonNameUtils.discoverPersonNames("Müller, {Arne} and Meier, {Beate}");
		assertEquals(Arrays.asList(new PersonName("{Arne}", "Müller"), new PersonName("{Beate}", "Meier")), pn4);
		
		/*
		 * why semicolon? why?
		 */
		final List<PersonName> pn5 = PersonNameUtils.discoverPersonNames("Jon Kleinberg and T\\&\\#201;va Tardos");
		assertEquals(Arrays.asList(new PersonName("Jon", "Kleinberg"), new PersonName("T\\&\\#201;va", "Tardos")), pn5);
		
	}
	
	private static void printPersonList(final List<PersonName> person) {
		for (final PersonName personName : person) {
			System.out.println(personName);
		}
	}
	
	/**
	 * @throws PersonListParserException 
	 * 
	 */
	@Test
	public void testSerializePersonName() throws PersonListParserException {
		/*
		 * lastFirstName = true
		 */
		assertEquals("de la Vall{'e}e Poussin, Charles Louis Xavier Joseph", ds("de la Vall{'e}e Poussin, Charles Louis Xavier Joseph", true));
		assertEquals("Knuth, D.E.", ds("D.E. Knuth", true));
		assertEquals("Dampf, Hans", ds("Hans Dampf", true));
		assertEquals("Knuth, Donald E.", ds("Donald E. Knuth", true));
		assertEquals("van Bar, Foo", ds("Foo van Bar", true));
		assertEquals("Jäschke, R.", ds("R. Jäschke", true));
		assertEquals("Smith, John Chris", ds("John Chris Smith", true));
		assertEquals("von Neumann, John", ds("John von Neumann", true));
		assertEquals("von der Schmidt, Alex", ds("von der Schmidt, Alex", true));
		assertEquals("{Long Company Name}", ds("{Long Company Name}", true));
		assertEquals("Marinho, L. Balby", ds("L. Balby Marinho", true));
		assertEquals("Balby Marinho, Leandro", ds("Balby Marinho, Leandro", true));
		assertEquals("Rocchio, Jr., Joseph John", ds("Rocchio, Jr., Joseph John", true));
		
		/*
		 * lastFirstName = false
		 */
		assertEquals("D.E. Knuth", ds("D.E. Knuth", false));
		assertEquals("Hans Dampf", ds("Hans Dampf", false));
		assertEquals("Donald E. Knuth", ds("Donald E. Knuth", false));
		assertEquals("Foo van Bar", ds("Foo van Bar", false));
		assertEquals("R. Jäschke", ds("R. Jäschke", false));
		assertEquals("John Chris Smith", ds("John Chris Smith", false));
		assertEquals("John von Neumann", ds("John von Neumann", false));
		assertEquals("Alex von der Schmidt", ds("von der Schmidt, Alex", false));
		assertEquals("{Long Company Name}", ds("{Long Company Name}", false));
		assertEquals("L. Balby Marinho", ds("L. Balby Marinho", false));
		assertEquals("Leandro Balby Marinho", ds("Balby Marinho, Leandro", false));
		assertEquals("Leandro Balby Marinho", ds("Leandro Balby Marinho", false));
		assertEquals("Joseph John Rocchio, Jr.", ds("Rocchio, Jr., Joseph John", false));
		
		/*
		 * special case: "others"
		 */
		assertEquals("others", ds("others", true));
		assertEquals("others", ds("others", false));
	}
	
	/**
	 * For these names we guarantee that our serialization does not change them.
	 * <br/>
	 * This assumption is stronger than the one in {@link #testDiscoverSerializeDiscoverSerialize()}.
	 * @throws PersonListParserException 
	 */
	@Test
	public void testDiscoverSerialize() throws PersonListParserException {
		ds("Jäschke, R.");
		ds("Smith, John Chris");
		ds("von der Schmidt, Alex");
		ds("{Long Company Name}");
		ds("Bar Mar, Leo");
		ds("Bar Mar, L.");
		ds("{Rocchio, Jr.}, Joseph John");
		ds("Barab{\\'a}si, Albert-L{\\'a}szl{\\'o} and Albert, R{\\'e}ka");
		ds("Müller, {Arne} and Meier, {Beate}");
		ds("de la Vall{'e}e Poussin, Charles Louis Xavier Joseph");
		ds("Foobar, Hanand");
		ds("Rocchio, Jr., Joseph John");
	}
	
	/**
	 * Discovers the persons in p, serializes them and asserts that the result
	 * is the same as p. 
	 * 
	 * 
	 * @param p
	 * @throws PersonListParserException 
	 */
	private void ds(final String p) throws PersonListParserException {
		assertEquals(p, PersonNameUtils.serializePersonNames(PersonNameUtils.discoverPersonNames(p)));
	}
	
	
	/**
	 * @param p
	 * @param lastFirst
	 * @return
	 * @throws PersonListParserException
	 */
	private String ds(final String p, final boolean lastFirst) throws PersonListParserException {
		return PersonNameUtils.serializePersonNames(PersonNameUtils.discoverPersonNames(p), lastFirst);
	}
	
	/**
	 * For these names we can only guarantee that a second serialization does
	 * not change them. 
	 * Thus, this method checks if {@link PersonNameUtils#serializePersonNames(List)} 
	 * is compatible with {@link PersonNameUtils#discoverPersonNames(String)}.
	 * <br/>
	 * See also {@link #testDiscoverSerialize()}.
	 * @throws PersonListParserException 
	 */
	@Test
	public void testDiscoverSerializeDiscoverSerialize() throws PersonListParserException {
		dsds("D.E. Knuth");
		dsds("D.E. Knuth");
		dsds("Donald E. Knuth");
		dsds("Foo van Bar");
		dsds("R. Jäschke");
		dsds("John Chris Smith");
		dsds("Alex von der Schmidt");
		dsds("Jäschke, R.");
		dsds("Smith, John Chris");
		dsds("von der Schmidt, Alex");		
		dsds("{Long Company Name}");
		dsds("L. Balby Marinho");
		dsds("Leandro Balby Marinho");
		dsds("and D.E. Knuth and and Foo Bar and   and");
		dsds("Wolfgang van Briel and Ebbo Hahlweg and andere");
		dsds("Barab{\\'a}si, Albert-L{\\'a}szl{\\'o} and Albert, R{\\'e}ka");
		dsds("Müller, {Arne} and Meier, {Beate}");
		dsds("Jon Kleinberg and \\&\\#201;va Tardos");
		dsds("de la Vall{'e}e Poussin, Charles Louis Xavier Joseph");
		dsds("Joseph John {Rocchio, Jr.}");
		dsds("Chappe d'Auteroche");
		dsds("{Frans\\,A.} Janssen");
		dsds("A. Foo and B. Bar and others");
		dsds("Hanand Foobar");
		dsds("Rocchio, Jr., Joseph John");
	}
	
	/**
	 * Discovers the persons in p, serializes them into s, discovers the persons
	 * in s and then asserts that this is the same as s.
	 * 
	 * @param p
	 * @throws PersonListParserException 
	 */
	private void dsds(final String p) throws PersonListParserException {
		/*
		 * We first check the "Last, First" format ... 
		 */
		final String lastFirst = ds(p, true);
		assertEquals("Serialization of '" + p + "' failed.", lastFirst, ds(lastFirst, true));
		/*
		 * ... and then the "First Last" format. 
		 */
		final String firstLast = ds(p, false);
		assertEquals("Serialization of '" + p + "' failed.", firstLast, ds(lastFirst, false));
	}
	
	
	/**
	 * @throws PersonListParserException 
	 * 
	 */
	@Test
	public void serializePersonNames1() throws PersonListParserException {
		final PersonName[] personNames = new PersonName[]{
				PersonNameUtils.discoverPersonNames("Balby Marinho, Leandro").get(0),
				PersonNameUtils.discoverPersonNames("Donald E. Knuth").get(0)
		};
		
		assertEquals("Leandro Balby Marinho and Donald E. Knuth", PersonNameUtils.serializePersonNames(Arrays.asList(personNames), false));
		assertEquals("Balby Marinho, Leandro and Knuth, Donald E.", PersonNameUtils.serializePersonNames(Arrays.asList(personNames), true));
		
	}
	
	/**
	 * @throws PersonListParserException 
	 * 
	 */
	@Test
	public void serializePersonNames2() throws PersonListParserException {
		final PersonName[] personNames = new PersonName[]{
				PersonNameUtils.discoverPersonNames("Hans von und zu Kottenbröder").get(0),
				PersonNameUtils.discoverPersonNames("Nachname, Vorname").get(0)
		};
		
		assertEquals("Hans von und zu Kottenbröder and Vorname Nachname", PersonNameUtils.serializePersonNames(Arrays.asList(personNames), false));
		assertEquals("von und zu Kottenbröder, Hans and Nachname, Vorname", PersonNameUtils.serializePersonNames(Arrays.asList(personNames), true));
	}

	
	/**
	 * @throws Exception
	 */
	public void testDiscoverNotWorking() throws Exception {
		/*
		 * this "First Last" name form is ambigue - we can't discover the correct name 
		 */
		final PersonName pn8 = PersonNameUtils.discoverPersonNames("Leo Bar Mar").get(0);
		assertEquals("Leo Bar", pn8.getFirstName());
		assertEquals("Mar", pn8.getLastName());

		/*
		 * well, it's just broken - use brackets for things like that
		 */
		final PersonName pn11 = PersonNameUtils.discoverPersonNames("ECML/PKDD’03 workshop proceedings").get(0);
		assertEquals("ECML/PKDD’03 workshop proceedings", pn11.getLastName());
		/*
		 * too many commas ...
		 */
		final List<PersonName> pn4 = PersonNameUtils.discoverPersonNames("K. Wilson and J. Brake and A. F. Lee, and R.M. Lambert,");
		printPersonList(pn4);
		assertEquals(Arrays.asList(new PersonName("K.", "Wilson"), new PersonName("J.", "Brake"), new PersonName("A. F.", "Lee"), new PersonName("R.M.", "Lambert")), pn4);
		/*
		 * ; as person delimiter ...
		 */
		final List<PersonName> pn6 = PersonNameUtils.discoverPersonNames("Kirsch KA; Schlemmer M");
		assertEquals(Arrays.asList(new PersonName("KA", "Kirsch"), new PersonName("M", "Schlemmer")), pn6);



	}
	
	/**
	 * Should be also working now because of new parser.
	 * @throws PersonListParserException 
	 * 
	 * @throws PersonListParserException
	 */
	@Test
	public void testShouldWorkNow() throws PersonListParserException {
		/*
		 * does not work, since we split at the last "."
		 */
		final PersonName pn9 = PersonNameUtils.discoverPersonNames("M. Joe Fox").get(0);
		assertEquals("M. Joe", pn9.getFirstName());
		assertEquals("Fox", pn9.getLastName());
		
		
		/*
		 * we now support this kind of lineage ... :-( 
		 */

		final PersonName pn12 = PersonNameUtils.discoverPersonNames("Rocchio, Jr., Joseph John").get(0);
		assertEquals("Joseph John", pn12.getFirstName());
		assertEquals("Rocchio, Jr.", pn12.getLastName());	

	}

}

