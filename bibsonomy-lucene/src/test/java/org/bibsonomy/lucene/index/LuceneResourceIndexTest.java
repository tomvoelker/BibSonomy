package org.bibsonomy.lucene.index;

import static org.junit.Assert.assertEquals;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
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
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.lucene.util.LucenePostConverter;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.util.ExceptionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author fei
 */
public class LuceneResourceIndexTest {
	private static final Log log = LogFactory.getLog(LuceneUpdateManagerTest.class);

	@Before
	public void setUp() {
		// bind datasource access via JNDI
		JNDITestDatabaseBinder.bind();
	}
	
	@After
	public void tearDown() {
		JNDITestDatabaseBinder.unbind();
	}
	
	@Test
	public void testCache() {
		LucenePost<Bookmark> bmPost = generateBookmarkDatabaseManagerTestPost();
		LucenePost<BibTex> bibPost  = generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
		
		bmPost.setContentId(0);
		bibPost.setContentId(0);
		
		Document bmDoc  = LucenePostConverter.readPost(bmPost);
		Document bibDoc = LucenePostConverter.readPost(bibPost);
		
		LuceneResourceIndex<Bookmark> bmIndex = LuceneBookmarkIndex.getInstance();
		LuceneResourceIndex<BibTex> bibIndex  = LuceneBibTexIndex.getInstance();
		
		bmIndex.insertDocument(bmDoc);
		bmIndex.insertDocument(bmDoc);
		bibIndex.insertDocument(bibDoc);
		bibIndex.insertDocument(bibDoc);
		
		assertEquals(1, bmIndex.getPostsToInsert().size());
		assertEquals(1, bibIndex.getPostsToInsert().size());
		
		for( int i=1; i<50; i++ ) {
			bmPost.setContentId(i);
			bibPost.setContentId(i);

			bmIndex.insertDocument(LucenePostConverter.readPost(bmPost));
			bibIndex.insertDocument(LucenePostConverter.readPost(bibPost));
		}

		assertEquals(50, bmIndex.getPostsToInsert().size());
		assertEquals(50, bibIndex.getPostsToInsert().size());
	}
	
	/**
	 * generate a BibTex Post, can't call setBeanPropertiesOn() because private
	 * so copy & paste the setBeanPropertiesOn() into this method
	 */
	private LucenePost <BibTex> generateBibTexDatabaseManagerTestPost(GroupID groupID) {
		
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
		setBeanPropertiesOn(user);
		user.setName("testuser1");
		user.setRole(Role.NOBODY);
		LucenePost.setUser(user);
		final BibTex resource;

		
		final BibTex bibtex = new BibTex();
		this.setBeanPropertiesOn(bibtex);
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
		setBeanPropertiesOn(user);
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
	
	/**
	 * Calls every setter on an object and fills it wiht dummy values.
	 */
	private void setBeanPropertiesOn(final Object obj) {
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
