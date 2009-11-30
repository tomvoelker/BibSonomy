/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Testcase for the BibtexUtils class
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class BibtexUtilsTest {

	private static final String FIELD_VALUE = "http://www.slac.stanford.edu/spires/find/hep?key=6368328";
	private static final String FIELD_NAME  = "url";
	private final static String bibtexStart = "@Article{Okumura:2005qr,\n" +  
	"author    = \"Okumura, Ken-ichi\",\n" + 
	"title     = \"{Sparticle spectrum and EWSB of mixed modulus-anomaly\n" +
	"             mediation in fluxed string compactification models}\",\n" +
	"year      = \"2005\",\n" + 
	"eprint    = \"hep-ph/0509225\",\n" +
	"archivePrefix = \"arXiv\",\n" + 
	"SLACcitation  = \"%%CITATION = HEP-PH/0509225;%%\"\n";

	private final static String bibtex = bibtexStart + "}";

	/**
	 * Tests with a field not occuring in the entry
	 */
	@Test
	public void testAddFieldNotContained() {
		final String addFieldIfNotContained = BibTexUtils.addFieldIfNotContained(bibtex, FIELD_NAME, FIELD_VALUE);
		final String expected = bibtexStart + "," + FIELD_NAME + " = {" + FIELD_VALUE + "}\n}";
		assertEquals(expected, addFieldIfNotContained);
	}

	/**
	 * Tests with a field occuring in the entry
	 */
	@Test
	public void testAddFieldContained() {
		final String addFieldIfNotContained = BibTexUtils.addFieldIfNotContained(bibtex, "year", FIELD_VALUE);
		assertEquals(bibtex, addFieldIfNotContained);
	}

	/**
	 * Tests with a field not occuring in the entry
	 */
	@Test
	public void testAddField() {
		final StringBuffer buf = new StringBuffer(bibtex);
		BibTexUtils.addField(buf, FIELD_NAME, FIELD_VALUE);
		final String expected = bibtexStart + "," + FIELD_NAME + " = {" + FIELD_VALUE + "}\n}";
		assertEquals(expected, buf.toString());
	}

	/**
	 * tests generation of bibtex string
	 */
	@Test
	public void toBibtexString() {
		final BibTex bib = new BibTex();
		bib.setEntrytype("inproceedings");
		bib.setBibtexKey("KIE");
		bib.setTitle("The most wonderfult title on earth");
		bib.setAuthor("Hans Dampf and Peter Silie");
		bib.setJournal("Journal of the most wonderful articles on earth");
		bib.setYear("2525");
		bib.setVolume("3");
		bib.setAbstract("This is a nice abstract.");

		final String expectedBibtex = 
			"@inproceedings{KIE,\n" +			
			"  author = {Hans Dampf and Peter Silie},\n" +
			"  journal = {Journal of the most wonderful articles on earth},\n" +
			"  title = {The most wonderfult title on earth},\n" +
			"  volume = {3},\n" +
			"  year = {2525},\n" + 
			"  abstract = {This is a nice abstract.}\n}";

//		System.out.print(BibTexUtils.toBibtexString(bib));		
		assertEquals(expectedBibtex, BibTexUtils.toBibtexString(bib));

		// add some misc fields
		bib.addMiscField("extraKey", "extraVal");
		bib.addMiscField("extraKey2", "extraVal2");
		bib.setAbstract(null);

		final String expectedBibtex2 = 
			"@inproceedings{KIE,\n" +
			"  author = {Hans Dampf and Peter Silie},\n" +
			"  journal = {Journal of the most wonderful articles on earth},\n" +
			"  title = {The most wonderfult title on earth},\n" +
			"  volume = {3},\n" +
			"  year = {2525},\n" + 
			"  extraKey = {extraVal},\n" + 
			"  extraKey2 = {extraVal2}\n}";

//		System.out.println(BibTexUtils.toBibtexString(bib));
		assertEquals(expectedBibtex2, BibTexUtils.toBibtexString(bib));		
	}

	/**
	 * Prior to 2009-08-03, {@link BibTexUtils#MISC_FIELD_PATTERN} did not match
	 * on misc fields, which contained a line break. Thus, fields containing a
	 * line break got lost. Adding {@link Pattern#DOTALL} solved that problem.
	 * This test documents the solution 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testToBibtexString() throws Exception {
		final BibTex bib = new BibTex();

		bib.setYear("2004");
		bib.setTitle("La maladie d'Alzheimer au jour le jour : guide pratique pour les familles et tous ceux qui accompagnent au quotidien une personne touchée par la maladie d'Alzheimer");
		bib.setPrivnote("");
		bib.setNote("Tome I");
		bib.setMisc(
				"q6 = {It needs.\n" + 
				"To trials.\n" + 
				"Health rises.}, q7 = {Payment costs.}, q3b = {Establishment followed.}, q1e = {This \n" + 
				"Cost-effectiveness paper.}, q9 = {Payment costs.}, q1a = {Participation health. \n" + 
				"Maintenance age. \n" + 
				"Studies programs.}, q3a = {Reminder 2004). \n" + 
				"Preventive 2007). \n" + 
		"For not.}");
		bib.setEntrytype("book");
		bib.setEditor("John Libbey Eurotext");
		bib.setEdition("John Libbey Eurotext");
		bib.setBibtexKey("Selmes2004");
		bib.setAbstract("Le diagnostic de la maladie d'Alzheimer bouleverse la vie du patient mais aussi celle de ses proches, qui seront de plus en plus sollicités en qualité d'aidant. Ce guide permet de comprendre la maladie, son évolution et ses manifestations. Il aborde de façon concrète la gestion de la vie quotidienne, les problèmes de communication avec le malade et les moyens de l'améliorer, ainsi que les difficultés rencontrées par la personne aidante. Enfin, la question des structures d'accueil ou d'aides et les aspects légaux et financiers sont également abordés. Des contacts d'associations ou d'organismes et des sites Internet complètent le guide.");
		bib.setAuthor("Jacques Selmès and Christian Derouesné");


		final String expected = 
			"@book{Selmes2004,\n" + 
			"  author = {Jacques Selmès and Christian Derouesné},\n" +
			"  edition = {John Libbey Eurotext},\n" +
			"  editor = {John Libbey Eurotext},\n" +
			"  note = {Tome I},\n" +
			"  title = {La maladie d'Alzheimer au jour le jour : guide pratique pour les familles et tous ceux qui accompagnent au quotidien une personne touchée par la maladie d'Alzheimer},\n" +
			"  year = {2004},\n" +
			"  q6 = {It needs.\n" +
			"To trials.\n" +
			"Health rises.},\n" +
			"  q3a = {Reminder 2004). \n" +
			"Preventive 2007). \n" +
			"For not.},\n" +
			"  q7 = {Payment costs.},\n" +
			"  q3b = {Establishment followed.},\n" +
			"  q1e = {This \n" + 
			"Cost-effectiveness paper.},\n" +
			"  q9 = {Payment costs.},\n" +
			"  q1a = {Participation health. \n" +
			"Maintenance age. \n" +
			"Studies programs.},\n" +
			"  abstract = {Le diagnostic de la maladie d'Alzheimer bouleverse la vie du patient mais aussi celle de ses proches, qui seront de plus en plus sollicités en qualité d'aidant. Ce guide permet de comprendre la maladie, son évolution et ses manifestations. Il aborde de façon concrète la gestion de la vie quotidienne, les problèmes de communication avec le malade et les moyens de l'améliorer, ainsi que les difficultés rencontrées par la personne aidante. Enfin, la question des structures d'accueil ou d'aides et les aspects légaux et financiers sont également abordés. Des contacts d'associations ou d'organismes et des sites Internet complètent le guide.}\n" +
			"}";

		assertEquals(expected, BibTexUtils.toBibtexString(bib));

	}


	/**
	 * tests generateBibtexKey
	 */
	@Test
	public void generateBibtexKey() {
		assertEquals("dampf", BibTexUtils.generateBibtexKey("Hans Dampf", null, null, null));
		assertEquals("dampf", BibTexUtils.generateBibtexKey("Hans Dampf and Reiner Zufall", null, null, null));
		assertEquals("dampf", BibTexUtils.generateBibtexKey("Hans Dampf and Reiner Zufall", "Peter Silie", null, null));
		assertEquals("dampf2005", BibTexUtils.generateBibtexKey("Hans Dampf and Reiner Zufall", "Peter Silie", "2005", null));
		assertEquals("silie", BibTexUtils.generateBibtexKey(null, "Peter Silie", null, null));
		assertEquals("silie", BibTexUtils.generateBibtexKey(null, "Peter Silie and Hans Dampf", null, null));
		assertEquals("silie2005", BibTexUtils.generateBibtexKey(null, "Peter Silie and Hans Dampf", "2005", null));
	}

	/**
	 * tests getFirstPersonsLastName
	 */
	@Test
	public void getFirstPersonsLastName() {
		assertNull(BibTexUtils.getFirstPersonsLastName(null));
		assertEquals("Dampf", BibTexUtils.getFirstPersonsLastName("Hans Dampf"));
		assertEquals("Dampf", BibTexUtils.getFirstPersonsLastName("Hans Dampf and Reiner Zufall"));
		// XXX: this should be "Dampf" instead of "Zufall"
		assertEquals("Zufall", BibTexUtils.getFirstPersonsLastName("Hans Dampf, Reiner Zufall"));
	}

	/**
	 * tests cleanBibTex
	 */
	@Test
	public void cleanBibTex() {
		assertEquals("M&#252;ller", BibTexUtils.cleanBibTex("M{\\\"u}ller"));
		assertEquals("M&#252;ller", BibTexUtils.cleanBibTex("M\\\"{u}ller"));
		assertEquals("M&#252;ller", BibTexUtils.cleanBibTex("M\"uller"));
		assertEquals("http://bla.fasel", BibTexUtils.cleanBibTex("\\url{http://bla.fasel}"));
	}

	/**
	 * tests getYear
	 */
	@Test
	public void getYear() {
		assertEquals(2005, BibTexUtils.getYear("2005"));
		assertEquals(2005, BibTexUtils.getYear("test 2005 test"));
		assertEquals(2005, BibTexUtils.getYear("test2005test"));
		assertEquals(Integer.MAX_VALUE, BibTexUtils.getYear("no year in this string"));
	}

	/**
	 * tests sortBibTexList
	 */
	@Ignore
	// FIXME: implement me...
	public void sortBibTexList() {
		final List<Post<BibTex>> posts = new ArrayList<Post<BibTex>>();
		final Post<BibTex> post1 = new Post<BibTex>();
		final Post<BibTex> post2 = new Post<BibTex>();
		BibTex b1 = new BibTex();
		b1.setAuthor("A. Test");
		post1.setResource(b1);
		BibTex b2 = new BibTex();
		b2.setAuthor("B. Test");
		post2.setResource(b2);
		posts.add(post1);
		posts.add(post2);
		assertEquals("A. Test", posts.get(0).getResource().getAuthor());
		assertEquals("B. Test", posts.get(1).getResource().getAuthor());
		BibTexUtils.sortBibTexList(posts, Arrays.asList(SortKey.AUTHOR), Arrays.asList(SortOrder.ASC));
		assertEquals("A. Test", posts.get(0).getResource().getAuthor());
		assertEquals("B. Test", posts.get(1).getResource().getAuthor());
		BibTexUtils.sortBibTexList(posts, Arrays.asList(SortKey.AUTHOR), Arrays.asList(SortOrder.DESC));
		assertEquals("A. Test", posts.get(0).getResource().getAuthor());
		assertEquals("B. Test", posts.get(1).getResource().getAuthor());
	}

	/**
	 * tests removeDuplicates
	 */
	@Test
	public void removeDuplicates() {
		final BibTex bibtex = new BibTex();
		bibtex.setInterHash("test");
		final Post<BibTex> post1 = new Post<BibTex>();
		post1.setResource(bibtex);
		final Post<BibTex> post2 = new Post<BibTex>();
		post2.setResource(bibtex);
		final List<Post<BibTex>> posts = new ArrayList<Post<BibTex>>();
		posts.add(post1);
		posts.add(post2);

		assertEquals(2, posts.size());
		BibTexUtils.removeDuplicates(posts);
		assertEquals(1, posts.size());
	}

	/**
	 * tests serializeMiscFields
	 */
	@Test
	public void serializeMiscFields() {
		final BibTex bib = new BibTex();
		BibTexUtils.serializeMiscFields(bib);
		assertEquals("", bib.getMisc());
		// add misc field, check if it is correctly serialized
		bib.addMiscField("key1", "value1");
		BibTexUtils.serializeMiscFields(bib);
		assertEquals("  key1 = {value1}", bib.getMisc());
		// reset, modify misc fields, re-check
		bib.addMiscField("key1", "anotherValue1");
		BibTexUtils.serializeMiscFields(bib);
		assertEquals("  key1 = {anotherValue1}", bib.getMisc());
		//try the other way round (parse the serialized stuff)
		bib.addMiscField("key1", "value1");
		bib.addMiscField("key2", "value2");
		BibTexUtils.serializeMiscFields(bib);
		bib.getMiscFields().clear();
		BibTexUtils.parseMiscField(bib);
//		System.out.println(bib.getMisc());
		assertEquals(2, bib.getMiscFields().values().size());
		assertEquals("value1", bib.getMiscField("key1"));
		assertEquals("value2", bib.getMiscField("key2"));
	}

	/**
	 * Tests that toBibtexString() does not add misc fields to the post.
	 */
	@Test
	public void toBibtexString2() {
		final BibTex bib = new BibTex();
		bib.setEntrytype("inproceedings");
		bib.setBibtexKey("KIE");
		bib.setTitle("The most wonderfult title on earth");
		bib.setAuthor("Hans Dampf and Peter Silie");
		bib.setJournal("Journal of the most wonderful articles on earth");
		bib.setYear("2525");
		bib.setVolume("3");
		bib.setAbstract("This is a nice abstract.");
		bib.setPrivnote("This is private!");

		final String originalMisc = "doi = {my doi}, isbn = {999-12345-123-x}, vgwort = {12}";
		final String cleanedMisc  = 
			"  isbn = {999-12345-123-x},\n" +
			"  vgwort = {12},\n" +
			"  doi = {my doi}";
		bib.setMisc(originalMisc);

		final Post<BibTex> post = new Post<BibTex>();
		post.setResource(bib);
		post.setDescription("Eine feine kleine Beschreibung.");
		post.addTag("foo");
		post.addTag("bar");
		post.addTag("blubb");
		post.addTag("babba");

		/*
		 * Create a bibtex string - the method adds and removes some
		 * misc fields! Nevertheless, we should have the same misc fields
		 * afterwards. 
		 */
		BibTexUtils.toBibtexString(post);
		/*
		 * The fields are parsed and then serialized. Inbetween, some fields
		 * have been added (keywords, description). We must ensure, that they're
		 * removed again such that we have the original misc fields!
		 */
		assertEquals(cleanedMisc, bib.getMisc());
	}


}