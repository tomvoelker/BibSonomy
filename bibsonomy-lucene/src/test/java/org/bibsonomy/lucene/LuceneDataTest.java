package org.bibsonomy.lucene;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import org.junit.Test;


public class LuceneDataTest {

	
	/**
	 * tests that userName is always set to lowercase
	 * 
	 */
	@Test
	public void bookmark() {

		LuceneData lD = new LuceneData();
		lD.setContentType(RecordType.Bookmark);
		lD.setField("content_id", "12345");
		lD.setField("date", "2009-08-07 12:12:12");
		lD.setField("desc", "Beschreibung");
		lD.setField("ext", "Ext");
		lD.setField("group", "public");
		lD.setField("intrahash", "123456789");
		lD.setField("tas", "das ist ein test");
		lD.setField("url", "http://www.bibsonomy.org/");
		lD.setField("user_name", "someUser");
		
		HashMap<String,String> hM = new HashMap<String,String>();
		hM.put("content_id", "12345");
		hM.put("date", "2009-08-07 12:12:12");
		hM.put("desc", "Beschreibung");
		hM.put("ext", "Ext");
		hM.put("group", "public");
		hM.put("intrahash", "123456789");
		hM.put("tas", "das ist ein test");
		hM.put("url", "http://www.bibsonomy.org/");
		hM.put("user_name", "someUser");
		
		assertEquals(lD.getContent(), hM);
		
	}	
	
}
