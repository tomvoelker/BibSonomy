package org.bibsonomy.rest.renderer.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.bibsonomy.common.exceptions.InternServerException;
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
		} catch (BadRequestOrResponseException e) {
		}

		// check empty/ wrong document
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile("bibsonomy", "junit");
		marshalToFile(bibXML, tmpFile);

		try {
			this.renderer.parseUser(new FileReader(tmpFile));
			fail("exception should have been thrown.");
		} catch (BadRequestOrResponseException e) {
			if (!"The body part of the received document is erroneous - no user defined.".equals(e.getMessage())) fail("wrong exception thrown: " + e.getMessage());
		}

		// check valid user
		bibXML = new BibsonomyXML();
		UserType userType = new UserType();
		userType.setName("test");
		bibXML.setUser(userType);
		tmpFile = File.createTempFile("bibsonomy", "junit");
		marshalToFile(bibXML, tmpFile);
		final User user = this.renderer.parseUser(new FileReader(tmpFile));
		assertTrue("model not correctly initialized", "test".equals(user.getName()));
	}

	@Test
	public void testParseGroup() throws Exception {
		// check null behavior
		try {
			this.renderer.parseGroup(null);
			fail("exception should have been thrown.");
		} catch (BadRequestOrResponseException e) {
		}

		// check empty/ wrong document
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile("bibsonomy", "junit");
		marshalToFile(bibXML, tmpFile);

		try {
			this.renderer.parseGroup(new FileReader(tmpFile));
			fail("exception should have been thrown.");
		} catch (BadRequestOrResponseException e) {
			if (!"The body part of the received document is erroneous - no group defined.".equals(e.getMessage())) fail("wrong exception thrown: " + e.getMessage());
		}

		// check valid group
		bibXML = new BibsonomyXML();
		GroupType groupType = new GroupType();
		groupType.setName("test");
		bibXML.setGroup(groupType);
		tmpFile = File.createTempFile("bibsonomy", "junit");
		marshalToFile(bibXML, tmpFile);
		final Group group = this.renderer.parseGroup(new FileReader(tmpFile));
		assertTrue("model not correctly initialized", "test".equals(group.getName()));
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
		} catch (BadRequestOrResponseException e) {
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
		xmlTag.setName("testtag");
		xmlBookmark.setUrl("http://www.google.de");
		bibXML.setPost(xmlPost);
		tmpFile = File.createTempFile("bibsonomy", "junit");
		marshalToFile(bibXML, tmpFile);
		this.renderer.parsePost(new FileReader(tmpFile));
	}

	private void marshalToFile(final BibsonomyXML bibXML, final File tmpFile) throws JAXBException, PropertyException, FileNotFoundException {
		final JAXBContext jc = JAXBContext.newInstance("org.bibsonomy.rest.renderer.xml");
		final JAXBElement<BibsonomyXML> webserviceElement = (new ObjectFactory()).createBibsonomyXMLInterchangeDocument(bibXML);
		final Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(webserviceElement, new FileOutputStream(tmpFile));
	}

	@Test
	public void testSerializeTags() throws Exception {
		StringWriter sw = new StringWriter(100);

		// empty list
		final LinkedList<Tag> tags = new LinkedList<Tag>();
		this.renderer.serializeTags(sw, tags, null);
		compareWithFile(sw, "ExampleResultTags0.txt");

		// empty list 2
		final ViewModel vm = new ViewModel();
		vm.setStartValue(0);
		vm.setEndValue(10);
		vm.setUrlToNextResources("http://www.bibsonomy.org/foo/bar");
		sw = new StringWriter(100);
		this.renderer.serializeTags(sw, tags, vm);
		compareWithFile(sw, "ExampleResultTags1.txt");

		// with tags
		sw = new StringWriter(100);
		final Tag t1 = new Tag();
		tags.add(t1);
		try {
			this.renderer.serializeTags(sw, tags, vm);
			fail("exception should have been thrown: no tagname specified");
		} catch (InternServerException e) {
		}
		t1.setName("foo");
		sw = new StringWriter(100);
		this.renderer.serializeTags(sw, tags, vm);
		compareWithFile(sw, "ExampleResultTags2.txt");

		// with multiple tags
		final Tag t2 = new Tag();
		t2.setName("bar");
		t2.setUsercount(5);
		t2.setGlobalcount(10);
		tags.add(t2);
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
		} catch (InternServerException e) {
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
		this.renderer.serializeUsers(sw, users, null);
		compareWithFile(sw, "ExampleResultUsers0.txt");

		//
		final ViewModel vm = new ViewModel();
		vm.setStartValue(20);
		vm.setEndValue(30);
		vm.setUrlToNextResources("http://www.bibsonomy.org/api/foo/bar");
		final User u1 = new User();
		users.add(u1);
		try {
			this.renderer.serializeUsers(sw, users, null);
			fail("exception should have been thrown: no username specified");
		} catch (InternServerException e) {
		}

		sw = new StringWriter(100);
		u1.setName("testName");
		u1.setEmail("mail@foo.bar");
		u1.setHomepage(new URL("http://foo.bar.com"));
		u1.setPassword("raboof");
		u1.setRealname("Dr. FOO BaR");
		final User u2 = new User();
		u2.setName("fooBar");
		users.add(u2);
		this.renderer.serializeUsers(sw, users, vm);
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
		} catch (InternServerException e) {
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
		this.renderer.serializeGroups(sw, groups, null);
		compareWithFile(sw, "ExampleResultGroups0.txt");

		//
		final ViewModel vm = new ViewModel();
		vm.setStartValue(20);
		vm.setEndValue(30);
		vm.setUrlToNextResources("http://www.bibsonomy.org/api/foo/bar");
		final Group g1 = new Group();
		groups.add(g1);
		try {
			this.renderer.serializeGroups(sw, groups, null);
			fail("exception should have been thrown: no groupname specified");
		} catch (InternServerException e) {
		}

		sw = new StringWriter(100);
		g1.setName("testName");
		g1.setDescription("foo bar ...");
		final Group g2 = new Group();
		g2.setName("testName2");
		groups.add(g2);
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
		} catch (InternServerException e) {
		}
		group.setName("foo");
		group.setDescription("foo bar :)");
		this.renderer.serializeGroup(sw, group, null);
		compareWithFile(sw, "ExampleResultGroup.txt");
	}

	@Test
	public void testSerializePosts() throws Exception {
		StringWriter sw = new StringWriter(100);
		final List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
		this.renderer.serializePosts(sw, posts, null);
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
		final BibTex bib = new BibTex();
		bib.setTitle("foo and bar");
		bib.setIntraHash("abc");
		bib.setInterHash("abc");
		post.setResource(bib);
		posts.add(post);
		final Bookmark b = new Bookmark();
		b.setInterHash("12345678");
		b.setIntraHash("12345678");
		b.setUrl("www.foobar.de");
		final Post<Resource> post2 = new Post<Resource>();
		post2.setResource(b);
		post2.setUser(user);
		post2.getTags().add(tag);
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
		} catch (InternServerException e) {
		}
		final User u = new User();
		u.setName("foo");
		post.setUser(u);
		try {
			this.renderer.serializePost(sw, post, null);
			fail("exception should have been thrown: no tags assigned");
		} catch (InternServerException e) {
		}
		final Tag t = new Tag();
		t.setName("bar");
		post.getTags().add(t);
		try {
			this.renderer.serializePost(sw, post, null);
			fail("exception should have been thrown: no ressource assigned");
		} catch (InternServerException e) {
		}
		final Bookmark b = new Bookmark();
		post.setResource(b);
		try {
			this.renderer.serializePost(sw, post, null);
			fail("exception should have been thrown: bookmark has no url assigned");
		} catch (InternServerException e) {
		}
		b.setUrl("www.foobar.org");
		b.setIntraHash("aabbcc");
		b.setInterHash("1324356789");
		this.renderer.serializePost(sw, post, null);
		compareWithFile(sw, "ExampleResultPost.txt");
	}

	@Test
	public void testQuoting() throws IOException {
		final StringWriter sw = new StringWriter(100);
		final List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
		final Post<Resource> post = new Post<Resource>();
		posts.add(post);
		final User u = new User();
		u.setName("foo");
		post.setUser(u);
		final Tag t = new Tag();
		t.setName("bar");
		post.getTags().add(t);
		final Bookmark b = new Bookmark();
		post.setResource(b);
		b.setUrl("www.foobar.org");
		b.setIntraHash("aabbcc");
		b.setInterHash("1324356789");
		final ViewModel vm = new ViewModel();
		vm.setStartValue(0);
		vm.setEndValue(1);
		vm.setUrlToNextResources("http://foo.bar/posts?start=1&end=2&resourcetype=bookmark&tags=a+->b+<-c+<->d&hash=asd&&&kjalsjdf");
		this.renderer.serializePosts(sw, posts, vm);
		compareWithFile(sw, "QuotingTest.txt");
	}

	private void compareWithFile(final StringWriter sw, final String filename) throws IOException {
		final StringBuffer sb = new StringBuffer(200);
		final File file = new File("src/test/java/org/bibsonomy/rest/renderer/impl/" + filename);
		final BufferedReader br = new BufferedReader(new FileReader(file));
		String s;
		while ((s = br.readLine()) != null) {
			sb.append(s + "\n");
		}
		assertTrue("output not as expected", sw.toString().equals(sb.toString()));
	}
}