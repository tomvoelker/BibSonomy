package org.bibsonomy.bibtex.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashSet;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Test;

import bibtex.parser.ParseException;

/**
 * @author rja
 * @version $Id$
 */
public class PostBibTeXParserTest {

	/**
	 * Parses a BibTeX string and checks the created post.
	 * 
	 * Then, creates a BibTeX string from the created post, parses it and checks
	 * the newly created post against the original post.
	 */
	@Test
	public void testParseBibTeXPost() {
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
		"isbn = {978-1-60558-486-7},\n" +
		"doi = {10.1145/1557914.1557969},\n" +
		"month = jun,\n" +
		"abstract = {In this demo we present BibSonomy, a social bookmark and publication sharing system.},\n" +
		"biburl = {http://www.bibsonomy.org/bibtex/299cafad8ce2afb5879c6c85c14cc5259/jaeschke},\n" + 
		"keywords = {2009 bibsonomy demo ht09 myown},\n" +
		"description = {Our demo at HT 2009},\n" + 
		"}";

		final PostBibTeXParser parser = new PostBibTeXParser();

		try {
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
			final HashSet<Tag> tags = new HashSet<Tag>();
			for (final String tag:"2009 bibsonomy demo ht09 myown".split(" ")) {
				tags.add(new Tag(tag));
			}
			assertEquals(tags, post.getTags());

			/*
			 * second step: create BibTeX from the post, parse it and compare
			 * the created post with the original post
			 */
			final Post<BibTex> secondParsedPost = parser.parseBibTeXPost(BibTexUtils.toBibtexString(post));
			secondParsedPost.getResource().recalculateHashes();
			
			ModelUtils.assertPropertyEquality(post, secondParsedPost, 5, null, new String[]{});

			
		} catch (ParseException ex) {
			fail(ex.getMessage());
		} catch (IOException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void testUpdateWithParsedBibTeX() {
//		fail("Not yet implemented");
	}

	@Test
	public void testGetParsedCopy() {
//		fail("Not yet implemented");
	}

}
