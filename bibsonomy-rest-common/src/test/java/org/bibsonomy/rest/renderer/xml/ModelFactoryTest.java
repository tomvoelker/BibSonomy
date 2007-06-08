package org.bibsonomy.rest.renderer.xml;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ModelFactoryTest {

	private ModelFactory modelFactory;
	private final String XML_IS_INVALID_MSG = "The body part of the received XML document is not valid: ";

	@Before
	public void setUp() {
		this.modelFactory = ModelFactory.getInstance();
	}

	@Test
	public void testCreateUser() {
		// check invalid user
		final UserType xmlUser = new UserType();
		try {
			this.modelFactory.createUser(xmlUser);
			fail("exception should have been thrown.");
		} catch (final InvalidModelException e) {
			if (!(this.XML_IS_INVALID_MSG + "username is missing").equals(e.getMessage())) fail("wrong exception thrown: " + e.getMessage());
		}

		// check valid user
		xmlUser.setName("test");
		final User user = this.modelFactory.createUser(xmlUser);
		assertTrue("model not correctly initialized", "test".equals(user.getName()));
	}

	@Test
	public void testCreateGroup() {
		// check invalid group
		final GroupType xmlGroup = new GroupType();
		try {
			this.modelFactory.createGroup(xmlGroup);
			fail("exception should have been thrown.");
		} catch (final InvalidModelException e) {
			if (!(this.XML_IS_INVALID_MSG + "groupname is missing").equals(e.getMessage())) fail("wrong exception thrown: " + e.getMessage());
		}

		// check valid group
		xmlGroup.setName("test");
		final Group group = this.modelFactory.createGroup(xmlGroup);
		assertTrue("model not correctly initialized", "test".equals(group.getName()));
	}

	@Test
	public void testCreateTag() {
		// check invalid tag
		final TagType xmlTag = new TagType();
		try {
			this.modelFactory.createTag(xmlTag);
		} catch (InvalidModelException e) {
			if (!(this.XML_IS_INVALID_MSG + "tag name is missing").equals(e.getMessage())) fail("wrong exception thrown: " + e.getMessage());
		}

		// check valid tag
		xmlTag.setName("foo");
		Tag tag = this.modelFactory.createTag(xmlTag);
		assertTrue("tag not correctly initailized", "foo".equals(tag.getName()));
		xmlTag.setGlobalcount(BigInteger.ONE);
		xmlTag.setUsercount(BigInteger.TEN);
		tag = this.modelFactory.createTag(xmlTag);
		assertTrue("tag not correctly initailized", tag.getGlobalcount() == 1);
		assertTrue("tag not correctly initailized", tag.getUsercount() == 10);
	}

	@Test
	public void testCreatePost() {
		// check invalid posts
		final PostType xmlPost = new PostType();
		checkInvalidPost(xmlPost, this.XML_IS_INVALID_MSG + "no tags specified");
		final TagType xmlTag = new TagType();
		xmlPost.getTag().add(xmlTag);
		checkInvalidPost(xmlPost, this.XML_IS_INVALID_MSG + "user is missing");
		final UserType xmlUser = new UserType();
		xmlUser.setName("tuser");
		xmlPost.setUser(xmlUser);
		checkInvalidPost(xmlPost, this.XML_IS_INVALID_MSG + "resource is missing");
		final BookmarkType xmlBookmark = new BookmarkType();
		xmlPost.setBookmark(xmlBookmark);
		checkInvalidPost(xmlPost, this.XML_IS_INVALID_MSG + "tag name is missing");
		xmlTag.setName("testtag");
		checkInvalidPost(xmlPost, this.XML_IS_INVALID_MSG + "url is missing");
		xmlBookmark.setUrl("http://www.google.de");
		xmlPost.setBibtex(new BibtexType());
		checkInvalidPost(xmlPost, this.XML_IS_INVALID_MSG + "only one resource is allowed");
		xmlPost.setBibtex(null);

		// check valid post with bookmark
		Post post = modelFactory.createPost(xmlPost);
		assertTrue("model not correctly initialized", "tuser".equals(post.getUser().getName()));
		assertTrue("model not correctly initialized", post.getResource() instanceof Bookmark);
		assertTrue("model not correctly initialized", "http://www.google.de".equals(((Bookmark) post.getResource()).getUrl()));
		assertTrue("model not correctly initialized", "testtag".equals(((Tag) post.getTags().iterator().next()).getName()));

		xmlPost.setBookmark(null);
		final BibtexType xmlBibtex = new BibtexType();
		xmlPost.setBibtex(xmlBibtex);
		checkInvalidPost(xmlPost, this.XML_IS_INVALID_MSG + "title is missing");
		xmlBibtex.setTitle("foo bar");

		// check valid post with bibtex
		post = this.modelFactory.createPost(xmlPost);
		assertTrue("model not correctly initialized", "tuser".equals(post.getUser().getName()));
		assertTrue("model not correctly initialized", post.getResource() instanceof BibTex);
		assertTrue("model not correctly initialized", "foo bar".equals(((BibTex) post.getResource()).getTitle()));
		assertTrue("model not correctly initialized", "testtag".equals(((Tag) post.getTags().iterator().next()).getName()));
	}

	private void checkInvalidPost(final PostType xmlPost, final String exceptionMessage) {
		try {
			this.modelFactory.createPost(xmlPost);
			fail("exception should have been thrown.");
		} catch (final InvalidModelException e) {
			if (!e.getMessage().equals(exceptionMessage)) {
				System.out.println(e.getMessage());
				fail("wrong exception thrown: " + e.getMessage());
			}
		}
	}
}