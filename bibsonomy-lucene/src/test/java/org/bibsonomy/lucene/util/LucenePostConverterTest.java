package org.bibsonomy.lucene.util;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.CommonModelUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * @author fei
 * @version $Id$
 */
public class LucenePostConverterTest extends LuceneBase {
	LuceneResourceConverter<BibTex> bibTexConverter;
	LuceneResourceConverter<Bookmark> bookmarkConverter;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		// bind datasource access via JNDI
		JNDITestDatabaseBinder.bind();
		LuceneBase.initRuntimeConfiguration();
		
		// initialize
		Map<String,Map<String,Object>> postPropertyMap;

		postPropertyMap = (Map<String, Map<String, Object>>) LuceneSpringContextWrapper.getBeanFactory().getBean("bibTexPropertyMap");
		bibTexConverter = new LuceneBibTexConverter();
		bibTexConverter.setPostPropertyMap(postPropertyMap);
		
		postPropertyMap = (Map<String, Map<String, Object>>) LuceneSpringContextWrapper.getBeanFactory().getBean("bookmarkPropertyMap");
		bookmarkConverter = new LuceneBookmarkConverter();
		bookmarkConverter.setPostPropertyMap(postPropertyMap);
		
	}
	
	@Test
	public void writeBookmarkPost() {
		final LucenePost<Bookmark> refPost =	generateBookmarkTestPost(
			"testTitle", "testTag", "testAuthor", "testUser", 
			new Date(System.currentTimeMillis()), GroupID.PUBLIC);
		
		final Document doc = this.bookmarkConverter.readPost(refPost);
		
		final Post<Bookmark> testPost = this.bookmarkConverter.writePost(doc); 

		//--------------------------------------------------------------------
		// compare some elements
		//--------------------------------------------------------------------
		// title
		assertEquals(refPost.getResource().getTitle(), testPost.getResource().getTitle());

		// url
		assertEquals(refPost.getResource().getUrl(), testPost.getResource().getUrl());

		// tags
		for( final Tag tag : refPost.getTags() ) {
			assertEquals(true, testPost.getTags().contains(tag));
		}
		// hashes
		assertEquals(refPost.getResource().getIntraHash(), testPost.getResource().getIntraHash());
		assertEquals(refPost.getResource().getInterHash(), testPost.getResource().getInterHash());

		// groups
		for( final Group group : refPost.getGroups() ) {
			assertEquals(true, testPost.getGroups().contains(group));
		}
	}
	
	@Test
	public void writeBibTexPost() {
		final LucenePost<BibTex> refPost =	generateBibTexTestPost(
			"testTitle", "testTag", "testAuthor", "testUser", 
			new Date(System.currentTimeMillis()), GroupID.PUBLIC);
		
		final Document doc = this.bibTexConverter.readPost(refPost);
		
		final Post<BibTex> testPost = this.bibTexConverter.writePost(doc); 

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
		for( final Tag tag : refPost.getTags() ) {
			assertEquals(true, testPost.getTags().contains(tag));
		}
		// hashes
		assertEquals(refPost.getResource().getIntraHash(), testPost.getResource().getIntraHash());
		assertEquals(refPost.getResource().getInterHash(), testPost.getResource().getInterHash());
		// groups
		for( final Group group : refPost.getGroups() ) {
			assertEquals(true, testPost.getGroups().contains(group));
		}
	}
	
	
	@Test
	public void bibTexPost() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		final LucenePost<BibTex> testPost = generateBibTexTestPost(
				"testTitle", "testTag", "testAuthor", "testUser", 
				new Date(System.currentTimeMillis()), GroupID.PUBLIC);

		
		final Document postDoc = this.bibTexConverter.readPost(testPost);
		
		//--------------------------------------------------------------------
		// compare some elements
		//--------------------------------------------------------------------
		// title
		assertEquals(testPost.getResource().getTitle(), postDoc.get(FLD_TITLE));
		// tags
		for( final Tag tag : testPost.getTags() ) {
			final String tagName = tag.getName();
			
			assertEquals(true, postDoc.get(FLD_TAS).contains(tagName));
		}
		// author
		assertEquals(testPost.getResource().getAuthor(), postDoc.get(FLD_AUTHOR));
		// year
		assertEquals(testPost.getResource().getYear(), postDoc.get(FLD_YEAR));
		// address
		assertEquals(testPost.getResource().getAddress(), postDoc.get(FLD_ADDRESS));
		// groups
		for( final Group group : testPost.getGroups() ) {
			final String tagName = group.getName();
			
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
			final String titleText, final String tagName, 
			final String authorName, 
			final String userName, final Date postDate, final GroupID groupID) {
		
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
		CommonModelUtils.setBeanPropertiesOn(user);
		user.setName(userName);
		user.setRole(Role.NOBODY);
		post.setUser(user);
		final BibTex resource;

		
		final BibTex bibtex = new BibTex();
		CommonModelUtils.setBeanPropertiesOn(bibtex);
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
			final String titleText, final String tagName, 
			final String authorName, 
			final String userName, final Date postDate, final GroupID groupID	
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
		CommonModelUtils.setBeanPropertiesOn(user);
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
}
