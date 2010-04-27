/**
 *  
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.datatype.DatatypeFactory;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;
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
import org.bibsonomy.rest.renderer.xml.GoldStandardPublicationType;
import org.bibsonomy.rest.renderer.xml.GroupType;
import org.bibsonomy.rest.renderer.xml.ObjectFactory;
import org.bibsonomy.rest.renderer.xml.PostType;
import org.bibsonomy.rest.renderer.xml.ReferenceType;
import org.bibsonomy.rest.renderer.xml.ReferencesType;
import org.bibsonomy.rest.renderer.xml.TagType;
import org.bibsonomy.rest.renderer.xml.UserType;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Manuel Bork
 * @author Christian Schenk
 * @version $Id$
 */
public class XMLRendererTest {

	private static final String GOLD_STANDARD_PUBLICATION_ENTRYTYPE = "research report";
	private static final String GOLD_STANDARD_PUBLICATION_BIBTEX_KEY = "doe2004";
	private static final String GOLD_STANDARD_PUBLICATION_TITLE = "The ten famoust";
	private static final String GOLD_STANDARD_PUBLICATION_YEAR = "2004";
	private static final String GOLD_STANDARD_PUBLICATION_AUTHOR = "John Doe";
	
	private static final String PATH_TO_TEST_COMPARE_FILES = "src/test/resources/xmlrenderer/";
	
	private static Renderer renderer;

	@BeforeClass
	public static void setUp() {
		renderer = XMLRenderer.getInstance();
	}
	
	private static void compareWithFile(final Writer sw, final String filename) {
		final StringBuilder sb = new StringBuilder(200);
		final File file = new File(PATH_TO_TEST_COMPARE_FILES + filename);
		try {
			final BufferedReader br = new BufferedReader(new FileReader(file));
			String s;
			while ((s = br.readLine()) != null) {						
				sb.append(s + "\n");
			}
			// cut out postingdate as it changes each time
			final String swWithoutPostingdate = sw.toString().replaceAll(" postingdate=\"[^\"]*\"", "");
			assertEquals("output not as expected", sb.toString(), swWithoutPostingdate.toString());
		} catch (final IOException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void testParseUser() throws Exception {
		// check null behavior
		try {
			renderer.parseUser(null);
			fail("exception should have been thrown.");
		} catch (final BadRequestOrResponseException e) {
		}

		// check empty/ wrong document
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile("bibsonomy", "junit");
		marshalToFile(bibXML, tmpFile);

		try {
			renderer.parseUser(new FileReader(tmpFile));
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
		final User user = renderer.parseUser(new FileReader(tmpFile));
		assertEquals("model not correctly initialized", "test", user.getName());
	}

	@Test
	public void testParseGroup() throws Exception {
		// check null behavior
		try {
			renderer.parseGroup(null);
			fail("exception should have been thrown.");
		} catch (final BadRequestOrResponseException e) {
		}

		// check empty/ wrong document
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile("bibsonomy", "junit");
		marshalToFile(bibXML, tmpFile);

		try {
			renderer.parseGroup(new FileReader(tmpFile));
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
		final Group group = renderer.parseGroup(new FileReader(tmpFile));
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
			renderer.parsePost(null);
			fail("exception should have been thrown.");
		} catch (final BadRequestOrResponseException e) {
		}

		// check empty/ wrong document
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile("bibsonomy", "junit");
		marshalToFile(bibXML, tmpFile);

		try {
			renderer.parsePost(new FileReader(tmpFile));
			fail("exception should have been thrown.");
		} catch (final BadRequestOrResponseException e) {
			if (!"The body part of the received document is erroneous - no post defined.".equals(e.getMessage())) {
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
		final DatatypeFactory dataFact = DatatypeFactory.newInstance();		
		xmlPost.setPostingdate(dataFact.newXMLGregorianCalendar("2008-12-04T10:42:06.000+01:00"));
		xmlTag.setName("testtag");
		xmlBookmark.setUrl("http://www.google.de");
		xmlBookmark.setTitle("Google Search engine");
		bibXML.setPost(xmlPost);
		tmpFile = File.createTempFile("bibsonomy", "junit");
		marshalToFile(bibXML, tmpFile);
		renderer.parsePost(new FileReader(tmpFile));
	}
	
	@Test
	public void testParseReferences() throws Exception {
		final Set<String> refInterHash = new HashSet<String>();
		refInterHash.add("9t8gefnpgjwneigfwmf23324");
		refInterHash.add("82inkrfwfweffsdfsdfsdf");
		refInterHash.add("34efikegnfdgnkdflsgsdfpg");
		
		// build xml
		final BibsonomyXML xml = new BibsonomyXML();
		final ReferencesType refsXML = new ReferencesType();
		xml.setReferences(refsXML);
		
		final List<ReferenceType> referenceList = refsXML.getReference();
		
		for (final String interHash : refInterHash) {
			final ReferenceType reference = new ReferenceType();
			reference.setInterhash(interHash);
			
			referenceList.add(reference);
		}
		
		// save it to file
		final File tmpFile = File.createTempFile("parseReferences", "xml");
		marshalToFile(xml, tmpFile);
		
		// parse from file
		final Set<String> actual = renderer.parseReferences(new FileReader(tmpFile));
		
		// check if the correct interhashes were parsed
		assertEquals(refInterHash, actual);
	}
	
	@Test
	public void testParseStandardPost() throws Exception {
		final BibsonomyXML xml = new BibsonomyXML();
		final GoldStandardPublicationType goldStandardPubXml = new GoldStandardPublicationType();
		goldStandardPubXml.setTitle(GOLD_STANDARD_PUBLICATION_TITLE);
		goldStandardPubXml.setYear(GOLD_STANDARD_PUBLICATION_YEAR);
		goldStandardPubXml.setBibtexKey(GOLD_STANDARD_PUBLICATION_BIBTEX_KEY);
		goldStandardPubXml.setAuthor(GOLD_STANDARD_PUBLICATION_AUTHOR);
		goldStandardPubXml.setEntrytype(GOLD_STANDARD_PUBLICATION_ENTRYTYPE);
		
		final PostType postxml = new PostType();
		postxml.setGoldStandardPublication(goldStandardPubXml);
		
		xml.setPost(postxml);
		
		final UserType userxml = new UserType();
		userxml.setName("foo");
		
		postxml.setUser(userxml);
		
		// save it to file
		final File tmpFile = File.createTempFile("parseStandardPost", "xml");
		marshalToFile(xml, tmpFile);
		
		final Post<? extends Resource> standardPost = renderer.parseStandardPost(new FileReader(tmpFile));
		
		assertTrue(standardPost.getResource() instanceof GoldStandardPublication);
		
		final GoldStandardPublication publication = (GoldStandardPublication) standardPost.getResource();
		
		assertEquals(GOLD_STANDARD_PUBLICATION_AUTHOR, publication.getAuthor());
		assertEquals(GOLD_STANDARD_PUBLICATION_BIBTEX_KEY, publication.getBibtexKey());
		assertEquals(GOLD_STANDARD_PUBLICATION_ENTRYTYPE, publication.getEntrytype());
		assertEquals(GOLD_STANDARD_PUBLICATION_TITLE, publication.getTitle());
		assertEquals(GOLD_STANDARD_PUBLICATION_YEAR, publication.getYear());
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
		Writer sw = new StringWriter(100);
		
		// empty list without start-/end-values 
		final List<Tag> tags = new LinkedList<Tag>();
		try {
			renderer.serializeTags(sw, tags, null);
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
		renderer.serializeTags(sw, tags, vm);
		compareWithFile(sw, "ExampleResultTags1.txt");

		// with tags
		sw = new StringWriter(100);
		final Tag tag1 = new Tag();
		tags.add(tag1);
		try {
			renderer.serializeTags(sw, tags, vm);
			fail("exception should have been thrown: no tagname specified");
		} catch (final InvalidModelException e) {
		}
		tag1.setName("foo");
		sw = new StringWriter(100);
		renderer.serializeTags(sw, tags, vm);
		compareWithFile(sw, "ExampleResultTags2.txt");

		// with multiple tags
		final Tag tag2 = new Tag();
		tag2.setName("bar");
		tag2.setUsercount(5);
		tag2.setGlobalcount(10);
		tags.add(tag2);
		sw = new StringWriter(100);
		renderer.serializeTags(sw, tags, vm);
		compareWithFile(sw, "ExampleResultTags3.txt");
	}

	@Test
	public void testSerializeTag() throws Exception {
		// empty tag
		final Writer sw = new StringWriter(100);
		final Tag tag = new Tag();
		try {
			renderer.serializeTag(sw, tag, null);
			fail("exception should have been thrown: no tagname specified");
		} catch (final InvalidModelException e) {
		}
		tag.setName("foo");
		renderer.serializeTag(sw, tag, null);
		compareWithFile(sw, "ExampleResultTag.txt");
	}

	@Test
	public void testSerializeUsers() throws Exception {
		Writer sw = new StringWriter(100);

		// empty user
		final List<User> users = new LinkedList<User>();
		try {
			renderer.serializeUsers(sw, users, null);
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
			renderer.serializeUsers(sw, users, null);
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
		renderer.serializeUsers(sw, users, vm);
		compareWithFile(sw, "ExampleResultUsers1.txt");
	}

	@Test
	public void testSerializeUser() throws Exception {
		// empty user
		final Writer sw = new StringWriter(100);
		final User user = new User();
		try {
			renderer.serializeUser(sw, user, null);
			fail("exception should have been thrown: no username specified");
		} catch (final InvalidModelException e) {
		}
		user.setName("foo");
		renderer.serializeUser(sw, user, null);
		compareWithFile(sw, "ExampleResultUser.txt");
	}

	@Test
	public void testSerializeGroups() throws Exception {
		Writer sw = new StringWriter(100);

		// empty group
		final List<Group> groups = new LinkedList<Group>();		
		try {
			renderer.serializeGroups(sw, groups, null);
			//fail("exception should have been thrown: no start-/end values specified");
		}
		catch (final InternServerException ex) {			
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
			renderer.serializeGroups(sw, groups, null);
			fail("exception should have been thrown: no groupname specified");
		} catch (final InvalidModelException e) {
		}

		sw = new StringWriter(100);
		group1.setName("testName");
		group1.setDescription("foo bar ...");
		final Group group2 = new Group();
		group2.setName("testName2");
		groups.add(group2);
		renderer.serializeGroups(sw, groups, vm);
		compareWithFile(sw, "ExampleResultGroups1.txt");
	}

	@Test
	public void testSerializeGroup() throws Exception {
		// empty group
		final Writer sw = new StringWriter(100);
		final Group group = new Group();
		try {
			renderer.serializeGroup(sw, group, null);
			fail("exception should have been thrown: no groupname specified");
		} catch (final InvalidModelException e) {
		}
		group.setName("foo");
		group.setDescription("foo bar :)");
		group.setHomepage(new URL("http://www.example.com/"));
		group.setRealname("TestGroup");
		renderer.serializeGroup(sw, group, null);
		compareWithFile(sw, "ExampleResultGroup.txt");
	}

	@Test
	public void testSerializePosts() throws Exception {
		Writer sw = new StringWriter(100);
		final List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
		try {
			renderer.serializePosts(sw, posts, null);
			//fail ("Exception should have been trown: no start-/end-values specified");
		}
		catch (final InternServerException ex) {			
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
		final BibTex publication = this.createPublication();
		post.setResource(publication);
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
		renderer.serializePosts(sw, posts, vm);
		compareWithFile(sw, "ExampleResultPosts.txt");
	}
	
	private BibTex createPublication() {
		final BibTex publication = new BibTex();
		publication.setTitle("foo and bar");
		publication.setIntraHash("abc");
		publication.setInterHash("abc");
		return publication;
	}

	@Test
	public void testSerializePost() {
		final Writer sw = new StringWriter(100);
		final Post<Resource> post = new Post<Resource>();
		try {
			renderer.serializePost(sw, post, null);
			fail("exception should have been thrown: no user specified");
		} catch (final InternServerException e) {
		}
		final User user = new User();
		user.setName("foo");
		post.setUser(user);
		try {
			renderer.serializePost(sw, post, null);
			fail("exception should have been thrown: no tags assigned");
		} catch (final InternServerException e) {
		}
		final Tag tag = new Tag();
		tag.setName("bar");
		post.getTags().add(tag);
		try {
			renderer.serializePost(sw, post, null);
			fail("exception should have been thrown: no ressource assigned");
		} catch (final InternServerException e) {
		}
		final Bookmark bookmark = new Bookmark();
		post.setResource(bookmark);
		post.setDate(new Date(System.currentTimeMillis()));
		try {
			renderer.serializePost(sw, post, null);
			fail("exception should have been thrown: bookmark has no url assigned");
		} catch (final InvalidModelException e) {
		}
		bookmark.setUrl("www.foobar.org");
		bookmark.setTitle("bookmarktitle");
		bookmark.setIntraHash("aabbcc");
		bookmark.setInterHash("1324356789");
		renderer.serializePost(sw, post, null);
		compareWithFile(sw, "ExampleResultPost.txt");
	}
	
	@Test
	public void testSerializeGoldStandardPost() {
		final Post<Resource> post = new Post<Resource>();
		post.setUser(new User("foo"));
		
		final GoldStandardPublication publication = new GoldStandardPublication();
		publication.setAuthor(GOLD_STANDARD_PUBLICATION_AUTHOR);
		publication.setYear(GOLD_STANDARD_PUBLICATION_YEAR);
		publication.setTitle(GOLD_STANDARD_PUBLICATION_TITLE);
		publication.setBibtexKey(GOLD_STANDARD_PUBLICATION_BIBTEX_KEY);
		publication.addToReferences(this.createPublication());
		publication.recalculateHashes();
		
		post.setResource(publication);
		final Writer sw = new StringWriter(100);
		
		renderer.serializePost(sw, post, null);
		
		compareWithFile(sw, "ExampleGoldStandardPublication.xml");
	}

	@Test
	public void testQuoting() {
		final Writer sw = new StringWriter(100);
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
		
		final Bookmark bookmark = this.createBookmark();
		post.setResource(bookmark);
		
		final ViewModel vm = new ViewModel();
		vm.setStartValue(0);
		vm.setEndValue(1);
		vm.setUrlToNextResources("http://foo.bar/posts?start=1&end=2&resourcetype=bookmark&tags=a+->b+<-c+<->d&hash=asd&&&kjalsjdf");
		renderer.serializePosts(sw, posts, vm);
		compareWithFile(sw, "QuotingTest.txt");
	}

	/**
	 * @return
	 */
	private Bookmark createBookmark() {
		final Bookmark bookmark = new Bookmark();
		
		bookmark.setUrl("www.foobar.org");
		bookmark.setTitle("bookmarktitle");
		bookmark.setIntraHash("aabbcc");
		bookmark.setInterHash("1324356789");
		return bookmark;
	}
}