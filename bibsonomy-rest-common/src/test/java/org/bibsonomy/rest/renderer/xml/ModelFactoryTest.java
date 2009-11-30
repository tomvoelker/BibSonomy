/**
 *  
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
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

package org.bibsonomy.rest.renderer.xml;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
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
			if (!(this.XML_IS_INVALID_MSG + "username is missing in element 'user'").equals(e.getMessage())) fail("wrong exception thrown: " + e.getMessage());
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
			if (!(this.XML_IS_INVALID_MSG + "groupname is missing in element 'group'").equals(e.getMessage())) fail("wrong exception thrown: " + e.getMessage());
		}

		// check valid group
		xmlGroup.setName("test");
		xmlGroup.setRealname("TestGroup");
		xmlGroup.setHomepage("http://www.example.com/");
		final Group group = this.modelFactory.createGroup(xmlGroup);
		assertTrue("model not correctly initialized", "test".equals(group.getName()));
		assertTrue("model not correctly initialized", "http://www.example.com/".equals(group.getHomepage().toString()));
		assertTrue("model not correctly initialized", "TestGroup".equals(group.getRealname()));
	}

	@Test
	public void testCreateTag() {
		// check invalid tag
		final TagType xmlTag = new TagType();
		try {
			this.modelFactory.createTag(xmlTag);
		} catch (InvalidModelException e) {
			if (!(this.XML_IS_INVALID_MSG + "tag name is missing in element 'tag'").equals(e.getMessage())) fail("wrong exception thrown: " + e.getMessage());
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
	public void testCreatePost() throws DatatypeConfigurationException {
		// check invalid posts
		final PostType xmlPost = new PostType();
		DatatypeFactory dataFact = DatatypeFactory.newInstance();		
		xmlPost.setPostingdate(dataFact.newXMLGregorianCalendar("2008-12-04T10:42:06.000+01:00"));		
		checkInvalidPost(xmlPost, this.XML_IS_INVALID_MSG + "no tags specified");
		final TagType xmlTag = new TagType();
		xmlPost.getTag().add(xmlTag);
		checkInvalidPost(xmlPost, this.XML_IS_INVALID_MSG + "user is missing");
		final UserType xmlUser = new UserType();
		xmlUser.setName("tuser");
		xmlPost.setUser(xmlUser);
		checkInvalidPost(xmlPost, this.XML_IS_INVALID_MSG + "resource is missing inside element 'post'");
		final BookmarkType xmlBookmark = new BookmarkType();
		xmlPost.setBookmark(xmlBookmark);
		checkInvalidPost(xmlPost, this.XML_IS_INVALID_MSG + "tag name is missing in element 'tag'");
		xmlTag.setName("testtag");
		checkInvalidPost(xmlPost, this.XML_IS_INVALID_MSG + "url is missing in element 'bookmark'");
		xmlBookmark.setUrl("http://www.google.de");
		xmlBookmark.setTitle("Google search engine");
		xmlPost.setBookmark(xmlBookmark);
		xmlPost.setBibtex(new BibtexType());
		checkInvalidPost(xmlPost, this.XML_IS_INVALID_MSG + "only one resource type is allowed inside element 'post'");
		xmlPost.setBibtex(null);

		// check valid post with bookmark
		Post<? extends Resource> post = modelFactory.createPost(xmlPost);
		assertTrue("model not correctly initialized", "tuser".equals(post.getUser().getName()));
		assertTrue("model not correctly initialized", post.getResource() instanceof Bookmark);
		assertTrue("model not correctly initialized", "http://www.google.de".equals(((Bookmark) post.getResource()).getUrl()));
		assertTrue("model not correctly initialized", "testtag".equals(post.getTags().iterator().next().getName()));

		xmlPost.setBookmark(null);
		final BibtexType xmlBibtex = new BibtexType();
		xmlPost.setBibtex(xmlBibtex);
		checkInvalidPost(xmlPost, this.XML_IS_INVALID_MSG + "title is missing in element 'bibtex'");
		xmlBibtex.setTitle("foo bar");
		xmlBibtex.setYear("2005");
		xmlBibtex.setBibtexKey("myBibtexKey");
		xmlBibtex.setEntrytype("inproceedings");
		xmlBibtex.setAuthor("Hans Dampf");

		// check valid post with bibtex
		post = this.modelFactory.createPost(xmlPost);
		assertTrue("model not correctly initialized", "tuser".equals(post.getUser().getName()));
		assertTrue("model not correctly initialized", post.getResource() instanceof BibTex);
		assertTrue("model not correctly initialized", "foo bar".equals(((BibTex) post.getResource()).getTitle()));
		assertTrue("model not correctly initialized", "testtag".equals(post.getTags().iterator().next().getName()));
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