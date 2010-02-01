package org.bibsonomy.services;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class URLGeneratorTest {

	private static URLGenerator ug = new URLGenerator("http://www.bibsonomy.org/");
	
	@Test
	public void testGetPostUrl() {
		final Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		assertEquals("http://www.bibsonomy.org/bibtex/" + HashID.INTRA_HASH.getId() + post.getResource().getIntraHash() + "/" + post.getUser().getName(), ug.getPublicationUrl(post.getResource(), post.getUser()).toString());
		final Post<Bookmark> bPost = ModelUtils.generatePost(Bookmark.class);
		assertEquals("http://www.bibsonomy.org/url/" + bPost.getResource().getIntraHash() + "/" + bPost.getUser().getName(), ug.getPostUrl(bPost).toString());

	}

	@Test
	public void testGetPublicationUrl() {
		final Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		assertEquals("http://www.bibsonomy.org/bibtex/" + HashID.INTRA_HASH.getId() + post.getResource().getIntraHash() + "/" + post.getUser().getName(), ug.getPublicationUrl(post.getResource(), post.getUser()).toString());
	}

	@Test
	public void testGetUserUrl() {
		assertEquals("http://www.bibsonomy.org/user/jaeschke", ug.getUserUrl(new User("jaeschke")).toString());
	}

}
