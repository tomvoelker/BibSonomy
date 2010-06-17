package org.bibsonomy.lucene.util;

import static org.bibsonomy.lucene.util.LuceneBase.FLD_ADDRESS;
import static org.bibsonomy.lucene.util.LuceneBase.FLD_AUTHOR;
import static org.bibsonomy.lucene.util.LuceneBase.FLD_GROUP;
import static org.bibsonomy.lucene.util.LuceneBase.FLD_TAS;
import static org.bibsonomy.lucene.util.LuceneBase.FLD_TITLE;
import static org.bibsonomy.lucene.util.LuceneBase.FLD_YEAR;
import static org.junit.Assert.assertEquals;

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
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.CommonModelUtils;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author fei
 * @version $Id$
 */
public class LucenePostConverterTest {
	private static LuceneResourceConverter<BibTex> bibTexConverter;
	private static LuceneResourceConverter<Bookmark> bookmarkConverter;
	
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUp() {
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
		final LucenePost<Bookmark> refPost = generateBookmarkTestPost("testTitle", "testTag", "testUser", new Date(System.currentTimeMillis()), GroupID.PUBLIC);
		
		final Document doc = bookmarkConverter.readPost(refPost);
		
		final Post<Bookmark> testPost = bookmarkConverter.writePost(doc); 

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
		final LucenePost<BibTex> refPost = generateBibTexTestPost( "testTitle", "testTag", "testAuthor", "testUser", new Date(System.currentTimeMillis()), GroupID.PUBLIC);
		final Document doc = bibTexConverter.readPost(refPost);
		
		final Post<BibTex> testPost = bibTexConverter.writePost(doc); 

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
	public void bibTexPost() {
		final LucenePost<BibTex> testPost = generateBibTexTestPost("testTitle", "testTag", "testAuthor", "testUser", new Date(System.currentTimeMillis()), GroupID.PUBLIC);
		
		final Document postDoc = bibTexConverter.readPost(testPost);
		
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
	private static LucenePost<BibTex> generateBibTexTestPost(final String titleText, final String tagName, final String authorName, final String userName, final Date postDate, final GroupID groupID) {
		final LucenePost<BibTex> post = createEmptyPost(BibTex.class, tagName, groupID, postDate, userName);
		
		final User user = new User();
		CommonModelUtils.setBeanPropertiesOn(user);
		user.setName(userName);
		user.setRole(Role.NOBODY);
		post.setUser(user);
		final BibTex resource = new BibTex();
		CommonModelUtils.setBeanPropertiesOn(resource);
		resource.setCount(0);		
		resource.setEntrytype("inproceedings");
		resource.setAuthor("MegaMan and Lucene GigaWoman "+authorName);
		resource.setEditor("Peter Silie "+authorName);
		resource.setTitle("bibtex insertpost test");
		
		resource.setTitle("title "+ (Math.round(Math.random()*Integer.MAX_VALUE))+" "+titleText); // TODO: random with seed
		resource.setYear("test year");
		resource.setJournal("test journal");
		resource.setBooktitle("test booktitle");
		resource.setVolume("test volume");
		resource.setNumber("test number");
		resource.setScraperId(-1);
		resource.setType("2");
		resource.recalculateHashes();
		post.setResource(resource);
		return post;
	}

	private static <T extends Resource> LucenePost<T> createEmptyPost(@SuppressWarnings("unused") final Class<T> rClass, final String tagName, final GroupID groupID, Date postDate, String userName) {
		final LucenePost<T> post = new LucenePost<T>();
		post.setContentId((int)Math.floor(Math.random()*Integer.MAX_VALUE)); // TODO: random with seed

		final Group group = new Group(groupID);
		post.getGroups().add(group);

		post.setTags(ModelUtils.getTagSet("tag1", "tag2", tagName));
		
		post.setContentId(null);
		post.setDescription("luceneTestPost");
		post.setDate(postDate);
		
		final User user = new User();
		CommonModelUtils.setBeanPropertiesOn(user);
		user.setName(userName);
		user.setRole(Role.NOBODY);
		post.setUser(user);
		
		return post;
	}

	/**
	 * generate a Bookmark Post, can't call setBeanPropertiesOn() because private
	 * so copy & paste the setBeanPropertiesOn() into this method
	 */
	private static LucenePost<Bookmark> generateBookmarkTestPost(final String titleText, final String tagName, final String userName, final Date postDate, final GroupID groupID) {
		final LucenePost<Bookmark> post = createEmptyPost(Bookmark.class, tagName, groupID, postDate, userName);
		
		final Bookmark resource = new Bookmark();
		resource.setCount(0);
		resource.setTitle("test"+(Math.round(Math.random()*Integer.MAX_VALUE))+" "+titleText); // TODO: random with seed
		resource.setUrl("http://www.test"+(Math.round(Math.random()*Integer.MAX_VALUE))+"url.org"); // TODO: random with seed
		resource.recalculateHashes();
		
		post.setResource(resource);
		return post;
	}
}
