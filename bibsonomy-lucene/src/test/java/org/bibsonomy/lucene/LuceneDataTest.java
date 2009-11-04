package org.bibsonomy.lucene;

import static org.junit.Assert.assertEquals;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.lucene.param.LuceneData;
import org.bibsonomy.lucene.param.RecordType;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.util.ExceptionUtils;
import org.junit.Ignore;
import org.junit.Test;

public class LuceneDataTest {
	private static final Log log = LogFactory.getLog(LuceneDataTest.class);

	private static final Object FLD_ADDRESS = "address";
	private static final String FLD_AUTHOR = "author";
	private static final Object FLD_GROUP = "group";
	private static final Object FLD_YEAR = "year";
	private static final String FLD_TAGS = "tas";
	private static final String FLD_TITLE = "title";

	
	
	/**
	 * tests that userName is always set to lowercase
	 * 
	 */
	@Test
	public void bookmarkStrings() {

		log.info ("Testing bookmarkStrings");
		LuceneData lD = new LuceneData(RecordType.Bookmark);
		lD.setBookmarkContentId("12345");
		lD.setBookmarkDate("2009-08-07 12:12:12");
		lD.setBookmarkDescription("Beschreibung");
		lD.setBookmarkExt("Ext");
		lD.setBookmarkGroup("public");
		lD.setBookmarkIntrahash("123456789");
		lD.setBookmarkTas("das ist ein test");
		lD.setBookmarkUrl("http://www.bibsonomy.org/");
		lD.setBookmarkUsername("someuser");
		
		HashMap<String,String> hM = new HashMap<String,String>();
		hM.put("content_id", "12345");
		hM.put("date", "2009-08-07 12:12:12");
		hM.put("desc", "Beschreibung");
		hM.put("ext", "Ext");
		hM.put("group", "public");
		hM.put("intrahash", "123456789");
		hM.put(FLD_TAGS, "das ist ein test");
		hM.put("url", "http://www.bibsonomy.org/");
		hM.put("user_name", "someuser");
		
		assertEquals(lD.getContent(), hM);
		
	}	
	

	/**
	 * tests that userName is always set to lowercase
	 * 
	 */
	@Test
	public void bookmarkPost() {
		
		log.info ("Testing bookmarkPost");

		LuceneData lD = new LuceneData(RecordType.Bookmark);
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		
		// Data for testig and filling post
		HashMap<String,String> hM = new HashMap<String,String>();
		hM.put("content_id", "12345");
		hM.put("date", dateFormat.format(date));
		hM.put("desc", "Titel");
		hM.put("ext", "Ext");
		//hM.put("group", "public");
		hM.put("group", "bibsonomy,public,puma");  // use alphabetical order!
		hM.put("intrahash", "123456789");
		hM.put(FLD_TAGS, "das ein ist test");  // use alphabetical order!
		hM.put("url", "http://www.bibsonomy.org/");
		hM.put("user_name", "someuser");	// use lowercase letters!
		
		
		// build a bookmark post and put it into LuceneData
		Bookmark bookmark = new Bookmark();
	
		Post<Bookmark> postBookmark = new Post<Bookmark>();

		bookmark.setUrl(hM.get("url"));
		bookmark.setTitle(hM.get("desc")); // Zuordnung setTitle und desc ist richtig!
	
		for (String g : hM.get("group").split(",")) {
			postBookmark.addGroup(g);
		}
	
		for (String tag : hM.get(FLD_TAGS).split(" ")) {
			postBookmark.addTag(tag);
		}
	
		bookmark.setIntraHash(hM.get("intrahash"));
		bookmark.setInterHash(hM.get("intrahash")); // same as intrahash
	
		postBookmark.setContentId(Integer.parseInt(hM.get("content_id")));
		//bookmark.setCount(42);
	
		postBookmark.setDate(date);
		postBookmark.setDescription(hM.get("ext")); // Zuordnung setDescription und ext ist richtig!
		postBookmark.setResource(bookmark);
		postBookmark.setUser(new User(hM.get("user_name")));
		
		lD.setPostBookmark(postBookmark);

		System.out.println("hM: " + hM.toString());
		System.out.println("lD: " + lD.getContent().toString());
		
		Map <String,String> lDhM = lD.getContent();
		for ( String k : hM.keySet())
		{
			log.info ("...testing key "+k+": "+lDhM.get(k)+" = "+hM.get(k));
			assertEquals(lDhM.get(k), hM.get(k));
		}
		
	}	
	
	@Test
	public void bibTexPost() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Post<BibTex> testPost = generateBibTexTestPost(
				"testTitle", "testTag", "testAuthor", "testUser", 
				new Date(System.currentTimeMillis()), GroupID.PUBLIC);

		
		Map<String,String> contentMap = LuceneData.extractPost(testPost);
		
		//--------------------------------------------------------------------
		// compare some elements
		//--------------------------------------------------------------------
		// title
		assertEquals(testPost.getResource().getTitle(), contentMap.get(FLD_TITLE));
		// tags
		for( Tag tag : testPost.getTags() ) {
			String tagName = tag.getName();
			
			assertEquals(true, contentMap.get(FLD_TAGS).contains(tagName));
		}
		// author
		assertEquals(testPost.getResource().getAuthor(), contentMap.get(FLD_AUTHOR));
		// year
		assertEquals(testPost.getResource().getYear(), contentMap.get(FLD_YEAR));
		// address
		assertEquals(testPost.getResource().getAddress(), contentMap.get(FLD_ADDRESS));
		// groups
		for( Group group : testPost.getGroups() ) {
			String tagName = group.getName();
			
			assertEquals(true, contentMap.get(FLD_GROUP).contains(tagName));
		}
	}
	
	/**
	 * generate a BibTex Post, can't call setBeanPropertiesOn() because private
	 * so copy & paste the setBeanPropertiesOn() into this method
	 * 
	 * GroupID.PUBLIC
	 */
	private static Post <BibTex> generateBibTexTestPost(
			String titleText, String tagName, 
			String authorName, 
			String userName, Date postDate, GroupID groupID) {
		
		final Post<BibTex> post = new Post<BibTex>();

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
	private static Post <Bookmark> generateBookmarkTestPost(
			String titleText, String tagName, 
			String authorName, 
			String userName, Date postDate, GroupID groupID	
			) {
		
		final Post<Bookmark> post = new Post<Bookmark>();

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
