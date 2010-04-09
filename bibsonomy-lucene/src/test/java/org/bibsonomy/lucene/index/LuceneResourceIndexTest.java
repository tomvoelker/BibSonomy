package org.bibsonomy.lucene.index;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.lucene.util.LuceneBibTexConverter;
import org.bibsonomy.lucene.util.LuceneBookmarkConverter;
import org.bibsonomy.lucene.util.LuceneResourceConverter;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.CommonModelUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author fei
 */
public class LuceneResourceIndexTest {
	private LuceneResourceConverter<BibTex> bibTexConverter;
	private LuceneResourceConverter<Bookmark> bookmarkConverter;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		// bind datasource access via JNDI
		JNDITestDatabaseBinder.bind();
		
		// initialize
		Map<String,Map<String,Object>> postPropertyMap;

		postPropertyMap = (Map<String, Map<String, Object>>) LuceneSpringContextWrapper.getBeanFactory().getBean("bibTexPropertyMap");
		bibTexConverter = new LuceneBibTexConverter();
		bibTexConverter.setPostPropertyMap(postPropertyMap);
		
		postPropertyMap = (Map<String, Map<String, Object>>) LuceneSpringContextWrapper.getBeanFactory().getBean("bookmarkPropertyMap");
		bookmarkConverter = new LuceneBookmarkConverter();
		bookmarkConverter.setPostPropertyMap(postPropertyMap);
	}
	
	@After
	public void tearDown() {
		JNDITestDatabaseBinder.unbind();
	}
	
	@Test
	public void testCache() {
		final LucenePost<Bookmark> bmPost = generateBookmarkDatabaseManagerTestPost();
		final LucenePost<BibTex> bibPost  = generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
		
		bmPost.setContentId(0);
		bibPost.setContentId(0);
		
		final Document bmDoc  = this.bookmarkConverter.readPost(bmPost);
		final Document bibDoc = this.bibTexConverter.readPost(bibPost);
		
		final LuceneResourceIndex<Bookmark> bmIndex = new LuceneBookmarkIndex(0);
		final LuceneResourceIndex<BibTex> bibIndex  = new LuceneBibTexIndex(0);
		
		bmIndex.insertDocument(bmDoc);
		bmIndex.insertDocument(bmDoc);
		bibIndex.insertDocument(bibDoc);
		bibIndex.insertDocument(bibDoc);
		
		assertEquals(1, bmIndex.getPostsToInsert().size());
		assertEquals(1, bibIndex.getPostsToInsert().size());
		
		for( int i=1; i<50; i++ ) {
			bmPost.setContentId(i);
			bibPost.setContentId(i);

			bmIndex.insertDocument(this.bookmarkConverter.readPost(bmPost));
			bibIndex.insertDocument(this.bibTexConverter.readPost(bibPost));
		}

		assertEquals(50, bmIndex.getPostsToInsert().size());
		assertEquals(50, bibIndex.getPostsToInsert().size());
	}
	
	/**
	 * generate a BibTex Post, can't call setBeanPropertiesOn() because private
	 * so copy & paste the setBeanPropertiesOn() into this method
	 */
	private LucenePost <BibTex> generateBibTexDatabaseManagerTestPost(final GroupID groupID) {
		
		final LucenePost<BibTex> LucenePost = new LucenePost<BibTex>();

		final Group group = new Group(groupID);
	
		LucenePost.getGroups().add(group);

		Tag tag = new Tag();
		tag.setName("tag1");
		LucenePost.getTags().add(tag);
		tag = new Tag();
		tag.setName("tag2");
		LucenePost.getTags().add(tag);
		tag = new Tag();
		tag.setName("testTag");
		LucenePost.getTags().add(tag);

		LucenePost.setContentId(null); // will be set in storePost()
		LucenePost.setDescription("luceneTestPost");
		LucenePost.setDate(new Date(System.currentTimeMillis()));
		final User user = new User();
		CommonModelUtils.setBeanPropertiesOn(user);
		user.setName("testuser1");
		user.setRole(Role.NOBODY);
		LucenePost.setUser(user);
		final BibTex resource;

		
		final BibTex bibtex = new BibTex();
		CommonModelUtils.setBeanPropertiesOn(bibtex);
		bibtex.setCount(0);		
		bibtex.setEntrytype("inproceedings");
		bibtex.setAuthor("MegaMan and Lucene GigaWoman");
		bibtex.setEditor("Peter Silie");
		bibtex.setTitle("bibtex insertpost test");
		resource = bibtex;
		
		String title, year, journal, booktitle, volume, number = null;
		title = "title "+ (Math.round(Math.random()*Integer.MAX_VALUE));
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
		LucenePost.setResource(resource);
		return LucenePost;
	}
	
	/**
	 * generate a Bookmark LucenePost, can't call setBeanPropertiesOn() because private
	 * so copy & paste the setBeanPropertiesOn() into this method
	 */
	private LucenePost <Bookmark> generateBookmarkDatabaseManagerTestPost() {
		
		final LucenePost<Bookmark> LucenePost = new LucenePost<Bookmark>();

		final Group group = new Group();
		group.setDescription(null);
		group.setName("public");
		group.setGroupId(GroupID.PUBLIC.getId());
		LucenePost.getGroups().add(group);

		Tag tag = new Tag();
		tag.setName("tag1");
		LucenePost.getTags().add(tag);
		tag = new Tag();
		tag.setName("tag2");
		LucenePost.getTags().add(tag);

		LucenePost.setContentId(null); // will be set in storePost()
		LucenePost.setDescription("Some description");
		LucenePost.setDate(new Date());
		final User user = new User();
		CommonModelUtils.setBeanPropertiesOn(user);
		user.setName("testuser1");
		user.setRole(Role.NOBODY);
		LucenePost.setUser(user);
		final Bookmark resource;

		
		final Bookmark bookmark = new Bookmark();
		bookmark.setCount(0);
		//bookmark.setIntraHash("e44a7a8fac3a70901329214fcc1525aa");
		bookmark.setTitle("test"+(Math.round(Math.random()*Integer.MAX_VALUE)));
		bookmark.setUrl("http://www.testurl.orgg");
		bookmark.recalculateHashes();
		resource = bookmark;
		
		LucenePost.setResource(resource);
		return LucenePost;
	}
}
