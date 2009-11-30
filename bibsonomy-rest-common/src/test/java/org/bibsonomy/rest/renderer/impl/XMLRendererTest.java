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

package org.bibsonomy.rest.renderer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.bibsonomy.rest.renderer.xml.BookmarkType;
import org.bibsonomy.rest.renderer.xml.GroupType;
import org.bibsonomy.rest.renderer.xml.ObjectFactory;
import org.bibsonomy.rest.renderer.xml.PostType;
import org.bibsonomy.rest.renderer.xml.TagType;
import org.bibsonomy.rest.renderer.xml.UserType;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Manuel Bork
 * @author Christian Schenk
 * @version $Id$
 */
public class XMLRendererTest {

	private Renderer renderer;

	@Before
	public void setUp() {
		this.renderer = XMLRenderer.getInstance();
	}

	@Test
	public void testParseUser() throws Exception {
		// check null behavior
		try {
			this.renderer.parseUser(null);
			fail("exception should have been thrown.");
		} catch (final BadRequestOrResponseException e) {
		}

		// check empty/ wrong document
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile("bibsonomy", "junit");
		marshalToFile(bibXML, tmpFile);

		try {
			this.renderer.parseUser(new FileReader(tmpFile));
			fail("exception should have been thrown.");
		} catch (final BadRequestOrResponseException e) {
			if (!"The body part of the received document is erroneous - no user defined.".equals(e.getMessage())) fail("wrong exception thrown: " + e.getMessage());
		}

		// check valid user
		bibXML = new BibsonomyXML();
		final UserType xmlUser = new UserType();
		xmlUser.setName("test");
		bibXML.setUser(xmlUser);
		tmpFile = File.createTempFile("bibsonomy", "junit");
		marshalToFile(bibXML, tmpFile);
		final User user = this.renderer.parseUser(new FileReader(tmpFile));
		assertEquals("model not correctly initialized", "test", user.getName());
	}

	@Test
	public void testParseGroup() throws Exception {
		// check null behavior
		try {
			this.renderer.parseGroup(null);
			fail("exception should have been thrown.");
		} catch (final BadRequestOrResponseException e) {
		}

		// check empty/ wrong document
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile("bibsonomy", "junit");
		marshalToFile(bibXML, tmpFile);

		try {
			this.renderer.parseGroup(new FileReader(tmpFile));
			fail("exception should have been thrown.");
		} catch (final BadRequestOrResponseException e) {
			if (!"The body part of the received document is erroneous - no group defined.".equals(e.getMessage())) fail("wrong exception thrown: " + e.getMessage());
		}

		// check valid group
		bibXML = new BibsonomyXML();
		final GroupType xmlGroup = new GroupType();
		xmlGroup.setName("test");
		xmlGroup.setRealname("TestGroup");
		xmlGroup.setHomepage("http://www.example.com/");
		bibXML.setGroup(xmlGroup);
		tmpFile = File.createTempFile("bibsonomy", "junit");
		marshalToFile(bibXML, tmpFile);
		final Group group = this.renderer.parseGroup(new FileReader(tmpFile));
		assertEquals("model not correctly initialized", "test", group.getName());
		assertEquals("model not correctly initialized", "TestGroup", group.getRealname());
		assertEquals("model not correctly initialized", "http://www.example.com/", group.getHomepage().toString());
	}

	/**
	 * This is just a rudimentary test.<br/>
	 * 
	 * Tests of the created post object belong to
	 * {@link org.bibsonomy.rest.renderer.xml.ModelFactoryTest}
	 */
	@Test
	public void testParsePost() throws Exception {
		// check null behavior
		try {
			this.renderer.parsePost(null);
			fail("exception should have been thrown.");
		} catch (final BadRequestOrResponseException e) {
		}

		// check empty/ wrong document
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile("bibsonomy", "junit");
		marshalToFile(bibXML, tmpFile);

		try {
			this.renderer.parsePost(new FileReader(tmpFile));
			fail("exception should have been thrown.");
		} catch (BadRequestOrResponseException e) {
			if (!"The body part of the received document is erroneous - no post defined.".equals(e.getMessage())) {
				System.out.println(e.getMessage());
				fail("wrong exception thrown: " + e.getMessage());
			}
		}

		// check valid post
		bibXML = new BibsonomyXML();
		final PostType xmlPost = new PostType();
		final TagType xmlTag = new TagType();
		xmlPost.getTag().add(xmlTag);
		final UserType xmlUser = new UserType();
		xmlUser.setName("tuser");
		xmlPost.setUser(xmlUser);
		final BookmarkType xmlBookmark = new BookmarkType();
		xmlPost.setBookmark(xmlBookmark);
		DatatypeFactory dataFact = DatatypeFactory.newInstance();		
		xmlPost.setPostingdate(dataFact.newXMLGregorianCalendar("2008-12-04T10:42:06.000+01:00"));
		xmlTag.setName("testtag");
		xmlBookmark.setUrl("http://www.google.de");
		xmlBookmark.setTitle("Google Search engine");
		bibXML.setPost(xmlPost);
		tmpFile = File.createTempFile("bibsonomy", "junit");
		marshalToFile(bibXML, tmpFile);
		this.renderer.parsePost(new FileReader(tmpFile));
	}

	private void marshalToFile(final BibsonomyXML bibXML, final File tmpFile) throws JAXBException, PropertyException, FileNotFoundException {
		final JAXBContext jc = JAXBContext.newInstance("org.bibsonomy.rest.renderer.xml");
		final JAXBElement<BibsonomyXML> webserviceElement = new ObjectFactory().createBibsonomy(bibXML);
		final Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(webserviceElement, new FileOutputStream(tmpFile));
	}

	@Test
	public void testSerializeTags() throws Exception {
		StringWriter sw = new StringWriter(100);
		
		// empty list without start-/end-values 
		final LinkedList<Tag> tags = new LinkedList<Tag>();
		try {
			this.renderer.serializeTags(sw, tags, null);
			//fail("exception should have been thrown: no start-/end-values given");
		} catch (final InternServerException e) {
		}
		catch (final BadRequestOrResponseException e) {			
		}

		// empty list
		final ViewModel vm = new ViewModel();
		vm.setStartValue(0);
		vm.setEndValue(10);
		vm.setUrlToNextResources("http://www.bibsonomy.org/foo/bar");
		sw = new StringWriter(100);
		this.renderer.serializeTags(sw, tags, vm);
		compareWithFile(sw, "ExampleResultTags1.txt");

		// with tags
		sw = new StringWriter(100);
		final Tag tag1 = new Tag();
		tags.add(tag1);
		try {
			this.renderer.serializeTags(sw, tags, vm);
			fail("exception should have been thrown: no tagname specified");
		} catch (final InvalidModelException e) {
		}
		tag1.setName("foo");
		sw = new StringWriter(100);
		this.renderer.serializeTags(sw, tags, vm);
		compareWithFile(sw, "ExampleResultTags2.txt");

		// with multiple tags
		final Tag tag2 = new Tag();
		tag2.setName("bar");
		tag2.setUsercount(5);
		tag2.setGlobalcount(10);
		tags.add(tag2);
		sw = new StringWriter(100);
		this.renderer.serializeTags(sw, tags, vm);
		compareWithFile(sw, "ExampleResultTags3.txt");
	}

	@Test
	public void testSerializeTag() throws Exception {
		// empty tag
		final StringWriter sw = new StringWriter(100);
		final Tag tag = new Tag();
		try {
			this.renderer.serializeTag(sw, tag, null);
			fail("exception should have been thrown: no tagname specified");
		} catch (final InvalidModelException e) {
		}
		tag.setName("foo");
		this.renderer.serializeTag(sw, tag, null);
		compareWithFile(sw, "ExampleResultTag.txt");
	}

	@Test
	public void testSerializeUsers() throws Exception {
		StringWriter sw = new StringWriter(100);

		// empty user
		final LinkedList<User> users = new LinkedList<User>();
		try {
			this.renderer.serializeUsers(sw, users, null);
			// fail("exception should have been thrown: no start-/end values specified");
		} catch (final InternServerException e) {
		}
		catch (final BadRequestOrResponseException e) {			
		}		

		//
		final ViewModel vm = new ViewModel();
		vm.setStartValue(20);
		vm.setEndValue(30);
		vm.setUrlToNextResources("http://www.bibsonomy.org/api/foo/bar");
		final User user1 = new User();
		users.add(user1);
		try {
			this.renderer.serializeUsers(sw, users, null);
			fail("exception should have been thrown: no username specified");
		} catch (final InvalidModelException e) {
		}

		sw = new StringWriter(100);
		user1.setName("testName");
		user1.setEmail("mail@foo.bar");
		user1.setHomepage(new URL("http://foo.bar.com"));
		user1.setPassword("raboof");
		user1.setRealname("Dr. FOO BaR");
		final User user2 = new User();
		user2.setName("fooBar");
		user2.getGroups().add(new Group("kde"));
		users.add(user2);
		this.renderer.serializeUsers(sw, users, vm);
		System.out.println(sw.toString());
		compareWithFile(sw, "ExampleResultUsers1.txt");
	}

	@Test
	public void testSerializeUser() throws Exception {
		// empty user
		final StringWriter sw = new StringWriter(100);
		final User user = new User();
		try {
			this.renderer.serializeUser(sw, user, null);
			fail("exception should have been thrown: no username specified");
		} catch (final InvalidModelException e) {
		}
		user.setName("foo");
		this.renderer.serializeUser(sw, user, null);
		compareWithFile(sw, "ExampleResultUser.txt");
	}

	@Test
	public void testSerializeGroups() throws Exception {
		StringWriter sw = new StringWriter(100);

		// empty group
		final LinkedList<Group> groups = new LinkedList<Group>();		
		try {
			this.renderer.serializeGroups(sw, groups, null);
			//fail("exception should have been thrown: no start-/end values specified");
		}
		catch (InternServerException ex) {			
		}
		catch (final BadRequestOrResponseException e) {			
		}		

		// empty group
		final ViewModel vm = new ViewModel();
		vm.setStartValue(20);
		vm.setEndValue(30);
		vm.setUrlToNextResources("http://www.bibsonomy.org/api/foo/bar");
		final Group group1 = new Group();
		groups.add(group1);
		try {
			this.renderer.serializeGroups(sw, groups, null);
			fail("exception should have been thrown: no groupname specified");
		} catch (final InvalidModelException e) {
		}

		sw = new StringWriter(100);
		group1.setName("testName");
		group1.setDescription("foo bar ...");
		final Group group2 = new Group();
		group2.setName("testName2");
		groups.add(group2);
		this.renderer.serializeGroups(sw, groups, vm);
		compareWithFile(sw, "ExampleResultGroups1.txt");
	}

	@Test
	public void testSerializeGroup() throws Exception {
			
		// empty group
		final StringWriter sw = new StringWriter(100);
		final Group group = new Group();
		try {
			this.renderer.serializeGroup(sw, group, null);
			fail("exception should have been thrown: no groupname specified");
		} catch (final InvalidModelException e) {
		}
		group.setName("foo");
		group.setDescription("foo bar :)");
		group.setHomepage(new URL("http://www.example.com/"));
		group.setRealname("TestGroup");
		this.renderer.serializeGroup(sw, group, null);
		compareWithFile(sw, "ExampleResultGroup.txt");
	}

	@Test
	public void testSerializePosts() throws Exception {
		StringWriter sw = new StringWriter(100);
		final List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
		try {
			this.renderer.serializePosts(sw, posts, null);
			//fail ("Exception should have been trown: no start-/end-values specified");
		}
		catch (InternServerException ex) {			
		}
		catch (final BadRequestOrResponseException e) {			
		}		
		
		sw = new StringWriter(100);
		final ViewModel vm = new ViewModel();
		vm.setStartValue(0);
		vm.setEndValue(10);
		vm.setUrlToNextResources("www.bibsonomy.org/foo/bar");
		final Post<Resource> post = new Post<Resource>();
		final User user = new User();
		user.setName("foo");
		final Group group = new Group();
		group.setName("bar");
		final Tag tag = new Tag();
		tag.setName("foobar");
		post.setUser(user);
		post.getGroups().add(group);
		post.getTags().add(tag);
		post.setDate(new Date(System.currentTimeMillis()));
		final BibTex bibtex = new BibTex();
		bibtex.setTitle("foo and bar");
		bibtex.setIntraHash("abc");
		bibtex.setInterHash("abc");
		post.setResource(bibtex);
		posts.add(post);
		final Bookmark bookmark = new Bookmark();
		bookmark.setInterHash("12345678");
		bookmark.setIntraHash("12345678");
		bookmark.setUrl("www.foobar.de");
		bookmark.setTitle("bookmarktitle");
		final Post<Resource> post2 = new Post<Resource>();
		post2.setResource(bookmark);
		post2.setUser(user);
		post2.getTags().add(tag);
		post2.setDate(new Date(System.currentTimeMillis()));
		posts.add(post2);
		this.renderer.serializePosts(sw, posts, vm);
		compareWithFile(sw, "ExampleResultPosts.txt");
	}

	@Test
	public void testSerializePost() throws Exception {
		final StringWriter sw = new StringWriter(100);
		final Post<Resource> post = new Post<Resource>();
		try {
			this.renderer.serializePost(sw, post, null);
			fail("exception should have been thrown: no user specified");
		} catch (final InternServerException e) {
		}
		final User user = new User();
		user.setName("foo");
		post.setUser(user);
		try {
			this.renderer.serializePost(sw, post, null);
			fail("exception should have been thrown: no tags assigned");
		} catch (final InternServerException e) {
		}
		final Tag tag = new Tag();
		tag.setName("bar");
		post.getTags().add(tag);
		try {
			this.renderer.serializePost(sw, post, null);
			fail("exception should have been thrown: no ressource assigned");
		} catch (final InternServerException e) {
		}
		final Bookmark bookmark = new Bookmark();
		post.setResource(bookmark);
		post.setDate(new Date(System.currentTimeMillis()));
		try {
			this.renderer.serializePost(sw, post, null);
			fail("exception should have been thrown: bookmark has no url assigned");
		} catch (final InvalidModelException e) {
		}
		bookmark.setUrl("www.foobar.org");
		bookmark.setTitle("bookmarktitle");
		bookmark.setIntraHash("aabbcc");
		bookmark.setInterHash("1324356789");
		this.renderer.serializePost(sw, post, null);
		compareWithFile(sw, "ExampleResultPost.txt");
	}

	@Test
	public void testQuoting() throws IOException {
		final StringWriter sw = new StringWriter(100);
		final List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
		final Post<Resource> post = new Post<Resource>();
		posts.add(post);
		final User user = new User();
		user.setName("foo");
		post.setUser(user);
		final Tag tag = new Tag();
		tag.setName("bar");
		post.getTags().add(tag);
		post.setDate(new Date(System.currentTimeMillis()));
		final Bookmark bookmark = new Bookmark();
		post.setResource(bookmark);
		bookmark.setUrl("www.foobar.org");
		bookmark.setTitle("bookmarktitle");
		bookmark.setIntraHash("aabbcc");
		bookmark.setInterHash("1324356789");
		final ViewModel vm = new ViewModel();
		vm.setStartValue(0);
		vm.setEndValue(1);
		vm.setUrlToNextResources("http://foo.bar/posts?start=1&end=2&resourcetype=bookmark&tags=a+->b+<-c+<->d&hash=asd&&&kjalsjdf");
		this.renderer.serializePosts(sw, posts, vm);
		compareWithFile(sw, "QuotingTest.txt");
	}

	private void compareWithFile(final StringWriter sw, final String filename) throws IOException {
		final StringBuffer sb = new StringBuffer(200);
		final File file = new File("src/test/resources/xmlrenderer/" + filename);
		final BufferedReader br = new BufferedReader(new FileReader(file));
		String s;
		while ((s = br.readLine()) != null) {						
			sb.append(s + "\n");
		}
		// cut out postingdate as it changes each time
		String swWithoutPostingdate = sw.toString().replaceAll(" postingdate=\"[^\"]*\"", "");
		assertEquals("output not as expected", sb.toString(), swWithoutPostingdate.toString());
	}
}