package org.bibsonomy.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class PostTest {

	/**
	 * tests addTag
	 */
	@Test
	public void addTag() {
		Post<BibTex> post = new Post<BibTex>();
		assertEquals(0, post.getTags().size());
		post.addTag("tag1");
		post.addTag("tag2");
		assertEquals(2, post.getTags().size());

		// don't call getTags before addTag
		post = new Post<BibTex>();
		post.addTag("tag1");
		post.addTag("tag2");
		assertEquals(2, post.getTags().size());
	}

	/**
	 * tests addGroup
	 */
	@Test
	public void addGroup() {
		Post<BibTex> post = new Post<BibTex>();
		assertEquals(0, post.getGroups().size());
		post.addGroup("testgroup1");
		post.addGroup("testgroup2");
		assertEquals(2, post.getGroups().size());

		// don't call getGroups before addGroup
		post = new Post<BibTex>();
		post.addGroup("testgroup1");
		post.addGroup("testgroup2");
		assertEquals(2, post.getGroups().size());
	}
}