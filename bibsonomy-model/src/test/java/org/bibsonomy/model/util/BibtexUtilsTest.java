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

		final String expectedBibtex = 
			"@inproceedings{KIE,\n" +
			"author = {Hans Dampf and Peter Silie},\n" +
			"journal = {Journal of the most wonderful articles on earth},\n" +
			"title = {The most wonderfult title on earth},\n" +
			"volume = {3},\n" +
			"year = {2525}\n}";

		assertEquals(expectedBibtex, BibTexUtils.toBibtexString(bib));
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
}