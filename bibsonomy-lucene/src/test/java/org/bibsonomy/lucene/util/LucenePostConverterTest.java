package org.bibsonomy.lucene.util;

import static org.junit.Assert.assertEquals;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.util.ExceptionUtils;
import org.junit.Before;
import org.junit.Test;

public class LucenePostConverterTest extends LuceneBase {
	private static final Log log = LogFactory.getLog(LucenePostConverterTest.class);
	
	@Before
	public void setUp() {
		// bind datasource access via JNDI
		JNDITestDatabaseBinder.bind();
	}
	
	@Test
	public void writeBookmarkPost() {
		LucenePost<Bookmark> refPost =	generateBookmarkTestPost(
			"testTitle", "testTag", "testAuthor", "testUser", 
			new Date(System.currentTimeMillis()), GroupID.PUBLIC);
		
		Document doc = LucenePostConverter.readPost(refPost);
		
		Post<Bookmark> testPost = LucenePostConverter.writeBookmarkPost(doc); 

		//--------------------------------------------------------------------
		// compare some elements
		//--------------------------------------------------------------------
		// title
		assertEquals(refPost.getResource().getTitle(), testPost.getResource().getTitle());

		// url
		assertEquals(refPost.getResource().getUrl(), testPost.getResource().getUrl());

		// tags
		for( Tag tag : refPost.getTags() ) {
			assertEquals(true, testPost.getTags().contains(tag));
		}
		// hashes
		assertEquals(refPost.getResource().getIntraHash(), testPost.getResource().getIntraHash());
		assertEquals(refPost.getResource().getInterHash(), testPost.getResource().getInterHash());

		// groups
		for( Group group : refPost.getGroups() ) {
			assertEquals(true, testPost.getGroups().contains(group));
		}
	}
	
	@Test
	public void writeBibTexPost() {
		LucenePost<BibTex> refPost =	generateBibTexTestPost(
			"testTitle", "testTag", "testAuthor", "testUser", 
			new Date(System.currentTimeMillis()), GroupID.PUBLIC);
		
		Document doc = LucenePostConverter.readPost(refPost);
		
		Post<BibTex> testPost = LucenePostConverter.writeBibTexPost(doc); 

		//--------------------------------------------------------------------
		// compare some elements
		//--------------------------------------------------------------------
		// title
		assertEquals(refPost.getResource().getTitle(), testPost.getResource().getTitle());
		// url
		assertEquals(refPost.getResource().getUrl(), testPost.getResource().getUrl());
		// author
		assertEquals(refPost.getResource().getAuthor(), testPost.getResource().getAuthor());
		// year
		assertEquals(refPost.getResource().getYear(), testPost.getResource().getYear());
		// address
		assertEquals(refPost.getResource().getYear(), testPost.getResource().getYear());
		// tags
		for( Tag tag : refPost.getTags() ) {
			assertEquals(true, testPost.getTags().contains(tag));
		}
		// hashes
		assertEquals(refPost.getResource().getIntraHash(), testPost.getResource().getIntraHash());
		assertEquals(refPost.getResource().getInterHash(), testPost.getResource().getInterHash());
		// groups
		for( Group group : refPost.getGroups() ) {
			assertEquals(true, testPost.getGroups().contains(group));
		}
	}
	
	
	@Test
	public void bibTexPost() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		LucenePost<BibTex> testPost = generateBibTexTestPost(
				"testTitle", "testTag", "testAuthor", "testUser", 
				new Date(System.currentTimeMillis()), GroupID.PUBLIC);

		
		Document postDoc = LucenePostConverter.readPost(testPost);
		
		//--------------------------------------------------------------------
		// compare some elements
		//--------------------------------------------------------------------
		// title
		assertEquals(testPost.getResource().getTitle(), postDoc.get(FLD_TITLE));
		// tags
		for( Tag tag : testPost.getTags() ) {
			String tagName = tag.getName();
			
			assertEquals(true, postDoc.get(FLD_TAGS).contains(tagName));
		}
		// author
		assertEquals(testPost.getResource().getAuthor(), postDoc.get(FLD_AUTHOR));
		// year
		assertEquals(testPost.getResource().getYear(), postDoc.get(FLD_YEAR));
		// address
		assertEquals(testPost.getResource().getAddress(), postDoc.get(FLD_ADDRESS));
		// groups
		for( Group group : testPost.getGroups() ) {
			String tagName = group.getName();
			
			assertEquals(true, postDoc.get(FLD_GROUP).contains(tagName));
		}
	}
	
	/**
	 * generate a BibTex Post, can't call setBeanPropertiesOn() because private
	 * so copy & paste the setBeanPropertiesOn() into this method
	 * 
	 * GroupID.PUBLIC
	 */
	private static LucenePost <BibTex> generateBibTexTestPost(
			String titleText, String tagName, 
			String authorName, 
			String userName, Date postDate, GroupID groupID) {
		
		final LucenePost<BibTex> post = new LucenePost<BibTex>();
		post.setContentId((int)Math.floor(Math.random()*Integer.MAX_VALUE));

		final Group group = new Group(groupID);
	
		post.getGroups().add(group);

		Tag tag = new Tag();
		tag.setName("tag1");
		post.getTags().add(tag);
		tag = new Tag();
		tag.setName("tag2");
		post.getTags().add(tag);
		tag = new Tag();
		tag.setName(tagName);
		post.getTags().add(tag);

		post.setContentId(null); // will be set in storePost()
		post.setDescription("luceneTestPost");
		post.setDate(postDate);
		final User user = new User();
		setBeanPropertiesOn(user);
		user.setName(userName);
		user.setRole(Role.NOBODY);
		post.setUser(user);
		final BibTex resource;

		
		final BibTex bibtex = new BibTex();
		setBeanPropertiesOn(bibtex);
		bibtex.setCount(0);		
		bibtex.setEntrytype("inproceedings");
		bibtex.setAuthor("MegaMan and Lucene GigaWoman "+authorName);
		bibtex.setEditor("Peter Silie "+authorName);
		bibtex.setTitle("bibtex insertpost test");
		resource = bibtex;
		
		String title, year, journal, booktitle, volume, number = null;
		title = "title "+ (Math.round(Math.random()*Integer.MAX_VALUE))+" "+titleText;
		year = "test year";
		journal = "test journal";
		booktitle = "test booktitle";
		volume = "test volume";
		number = "test number";
		bibtex.setTitle(title);
		bibtex.setYear(year);
		bibtex.setJournal(journal);
		bibtex.setBooktitle(booktitle);
		bibtex.setVolume(volume);
		bibtex.setNumber(number);
		bibtex.setScraperId(-1);
		bibtex.setType("2");
		bibtex.recalculateHashes();
		post.setResource(resource);
		return post;
	}
	
	/**
	 * generate a Bookmark Post, can't call setBeanPropertiesOn() because private
	 * so copy & paste the setBeanPropertiesOn() into this method
	 */
	private static LucenePost <Bookmark> generateBookmarkTestPost(
			String titleText, String tagName, 
			String authorName, 
			String userName, Date postDate, GroupID groupID	
			) {
		
		final LucenePost<Bookmark> post = new LucenePost<Bookmark>();
		post.setContentId((int)Math.floor(Math.random()*Integer.MAX_VALUE));

		final Group group = new Group(groupID);
		post.getGroups().add(group);

		Tag tag = new Tag();
		tag.setName("tag1");
		post.getTags().add(tag);
		tag = new Tag();
		tag.setName("tag2");
		post.getTags().add(tag);
		tag = new Tag();
		tag.setName(tagName);
		post.getTags().add(tag);

		post.setContentId(null); // will be set in storePost()
		post.setDescription("Some description");
		post.setDate(postDate);
		final User user = new User();
		setBeanPropertiesOn(user);
		user.setName(userName);
		user.setRole(Role.NOBODY);
		post.setUser(user);
		final Bookmark resource;

		
		final Bookmark bookmark = new Bookmark();
		bookmark.setCount(0);
		//bookmark.setIntraHash("e44a7a8fac3a70901329214fcc1525aa");
		bookmark.setTitle("test"+(Math.round(Math.random()*Integer.MAX_VALUE))+" "+titleText);
		bookmark.setUrl("http://www.test"+(Math.round(Math.random()*Integer.MAX_VALUE))+"url.org");
		bookmark.recalculateHashes();
		resource = bookmark;
		
		post.setResource(resource);
		return post;
	}
	
	/**
	 * Calls every setter on an object and fills it wiht dummy values.
	 */
	private static void setBeanPropertiesOn(final Object obj) {
		try {
			final BeanInfo bi = Introspector.getBeanInfo(obj.getClass());
			for (final PropertyDescriptor d : bi.getPropertyDescriptors()) {
				try {
					final Method setter = d.getWriteMethod();
					final Method getter = d.getReadMethod();
					if ((setter != null) && (getter != null)) {
						setter.invoke(obj, new Object[] { getDummyValue(d.getPropertyType(), d.getName()) });
					}
				} catch (final Exception ex) {
					ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "could not invoke setter '" + d.getName() + "'");
				}
			}
		} catch (final IntrospectionException ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "could not introspect object of class '" + obj.getClass().getName() + "'");
		}
	}
	
	/**
	 * Returns dummy values for some primitive types and classes
	 */
	private static Object getDummyValue(final Class<?> type, final String name) {
		if (String.class == type) {
			return "test-" + name;
		}
		if ((int.class == type) || (Integer.class == type)) {
			return Math.abs(name.hashCode());
		}
		if ((boolean.class == type) || (Boolean.class == type)) {
			return (name.hashCode() % 2 == 0);
		}
		if (URL.class == type) {
			try {
				return new URL("http://www.bibsonomy.org/test/" + name);
			} catch (final MalformedURLException ex) {
				throw new RuntimeException(ex);
			}
		}
		if (Privlevel.class == type) {
			return Privlevel.MEMBERS;
		}
		log.debug("no dummy value for type '" + type.getName() + "'");
		return null;
	}
}
