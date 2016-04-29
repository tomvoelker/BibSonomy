/**
 * BibSonomy-Rest-Common - Common things for the REST-client and server.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.renderer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.model.util.ModelValidationUtils;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.data.NoDataAccessor;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.bibsonomy.rest.renderer.xml.BibtexType;
import org.bibsonomy.rest.renderer.xml.BookmarkType;
import org.bibsonomy.rest.renderer.xml.DocumentType;
import org.bibsonomy.rest.renderer.xml.ExtraUrlType;
import org.bibsonomy.rest.renderer.xml.ExtraUrlsType;
import org.bibsonomy.rest.renderer.xml.GoldStandardPublicationType;
import org.bibsonomy.rest.renderer.xml.GroupType;
import org.bibsonomy.rest.renderer.xml.PostType;
import org.bibsonomy.rest.renderer.xml.ReferenceType;
import org.bibsonomy.rest.renderer.xml.ReferencesType;
import org.bibsonomy.rest.renderer.xml.TagType;
import org.bibsonomy.rest.renderer.xml.UserType;
import org.bibsonomy.testutil.TestUtils;
import org.junit.Test;

/**
 * @author Manuel Bork
 * @author Christian Schenk
 */
public abstract class AbstractRendererTest {
	private static final String XML_IS_INVALID_MSG = ModelValidationUtils.DOCUMENT_NOT_VALID_ERROR_MESSAGE;
	
	private static final String GOLD_STANDARD_PUBLICATION_ENTRYTYPE = "research report";
	private static final String GOLD_STANDARD_PUBLICATION_BIBTEX_KEY = "doe2004";
	private static final String GOLD_STANDARD_PUBLICATION_TITLE = "The ten famoust";
	private static final String GOLD_STANDARD_PUBLICATION_YEAR = "2004";
	private static final String GOLD_STANDARD_PUBLICATION_AUTHOR = "Doe, John";
	
	/**
	 * @return the pathToTestFiles
	 */
	public abstract String getPathToTestFiles();

	/**
	 * @return the fileExt
	 */
	public abstract String getFileExt();

	/**
	 * @return the renderer
	 */
	public abstract AbstractRenderer getRenderer();
	
	/**
	 * method that compares the expected and the actual serialized result
	 * 
	 * @param expected
	 * @param actual
	 * @throws Exception
	 */
	public abstract void compare(final String expected, final String actual) throws Exception;
	
	private void assertWithFile(final Writer sw, final String filename) {
		try {
			final String fileContents = TestUtils.readEntryFromFile(filename);
			final String actual = sw.toString().trim();
			this.compare(fileContents.trim(), actual);
		} catch (final Exception ex1) {
			fail(ex1.getMessage());
		}
	}

	@Test
	public void testParseUser() throws Exception {
		// check null behavior
		try {
			this.getRenderer().parseUser(null);
			fail("exception should have been thrown.");
		} catch (final BadRequestOrResponseException e) {
		}

		// check empty/ wrong document
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile("bibsonomy", "junit");
		this.marshalToFile(bibXML, tmpFile);

		try {
			this.getRenderer().parseUser(new FileReader(tmpFile));
			fail("exception should have been thrown.");
		} catch (final BadRequestOrResponseException e) {
			assertEquals("wrong exception thrown: " + e.getMessage(), "The body part of the received document is erroneous - no user defined.", e.getMessage());
		}

		// check valid user
		bibXML = new BibsonomyXML();
		final UserType xmlUser = new UserType();
		xmlUser.setName("test");
		bibXML.setUser(xmlUser);
		tmpFile = File.createTempFile("bibsonomy", "junit");
		this.marshalToFile(bibXML, tmpFile);
		final User user = this.getRenderer().parseUser(new FileReader(tmpFile));
		assertEquals("model not correctly initialized", "test", user.getName());
	}

	@Test
	public void testParseGroup() throws Exception {
		// check null behavior
		try {
			this.getRenderer().parseGroup(null);
			fail("exception should have been thrown.");
		} catch (final BadRequestOrResponseException e) {
		}

		// check empty/ wrong document
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile("bibsonomy", "junit");
		this.marshalToFile(bibXML, tmpFile);

		try {
			this.getRenderer().parseGroup(new FileReader(tmpFile));
			fail("exception should have been thrown.");
		} catch (final BadRequestOrResponseException e) {
			assertEquals("wrong exception thrown: " + e.getMessage(), "The body part of the received document is erroneous - no group defined.", e.getMessage());
		}

		// check valid group
		bibXML = new BibsonomyXML();
		final GroupType xmlGroup = new GroupType();
		xmlGroup.setName("test");
		xmlGroup.setRealname("TestGroup");
		xmlGroup.setHomepage("http://www.example.com/");
		bibXML.setGroup(xmlGroup);
		tmpFile = File.createTempFile("bibsonomy", "junit");
		this.marshalToFile(bibXML, tmpFile);
		final Group group = this.getRenderer().parseGroup(new FileReader(tmpFile));
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
			this.getRenderer().parsePost(null, NoDataAccessor.getInstance());
			fail("exception should have been thrown.");
		} catch (final BadRequestOrResponseException e) {
		}

		// check empty/ wrong document
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile("bibsonomy", "junit");
		this.marshalToFile(bibXML, tmpFile);

		try {
			this.getRenderer().parsePost(new FileReader(tmpFile), NoDataAccessor.getInstance());
			fail("exception should have been thrown.");
		} catch (final BadRequestOrResponseException e) {
			assertEquals("The body part of the received document is erroneous - no post defined.", e.getMessage());
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
		xmlPost.setChangedate(dataFact.newXMLGregorianCalendar("2009-02-04T10:42:06.000+01:00"));
		xmlTag.setName("testtag");
		final String url = "http://www.google.de";
		xmlBookmark.setUrl(url);
		final String title = "Google Search engine";
		xmlBookmark.setTitle(title);
		bibXML.setPost(xmlPost);
		tmpFile = File.createTempFile("bibsonomy", this.getFileExt());
		this.marshalToFile(bibXML, tmpFile);
		final Post<? extends Resource> post = this.getRenderer().parsePost(new FileReader(tmpFile), NoDataAccessor.getInstance());
		final Bookmark bookmark = (Bookmark) post.getResource();
		assertEquals(title, bookmark.getTitle());
		assertEquals(url, bookmark.getUrl());
		
		// test parse publication
		
		xmlPost.setBookmark(null);
		final BibtexType publicationType = new BibtexType();
		publicationType.setTitle(title);
		final String firstName = "Author";
		final String author = "Test, " + firstName;
		publicationType.setAuthor(author);
		final String entryType = "acticle";
		publicationType.setEntrytype(entryType);
		final String year = "2010";
		publicationType.setYear(year);
		final String bibKey = "bibkey";
		publicationType.setBibtexKey(bibKey);
		xmlPost.setBibtex(publicationType);
		
		// extra URLs
		final ExtraUrlsType extraUrlsType = new ExtraUrlsType();
		final List<ExtraUrlType> urls = extraUrlsType.getUrl();
		final ExtraUrlType extraUrlType = new ExtraUrlType();
		extraUrlType.setDate(dataFact.newXMLGregorianCalendar("2008-12-04T10:42:06.000+01:00"));
		extraUrlType.setTitle("Data");
		final String extraUrl = "http://github.com/L3S";
		extraUrlType.setHref(extraUrl);
		urls.add(extraUrlType);
		publicationType.setExtraurls(extraUrlsType);
		
		
		tmpFile = File.createTempFile("bibsonomy-publ", this.getFileExt());
		this.marshalToFile(bibXML, tmpFile);
		
		final Post<? extends Resource> publicationPost = this.getRenderer().parsePost(new FileReader(tmpFile), NoDataAccessor.getInstance());
		final BibTex publication = (BibTex) publicationPost.getResource();
		assertEquals(title, publication.getTitle());
		assertEquals(entryType, publication.getEntrytype());
		assertEquals(bibKey, publication.getBibtexKey());
		assertEquals(year, publication.getYear());
		assertEquals(firstName, publication.getAuthor().get(0).getFirstName());
	
		
		final List<BibTexExtra> extraUrls = publication.getExtraUrls();
		assertEquals(1, extraUrls.size());
		assertEquals(extraUrl, extraUrls.get(0).getUrl().toExternalForm());
		
	}
	
	@Test
	public void testParseDocument() throws Exception {
		
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile("bibsonomy", "junit");
		this.marshalToFile(bibXML, tmpFile);
		
		try {
			this.getRenderer().parseDocument(new FileReader(tmpFile), NoDataAccessor.getInstance());
			fail("exception should have been thrown.");
		} catch (final BadRequestOrResponseException e) {
			e.getMessage();
			assertEquals("Wrong exception thrown " + e.getMessage(), "The body part of the received document is erroneous - no valid document data defined.", e.getMessage());
		}
		
		final String filename = "foobar";
		final String md5hash = "hash";
		
		bibXML = new BibsonomyXML();
		DocumentType docType = new DocumentType();
		docType.setFilename(filename);
		docType.setMd5Hash(md5hash);
		bibXML.setDocument(docType);
		
		tmpFile = File.createTempFile("bibsonomy", "junit");
		this.marshalToFile(bibXML, tmpFile);
		
		final Document parsedDoc = this.getRenderer().parseDocument(new FileReader(tmpFile), NoDataAccessor.getInstance());
		
		assertEquals(filename, parsedDoc.getFileName());
		assertEquals(md5hash, parsedDoc.getMd5hash());
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
		final File tmpFile = File.createTempFile("parseReferences", this.getFileExt());
		this.marshalToFile(xml, tmpFile);
		
		// parse from file
		final Set<String> actual = this.getRenderer().parseReferences(new FileReader(tmpFile));
		
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
		final File tmpFile = File.createTempFile("parseStandardPost", this.getFileExt());
		this.marshalToFile(xml, tmpFile);
		
		final Post<? extends Resource> communityPost = this.getRenderer().parseCommunityPost(new FileReader(tmpFile));
		
		assertTrue(communityPost.getResource() instanceof GoldStandardPublication);
		
		final GoldStandardPublication publication = (GoldStandardPublication) communityPost.getResource();
		
		assertEquals(GOLD_STANDARD_PUBLICATION_AUTHOR, publication.getAuthor().get(0).toString());
		assertEquals(GOLD_STANDARD_PUBLICATION_BIBTEX_KEY, publication.getBibtexKey());
		assertEquals(GOLD_STANDARD_PUBLICATION_ENTRYTYPE, publication.getEntrytype());
		assertEquals(GOLD_STANDARD_PUBLICATION_TITLE, publication.getTitle());
		assertEquals(GOLD_STANDARD_PUBLICATION_YEAR, publication.getYear());
	}

	protected void marshalToFile(final BibsonomyXML bibXML, final File tmpFile) throws Exception {
		this.getRenderer().serialize(new FileWriter(tmpFile), bibXML);
	}

	@Test
	public void testSerializeTags() throws Exception {
		Writer sw = new StringWriter(100);
		
		// empty list without start-/end-values 
		final List<Tag> tags = new LinkedList<Tag>();
		
		// empty list
		final ViewModel vm = new ViewModel();
		vm.setStartValue(0);
		vm.setEndValue(10);
		vm.setUrlToNextResources("http://www.bibsonomy.org/foo/bar");
		sw = new StringWriter(100);
		this.getRenderer().serializeTags(sw, tags, vm);
		this.assertWithFile(sw, this.getPathToTestFiles() + "ExampleResultTags1" + this.getFileExt());

		// with tags
		sw = new StringWriter(100);
		final Tag tag1 = new Tag();
		tag1.setName("foo");
		tags.add(tag1);
		sw = new StringWriter(100);
		this.getRenderer().serializeTags(sw, tags, vm);
		this.assertWithFile(sw, this.getPathToTestFiles() + "ExampleResultTags2" + this.getFileExt());

		// with multiple tags
		final Tag tag2 = new Tag();
		tag2.setName("bar");
		tag2.setUsercount(5);
		tag2.setGlobalcount(10);
		tags.add(tag2);
		sw = new StringWriter(100);
		this.getRenderer().serializeTags(sw, tags, vm);
		this.assertWithFile(sw, this.getPathToTestFiles() + "ExampleResultTags3" + this.getFileExt());
	}

	@Test
	public void testSerializeTag() throws Exception {
		// empty tag
		final Writer sw = new StringWriter(100);
		final Tag tag = new Tag();
		tag.setName("foo");
		this.getRenderer().serializeTag(sw, tag, null);
		this.assertWithFile(sw, this.getPathToTestFiles() + "ExampleResultTag" + this.getFileExt());
	}

	@Test
	public void testSerializeUsers() throws Exception {
		final Writer sw = new StringWriter(100);
		
		final List<User> users = new LinkedList<User>();
		
		final ViewModel vm = new ViewModel();
		vm.setStartValue(20);
		vm.setEndValue(30);
		vm.setUrlToNextResources("http://www.bibsonomy.org/api/foo/bar");
		
		final User user1 = new User();
		user1.setName("testName");
		user1.setEmail("mail@foo.bar");
		user1.setHomepage(new URL("http://foo.bar.com"));
		user1.setPassword("raboof");
		user1.setRealname("Dr. FOO BaR");
		users.add(user1);
		
		final User user2 = new User();
		user2.setName("fooBar");
		user2.getGroups().add(new Group("kde"));
		users.add(user2);
		this.getRenderer().serializeUsers(sw, users, vm);
		this.assertWithFile(sw, this.getPathToTestFiles() + "ExampleResultUsers1" + this.getFileExt());
	}

	@Test
	public void testSerializeUser() throws Exception {
		// empty user
		final Writer sw = new StringWriter(100);
		final User user = new User();
		user.setName("foo");
		this.getRenderer().serializeUser(sw, user, null);
		this.assertWithFile(sw, this.getPathToTestFiles() + "ExampleResultUser" + this.getFileExt());
	}

	@Test
	public void testSerializeGroups() throws Exception {
		final Writer sw = new StringWriter(100);
		final List<Group> groups = new LinkedList<Group>();
		// empty group
		final ViewModel vm = new ViewModel();
		vm.setStartValue(20);
		vm.setEndValue(30);
		vm.setUrlToNextResources("http://www.bibsonomy.org/api/foo/bar");
		
		final Group group1 = new Group();
		group1.setName("testName");
		group1.setDescription("foo bar ...");
		groups.add(group1);
		final Group group2 = new Group();
		group2.setName("testName2");
		groups.add(group2);
		this.getRenderer().serializeGroups(sw, groups, vm);
		this.assertWithFile(sw, this.getPathToTestFiles() + "ExampleResultGroups1" + this.getFileExt());
	}

	@Test
	public void testSerializeGroup() throws Exception {
		// empty group
		final Writer sw = new StringWriter(100);
		final Group group = new Group();
		group.setName("foo");
		group.setDescription("foo bar :)");
		group.setHomepage(new URL("http://www.example.com/"));
		group.setRealname("TestGroup");
		this.getRenderer().serializeGroup(sw, group, null);
		this.assertWithFile(sw, this.getPathToTestFiles() + "ExampleResultGroup" + this.getFileExt());
	}

	@Test
	public void testSerializePosts() throws Exception {
		final Writer sw = new StringWriter(100);
		final List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
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
		post.setDate(new Date(1303978514000l));
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
		post2.setDate(new Date(1303978514000l));
		posts.add(post2);
		this.getRenderer().serializePosts(sw, posts, vm);
		this.assertWithFile(sw, this.getPathToTestFiles() + "ExampleResultPosts" + this.getFileExt());
	}

	@Test
	public void testSerializePost() {
		final Writer sw = new StringWriter(100);
		final Post<Resource> post = new Post<Resource>();
		final User user = new User();
		user.setName("foo");
		post.setUser(user);
		final Tag tag = new Tag();
		tag.setName("bar");
		post.getTags().add(tag);
		final Bookmark bookmark = new Bookmark();
		post.setResource(bookmark);
		bookmark.setUrl("www.foobar.org");
		bookmark.setTitle("bookmarktitle");
		bookmark.setIntraHash("aabbcc");
		bookmark.setInterHash("1324356789");
		post.setDate(new Date(1303978514000l));
		post.setChangeDate(new Date(1303998514000l));
		this.getRenderer().serializePost(sw, post, null);
		this.assertWithFile(sw, this.getPathToTestFiles() + "ExampleResultPost" + this.getFileExt());
	}
	
	@Test
	public void testSerializeGoldStandardPost() throws PersonListParserException {
		final Post<Resource> post = new Post<Resource>();
		post.setUser(new User("foo"));
		
		final GoldStandardPublication publication = new GoldStandardPublication();
		publication.setAuthor(PersonNameUtils.discoverPersonNames(GOLD_STANDARD_PUBLICATION_AUTHOR));
		publication.setYear(GOLD_STANDARD_PUBLICATION_YEAR);
		publication.setTitle(GOLD_STANDARD_PUBLICATION_TITLE);
		publication.setBibtexKey(GOLD_STANDARD_PUBLICATION_BIBTEX_KEY);
		publication.addToReferences(this.createPublication());
		publication.recalculateHashes();
		
		post.setResource(publication);
		final Writer sw = new StringWriter(100);
		
		this.getRenderer().serializePost(sw, post, null);
		
		this.assertWithFile(sw, this.getPathToTestFiles() + "ExampleGoldStandardPublication" + this.getFileExt());
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
		post.setDate(new Date(1303978514000l));
		
		final Bookmark bookmark = this.createBookmark();
		post.setResource(bookmark);
		
		final ViewModel vm = new ViewModel();
		vm.setStartValue(0);
		vm.setEndValue(1);
		vm.setUrlToNextResources(this.getQuotingTestString());
		this.getRenderer().serializePosts(sw, posts, vm);
		this.assertWithFile(sw, this.getPathToTestFiles() + "QuotingTest" + this.getFileExt());
	}

	protected abstract String getQuotingTestString();

	@Test
	public void testCreateUser() {
		// check invalid user
		final UserType xmlUser = new UserType();
		try {
			this.getRenderer().createUser(xmlUser);
			fail("exception should have been thrown.");
		} catch (final InvalidModelException e) {
			assertEquals("wrong exception thrown", XML_IS_INVALID_MSG + "username is missing in element 'user'", e.getMessage());
		}

		// check valid user
		xmlUser.setName("test");
		final User user = this.getRenderer().createUser(xmlUser);
		assertEquals("model not correctly initialized", "test", user.getName());
	}

	@Test
	public void testCreateGroup() {
		// check invalid group
		final GroupType xmlGroup = new GroupType();
		try {
			this.getRenderer().createGroup(xmlGroup);
			fail("exception should have been thrown.");
		} catch (final InvalidModelException e) {
			assertEquals("wrong exception thrown", XML_IS_INVALID_MSG + "groupname is missing in element 'group'", e.getMessage());
		}

		// check valid group
		xmlGroup.setName("test");
		xmlGroup.setRealname("TestGroup");
		xmlGroup.setHomepage("http://www.example.com/");
		final Group group = this.getRenderer().createGroup(xmlGroup);
		
		assertEquals("model not correctly initialized", "test", group.getName());
		assertEquals("model not correctly initialized", "http://www.example.com/", group.getHomepage().toString());
		assertEquals("model not correctly initialized", "TestGroup", group.getRealname());
	}

	@Test
	public void testCreateTag() {
		// check invalid tag
		final TagType xmlTag = new TagType();
		try {
			this.getRenderer().createTag(xmlTag);
		} catch (final InvalidModelException e) {
			assertEquals("wrong exception thrown", XML_IS_INVALID_MSG + "tag name is missing in element 'tag'", e.getMessage());
		}

		// check valid tag
		xmlTag.setName("foo");
		Tag tag = this.getRenderer().createTag(xmlTag);
		assertTrue("tag not correctly initailized", "foo".equals(tag.getName()));
		xmlTag.setGlobalcount(BigInteger.ONE);
		xmlTag.setUsercount(BigInteger.TEN);
		tag = this.getRenderer().createTag(xmlTag);
		assertEquals("tag not correctly initailized", 1, tag.getGlobalcount());
		assertEquals("tag not correctly initailized", 10, tag.getUsercount());
	}

	@Test
	public void testCreatePost() throws DatatypeConfigurationException, PersonListParserException {
		// check invalid posts
		final PostType xmlPost = new PostType();
		final DatatypeFactory dataFact = DatatypeFactory.newInstance();		
		xmlPost.setPostingdate(dataFact.newXMLGregorianCalendar("2008-12-04T10:42:06.000+01:00"));
		// 2011/10/6, fei: deactivated test, as system tags are hidden and thus posts without tags are valid
		// checkInvalidPost(xmlPost, XML_IS_INVALID_MSG + "no tags specified");
		final TagType xmlTag = new TagType();
		xmlPost.getTag().add(xmlTag);
		this.checkInvalidPost(xmlPost, XML_IS_INVALID_MSG + "user is missing");
		final UserType xmlUser = new UserType();
		xmlUser.setName("tuser");
		xmlPost.setUser(xmlUser);
		this.checkInvalidPost(xmlPost, XML_IS_INVALID_MSG + "resource is missing inside element 'post'");
		final BookmarkType xmlBookmark = new BookmarkType();
		xmlPost.setBookmark(xmlBookmark);
		this.checkInvalidPost(xmlPost, XML_IS_INVALID_MSG + "tag name is missing in element 'tag'");
		xmlTag.setName("testtag");
		this.checkInvalidPost(xmlPost, XML_IS_INVALID_MSG + "url is missing in element 'bookmark'");
		xmlBookmark.setUrl("http://www.google.de");
		xmlBookmark.setTitle("Google search engine");
		xmlPost.setBookmark(xmlBookmark);
		xmlPost.setBibtex(new BibtexType());
		this.checkInvalidPost(xmlPost, XML_IS_INVALID_MSG + "only one resource type is allowed inside element 'post'");
		xmlPost.setBibtex(null);

		// check valid post with bookmark
		Post<? extends Resource> post = this.getRenderer().createPost(xmlPost, NoDataAccessor.getInstance());
		assertEquals("model not correctly initialized", "tuser", post.getUser().getName());
		assertTrue("model not correctly initialized", post.getResource() instanceof Bookmark);
		assertEquals("model not correctly initialized", "http://www.google.de", ((Bookmark) post.getResource()).getUrl());
		assertEquals("model not correctly initialized", "testtag", post.getTags().iterator().next().getName());

		xmlPost.setBookmark(null);
		final BibtexType xmlBibtex = new BibtexType();
		xmlPost.setBibtex(xmlBibtex);
		this.checkInvalidPost(xmlPost, XML_IS_INVALID_MSG + "title is missing in element 'bibtex'");
		xmlBibtex.setTitle("foo bar");
		xmlBibtex.setYear("2005");
		xmlBibtex.setBibtexKey("myBibtexKey");
		xmlBibtex.setEntrytype("inproceedings");
		xmlBibtex.setAuthor("Hans Dampf");
		
		// extra URLs
		final ExtraUrlsType extraUrlsType = new ExtraUrlsType();
		final List<ExtraUrlType> urls = extraUrlsType.getUrl();
		final ExtraUrlType extraUrlType = new ExtraUrlType();
		extraUrlType.setDate(dataFact.newXMLGregorianCalendar("2008-12-04T10:42:06.000+01:00"));
		extraUrlType.setTitle("Data");
		final String extraUrl = "http://github.com/L3S";
		extraUrlType.setHref(extraUrl);
		urls.add(extraUrlType);
		xmlBibtex.setExtraurls(extraUrlsType);
		

		// check valid post with bibtex
		post = this.getRenderer().createPost(xmlPost, NoDataAccessor.getInstance());
		assertEquals("model not correctly initialized", "tuser", post.getUser().getName());
		assertTrue("model not correctly initialized", post.getResource() instanceof BibTex);
		assertEquals("model not correctly initialized", "foo bar", ((BibTex) post.getResource()).getTitle());
		assertEquals("model not correctly initialized", "testtag", post.getTags().iterator().next().getName());
		assertEquals("model not correctly initialized", extraUrl, ((BibTex) post.getResource()).getExtraUrls().get(0).getUrl().toExternalForm());
		
		
	}

	private void checkInvalidPost(final PostType xmlPost, final String exceptionMessage) throws PersonListParserException {
		try {
			this.getRenderer().createPost(xmlPost, NoDataAccessor.getInstance());
			fail("exception should have been thrown.");
		} catch (final InvalidModelException e) {
			assertEquals("wrong exception thrown", exceptionMessage, e.getMessage());
		}
	}
	
	private BibTex createPublication() {
		final BibTex publication = new BibTex();
		publication.setYear("1998");
		publication.setBibtexKey("knuth1998computer");
		publication.setEntrytype("book");
		publication.setTitle("The Art of Computer Programming");
		publication.setAuthor(Arrays.asList(new PersonName("Donald E.", "Knuth")));
		publication.setIntraHash("abc");
		publication.setInterHash("abc");
		
		// extra URLs
		publication.setExtraUrls(Collections.singletonList(new BibTexExtra(this.createURL("http://github.com/L3S"), "Data", new Date(1303978514000l))));
		
		return publication;
	}

	private URL createURL(final String s) {
		try {
			return new URL(s);
		} catch (MalformedURLException e) {
			return null;
		}
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