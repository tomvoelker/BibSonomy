/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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
		bib.setBibtexAbstract("This is a nice abstract.");

		final String expectedBibtex = 
			"@inproceedings{KIE,\n" +			
			"author = {Hans Dampf and Peter Silie},\n" +
			"journal = {Journal of the most wonderful articles on earth},\n" +
			"title = {The most wonderfult title on earth},\n" +
			"volume = {3},\n" +
			"year = {2525},\n" +
			"abstract = {This is a nice abstract.},\n}";
				
		System.out.print(BibTexUtils.toBibtexString(bib));
		assertEquals(expectedBibtex, BibTexUtils.toBibtexString(bib));
					
		// add some misc fields
		bib.addMiscField("extraKey", "extraVal");
		bib.addMiscField("extraKey2", "extraVal2");
		bib.setBibtexAbstract(null);
				
		final String expectedBibtex2 = 
			"@inproceedings{KIE,\n" +
			"author = {Hans Dampf and Peter Silie},\n" +
			"journal = {Journal of the most wonderful articles on earth},\n" +
			"title = {The most wonderfult title on earth},\n" +
			"volume = {3},\n" +
			"year = {2525},\n" + 
			"extraKey = {extraVal}, extraKey2 = {extraVal2}\n}";
		
		assertEquals(expectedBibtex2, BibTexUtils.toBibtexString(bib));		
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
		assertEquals("key1 = {value1}", bib.getMisc());
		// reset, modify misc fields, re-check
		bib.addMiscField("key1", "anotherValue1");
		BibTexUtils.serializeMiscFields(bib);
		assertEquals("key1 = {anotherValue1}", bib.getMisc());
		//try the other way round (parse the serialized stuff)
		bib.addMiscField("key1", "value1");
		bib.addMiscField("key2", "value2");
		BibTexUtils.serializeMiscFields(bib);
		bib.getMiscFields().clear();
		BibTexUtils.parseMiscField(bib);
		System.out.println(bib.getMisc());
		assertEquals(2, bib.getMiscFields().values().size());
		assertEquals("value1", bib.getMiscField("key1"));
		assertEquals("value2", bib.getMiscField("key2"));
	}
	
	
}