package org.bibsonomy.lucene;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.bibsonomy.lucene.param.LuceneData;
import org.bibsonomy.lucene.param.RecordType;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class LuceneDataTest {

	private static final Logger log       = Logger.getLogger(LuceneDataTest.class);
	
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
		hM.put("tas", "das ist ein test");
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
		hM.put("tas", "das ein ist test");  // use alphabetical order!
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
	
		for (String tag : hM.get("tas").split(" ")) {
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
		
		HashMap <String,String> lDhM = lD.getContent();
		for ( String k : hM.keySet())
		{
			log.info ("...testing key "+k+": "+lDhM.get(k)+" = "+hM.get(k));
			assertEquals(lDhM.get(k), hM.get(k));
		}
		
	}	
	
}
