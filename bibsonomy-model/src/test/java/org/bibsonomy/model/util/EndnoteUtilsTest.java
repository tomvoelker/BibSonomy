package org.bibsonomy.model.util;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.Assert;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.junit.Test;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class EndnoteUtilsTest {
	private static final String expected = "%0 Journal Article\n" + //
			"%1 schoolofenglishliterature2009newcastle\n" + //
			"%A School of English Literature, Language and Linguistics, \n" + //
			"%D 2009\n" + //
			"%E Günter, Günter\n" + //
			"%K test\n" + //
			"%T Newcastle working papers in linguistics\n";

	@Test
	public void testIt() throws IOException {
		Post<BibTex> post = createPost();
		String rendered = EndnoteUtils.toEndnoteString(post);
		Assert.assertEquals(expected, rendered);
	}

	public Post<BibTex> createPost() {
		BibTex b = new BibTex();
		b.setEntrytype("article");
		b.setAuthor(new ArrayList<PersonName>());
		b.setTitle("Newcastle working papers in linguistics");
		// TODO: what about this?: b.setTitle("Newcastle working papers in linguistics \\& stuff");
		b.getAuthor().add(new PersonName("", "{School of English Literature, Language and Linguistics}"));
		b.setEditor(new ArrayList<PersonName>());
		b.getEditor().add(new PersonName("G\"unter", "Günter"));
		b.setYear("{2009}");
		b.setBibtexKey("schoolofenglishliterature2009newcastle");
		Post<BibTex> post = new Post<BibTex>();
		post.setResource(b);
		post.addTag("test");
		return post;
	}
}
