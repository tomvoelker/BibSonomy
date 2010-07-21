/**
 *  
 *  BibSonomy-BibTeX-Parser - BibTeX Parser from
 * 		http://www-plan.cs.colorado.edu/henkel/stuff/javabib/
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.bibtex.parser;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Test;

/**
 * XXX: Since this class extends {@link SimpleBibTeXParserTest}, JUnit runs the
 * test from there twice. Not so nice. :-(
 * 
 * @author rja
 * @version $Id$
 */
public class PostBibTeXParserTest extends SimpleBibTeXParserTest {

	/**
	 * Parses a BibTeX string and checks the created post.
	 * 
	 * Then, creates a BibTeX string from the created post, parses it and checks
	 * the newly created post against the original post.
	 * @throws Exception 
	 */
	@Test
	public void testParseBibTeXPost() throws Exception {
		final String bibtex = "@inproceedings{benz2009managing,\n" + 
		"address = {New York, NY, USA},\n" +
		"author = {Dominik Benz and Folke Eisterlehner and Andreas Hotho and Robert Jäschke and Beate Krause and Gerd Stumme},\n" +
		"booktitle = {HT '09: Proceedings of the 20th ACM Conference on Hypertext and Hypermedia},\n" +
		"editor = {Ciro Cattuto and Giancarlo Ruffo and Filippo Menczer},\n" +
		"interHash = {aa341801cf9a31d963fccb8a331043dc},\n" +
		"intraHash = {99cafad8ce2afb5879c6c85c14cc5259},\n" +
		"pages = {323--324},\n" +
		"publisher = {ACM},\n" +
		"title = {Managing publications and bookmarks with BibSonomy},\n" +
		"url = {http://portal.acm.org/citation.cfm?doid=1557914.1557969#},\n" +
		"year = {2009},\n" +
		"date = \"2010-07-19\",\n" + 
		"isbn = {978-1-60558-486-7},\n" +
		"doi = {10.1145/1557914.1557969},\n" +
		"month = jun,\n" +
		"comment = {(private-note)This is a test note!},\n" + 
		"abstract = {In this demo we present BibSonomy, a social bookmark and publication sharing system.},\n" +
		"biburl = {http://www.bibsonomy.org/bibtex/299cafad8ce2afb5879c6c85c14cc5259/jaeschke},\n" + 
		"keywords = {2009 bibsonomy demo ht09 myown},\n" +
		"description = {Our demo at HT 2009},\n" + 
		"}";

		final PostBibTeXParser parser = new PostBibTeXParser();

		final Post<BibTex> post = parser.parseBibTeXPost(bibtex);
		/*
		 * check the post
		 */
		final BibTex resource = post.getResource();

		resource.recalculateHashes();
		assertEquals("New York, NY, USA", resource.getAddress());
		assertEquals("Dominik Benz and Folke Eisterlehner and Andreas Hotho and Robert Jäschke and Beate Krause and Gerd Stumme", resource.getAuthor());
		assertEquals("HT '09: Proceedings of the 20th ACM Conference on Hypertext and Hypermedia", resource.getBooktitle());
		assertEquals("Ciro Cattuto and Giancarlo Ruffo and Filippo Menczer", resource.getEditor());
		assertEquals("aa341801cf9a31d963fccb8a331043dc", resource.getInterHash());
		assertEquals("99cafad8ce2afb5879c6c85c14cc5259", resource.getIntraHash());
		assertEquals("323--324", resource.getPages());
		assertEquals("ACM", resource.getPublisher());
		assertEquals("Managing publications and bookmarks with BibSonomy", resource.getTitle());
		assertEquals("http://portal.acm.org/citation.cfm?doid=1557914.1557969#", resource.getUrl());
		assertEquals("2009", resource.getYear());
		assertEquals("978-1-60558-486-7", resource.getMiscField("isbn"));
		assertEquals("10.1145/1557914.1557969", resource.getMiscField("doi"));
		/*
		 * The URL is stored in the url field of the resource - should not be contained in the misc fields!
		 */
		assertEquals(null, resource.getMiscField("url"));
		/*
		 * CiteULike uses the "comment" field to export (private) notes in the form
		 * 
		 * comment = {(private-note)This is a test note!}, 
		 * 
		 */
		assertEquals("This is a test note!", resource.getPrivnote());
		
		/*
		 * If we don't turn expansion of months off (in the 
		 * MacroReferenceExpander), the parser will change this to "June".
		 */
		assertEquals("jun", resource.getMonth());
		assertEquals("In this demo we present BibSonomy, a social bookmark and publication sharing system.", resource.getAbstract());

		/*
		 * post's fields
		 */
		/*
		 * description 
		 */
		assertEquals("Our demo at HT 2009", post.getDescription());
		/*
		 * tags
		 */
		final Set<Tag> tags = new HashSet<Tag>();
		for (final String tag : "2009 bibsonomy demo ht09 myown".split(" ")) {
			tags.add(new Tag(tag));
		}
		assertEquals(tags, post.getTags());

		/*
		 * second step: create BibTeX from the post, parse it and compare
		 * the created post with the original post
		 */
		final Post<BibTex> secondParsedPost = parser.parseBibTeXPost(BibTexUtils.toBibtexString(post));
		secondParsedPost.getResource().recalculateHashes();

		ModelUtils.assertPropertyEquality(post, secondParsedPost, 5, null, new String[]{"date"});
	}

	@Test
	public void testUpdateWithParsedBibTeX() throws Exception {
		final BibTex bib = getExampleBibtex();
		final Post<BibTex> post = getExamplePost(bib);

		final PostBibTeXParser parser = new PostBibTeXParser();
		/*
		 * the resource is exchanged by a parsed version
		 */
		parser.updateWithParsedBibTeX(post);

		ModelUtils.assertPropertyEquality(bib, post.getResource(), 5, null, new String[]{});
	}



	private Post<BibTex> getExamplePost(final BibTex bib) {
		final Post<BibTex> post = new Post<BibTex>();
		post.setResource(bib);
		post.setDescription("Eine feine kleine Beschreibung.");
		post.addTag("foo");
		post.addTag("bar");
		post.addTag("blubb");
		post.addTag("babba");
		return post;
	}



	@Test
	public void testGetParsedCopy() throws Exception {
		final Post<BibTex> post = getExamplePost(getExampleBibtex());

		final PostBibTeXParser parser = new PostBibTeXParser();

		final Post<BibTex> parsedCopy = parser.getParsedCopy(post);

		ModelUtils.assertPropertyEquality(post, parsedCopy, 5, null, new String[]{});

		/*
		 * The misc field is parsed and then serialized back again also in
		 * the original post! Thus, we here manually check if no additional
		 * fields were added.
		 */
		assertEquals(
				"  isbn = {999-12345-123-x},\n" +
				"  vgwort = {12},\n" + 
				"  doi = {my doi}", 
				parsedCopy.getResource().getMisc());
	}

	/**
	 * Checks that misc fields are initialized correctly.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMisc() throws Exception {
		final PostBibTeXParser parser = new PostBibTeXParser();

		final Post<BibTex> post = parser.parseBibTeXPost("@article{jaeschke2006social,\ntitle={Social Foo},\nauthor={Robert Jäschke}\n}");

		post.getResource().recalculateHashes();
		post.setUser(new User("rja"));

		// TODO: asserts!!
	}

}
